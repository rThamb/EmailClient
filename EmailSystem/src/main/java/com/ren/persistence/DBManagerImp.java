/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.ren.persistence;

import java.sql.*;
import com.ren.emailsystem.beans.*;
import java.io.*;
import jodd.mail.*;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
*
* This class define functionality of Database Manager. In charge of reading ,
* writing and updating record used by the email client
* 
* @author Renuchan
*/
public class DBManagerImp implements DBManageable
{

    private Configuration config; 
    private String url;
    private String user; 
    private String pass; 
    private final Logger log = LoggerFactory.getLogger(getClass().getName());


    public DBManagerImp(Configuration config)
    {
        this.config = config;      
        this.url =  "jdbc:mysql://" + config.getDbAddress() + ":"+ config.getDbPort() +"/cs1437641";
        this.user = config.getDbUser();
        this.pass = config.getDbPass();     
    }
    
    
    /**
     * Deletes the specified folder from the database.
     * 
     * @param folderName    Name of the folder to be deleted.
     * @return              Number of records deleted.
     * @throws SQLException 
     */
    public int deleteFolder(String folderName) throws SQLException {
        int result;
	String query = "DELETE FROM EmailSys_Folders WHERE folder_name = ? AND owner = ?;";
        
	try (Connection connection = DriverManager.getConnection(url,user,pass);
            PreparedStatement ps = connection.prepareStatement(query);) {
		ps.setString(1, folderName);
                ps.setString(2, config.getUserEmail());
		result = ps.executeUpdate();
	}
	log.info("Deleted folder: " + folderName);
	return result;
    }

    /**
     * Method in charge of writing a CustomEmail data into the database
     *
     * @param email 	Email containing the information to write in DB. 
     */
    public int writeEmail(CustomEmail email)throws SQLException
    {
        log.info("Inserting an Email");
        if(!checkIfSenderExists())
            addNewAccount();
        
        try(Connection con = DriverManager.getConnection(url,user,pass))
        {
            String owner = config.getUserEmail();
            String fName = email.getFoldername();
            
            int folderID = getFolderId(owner , fName);
            
            if(folderID == -1){
                createFolder(email.getFoldername());
                folderID = getFolderId(owner , fName);
            }
                    
            String query = "INSERT INTO EmailSys_Emails(person_to, person_from, cc, subject, text, html, receivedDate, folder_id)"
                    + "VALUES (?,?,?,?,?,?,?,?);";

            PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1,getAllEmails(email.getTo()));
            stmt.setString(2,email.getFrom().getEmail());
            stmt.setString(3,getAllEmails(email.getCc()));
            stmt.setString(4,email.getSubject());
            stmt.setString(5,getText(email.getAllMessages()));
            stmt.setString(6, getHtml(email.getAllMessages()));
            
            
            //use timestamp
            if(email.getIsReceived())
                stmt.setDate(7,new Date(email.getReceivedDate().getTime()));
            else 
                stmt.setDate(7,null);
            
            stmt.setInt(8,folderID);
            
            int rowAffect = stmt.executeUpdate();
            if(rowAffect == 1 )
            {
                int id = getEmailId(stmt);
                email.setId(id);
                if(id != -1)
                    writeAttachment(email, id); 
            }
            
            log.info("Finished Inserting an Email");
            return rowAffect;
        }
    }

    private void writeAttachment(CustomEmail email, int emailID)throws SQLException 
    {
        List<EmailAttachment> attachments = email.getAttachments();

        if(attachments != null)
        {        
            try(Connection con = DriverManager.getConnection(url,user,pass))
            {
                String query = "INSERT INTO EmailSys_Attachments (emailID, file_data,name_file) VALUES (?,?,?);"; 
                for(EmailAttachment attach : attachments)
                {
                    byte[] fileStream = attach.toByteArray();
                    String name = attach.getName();

                    PreparedStatement stmt = con.prepareStatement(query);
                    
                    stmt.setInt(1, emailID);
                    stmt.setBytes(2, fileStream);
                    stmt.setString(3, name);
                    
                    stmt.executeUpdate();
                    
                }
            }
        }
    }
    private int getEmailId(PreparedStatement stmt) throws SQLException
    {
       int id = -1;
       
       ResultSet rs = stmt.getGeneratedKeys();
       
       if(rs.next())
           id = rs.getInt(1);
        return id;
    }
    
    
    //----------------- Methods Above Involved in Writing to DB
    
    
    
    /**
     * Method in charge of updating an emails folder 
     *
     * @param id            Emails id that needs to be updated  
     * @param foldername    folder name that the was email moved to
     * @return              rows effected
     */
    @Override
    public int updateEmail(int id, String foldername)throws SQLException {
        
        log.info("Moving Email with Id" + id + " To " + foldername);
        //check if the file exists
        int changed_FolderID = getFolderId(config.getUserEmail(), foldername.toLowerCase());
        
        if(changed_FolderID != -1)
        {
            String query = "UPDATE EmailSys_Emails SET folder_id = ? WHERE id = ?;";
            try(Connection con = DriverManager.getConnection(url, user,pass); 
                PreparedStatement stmt = con.prepareStatement(query))
            {
                stmt.setInt(1, changed_FolderID);
                stmt.setInt(2, id);
                return stmt.executeUpdate();
            }
        }
        else
            throw new SQLException("Folder: " + foldername + " doesn't exist"); 
        
        
    }

    
    
    /**
     * Method will load all emails belonging to the current user, in the specified folder
     *
     * @param foldername    folder name email are stored in
     * @return              list of all emails in that folder
     */
    @Override
    public ArrayList<CustomEmail> retrieveEmail(String foldername)throws SQLException 
    {
       
        if(!checkIfSenderExists())
            addNewAccount();
        
        ArrayList<CustomEmail> emails = new ArrayList<CustomEmail>();

        String currentUser = config.getUserEmail();
        
        String query = "SELECT emailsys_emails.id, person_to,person_from, cc, subject,text, html, receivedDate FROM emailsys_emails " +
                "inner join emailsys_folders on emailsys_emails.folder_id = emailsys_folders.id " +
                "WHERE folder_name = ? AND owner = ?;";
        try(Connection con = DriverManager.getConnection(url, user,pass); 
                PreparedStatement stmt = con.prepareStatement(query))
        {
            stmt.setString(1, foldername);
            stmt.setString(2, currentUser);
            
            try(ResultSet rs = stmt.executeQuery())
            {
                while(rs.next())
                {
                    int id = rs.getInt(1);
                    String personTo = rs.getString(2);
                    String personFrom = rs.getString(3);
                    String cc = rs.getString(4);
                    String sub = rs.getString(5);
                    String text = rs.getString(6);
                    String html = rs.getString(7);
                    Timestamp ts = rs.getTimestamp(8);
                
                    CustomEmail mail = new CustomEmail();
                                     
                    getAttachmentsFromEmail(id, mail);
                    
                    mail.setId(id);
                    mail.to(personTo);
                    mail.from(personFrom);
                    mail.subject(sub);
                    mail.addText(text);
                    mail.addHtml(html);
                    mail.cc(cc);
                    mail.setReceivedDate(new Date(ts.getTime()));
                    mail.setFoldername(foldername);        
                                    
                    emails.add(mail);
                
                }
                
            }
        }
        
        return emails; 
    }
    
    /**
     * Retrieves all of the user's folders.
     * 
     * @return  List of all of the user's folders.
     * @throws SQLException 
     */
    @Override
    public ArrayList<String> getUsersFolders() throws SQLException {
        ArrayList<String> folders = new ArrayList<>(); 
        String query = "SELECT folder_name FROM emailsys_folders WHERE owner = ?";
        
        try(Connection connect = DriverManager.getConnection(url, user, pass); 
            PreparedStatement ps = connect.prepareStatement(query)){
            ps.setString(1, config.getUserEmail());               
            ResultSet rs = ps.executeQuery();
            
            while(rs.next())
                folders.add(rs.getString("folder_name"));
        }
        
        log.info("# of records found : " + folders.size()); 		
        return folders;
    }
    
    private void getAttachmentsFromEmail(int id, CustomEmail mail)throws SQLException
    {      
       String query = "select file_data, name_file from emailsys_attachments where emailID = ?;";   
       try(Connection con = DriverManager.getConnection(url,user,pass); 
               PreparedStatement stmt = con.prepareStatement(query))
       {
           stmt.setInt(1,id);
           
           try(ResultSet rs = stmt.executeQuery())
           {
               while(rs.next())
               {             
                   Blob fileData = rs.getBlob(1);
                   byte[] stream = fileData.getBytes(1, (int)fileData.length()); 
                   String name = rs.getString(2);
                   
                   //attach the attachment to the object                  
                   mail.attach(EmailAttachment.attachment().bytes(stream).setName(name).create());
                    
               }
           }
       }
    }
    
    

    //Helper method to preform additional work, used by the read and write functions  
    
    
    private String getText(List<EmailMessage> list)
    {
        StringBuilder info = new StringBuilder();
        
        for(EmailMessage mes : list)
        {
            if(mes.getMimeType().equalsIgnoreCase("text/plain"))
                info.append(mes.getContent());
        }
        
        return info.toString();
    }
    
    private String getHtml(List<EmailMessage> list)
    {
        StringBuilder info = new StringBuilder();
        
        for(EmailMessage mes : list)
        {
            if(mes.getMimeType().equalsIgnoreCase("text/html"))
                info.append(mes.getContent());
        }
        
        return info.toString();
    }
    
    private String getAllEmails(MailAddress[] list)
    {
        //email had no field
        if(list == null)
            return "";
              
        if (list.length == 0)
            return "";
        
        StringBuilder info = new StringBuilder("");
        
        for(MailAddress ele : list)
            info.append(ele.getEmail() + ",");
        
        String str = info.toString();
        
        return str.substring(0, str.length() - 1);
    }
    
    private boolean checkIfSenderExists()throws SQLException
    {

        String email = config.getUserEmail(); 

        try(Connection con = DriverManager.getConnection(url,user,pass))
        {
            String query = "SELECT emailAddress from EmailSys_Accounts WHERE emailAddress = ?;"; 

            PreparedStatement stmt = con.prepareStatement(query); 
            stmt.setString(1,email);

            ResultSet rs = stmt.executeQuery();

            return rs.next();

        }
    }
	
    private void addNewAccount()throws SQLException
    {
        String email = config.getUserEmail(); 
        String name = config.getUserName();
        String pwd = config.getUserEmailPwd();
        
        try(Connection con = DriverManager.getConnection(url,user,pass))
        {
            String query = "INSERT INTO EmailSys_Accounts VALUES(?,?,?);"; 

            PreparedStatement stmt = con.prepareStatement(query); 
            stmt.setString(1,email);
            stmt.setString(2,name);
            stmt.setString(3,pwd); 
            try{
            stmt.executeUpdate();
            }
            catch(SQLException e){}
        }

    }
	
    private int getFolderId(String owner, String fName)throws SQLException
    {
        try(Connection con = DriverManager.getConnection(url,user,pass))
        {
            String query = "SELECT id FROM EmailSys_Folders WHERE owner = ? AND folder_name = ?;"; 

            PreparedStatement stmt = con.prepareStatement(query); 
            stmt.setString(1,owner);
            stmt.setString(2,fName);

            ResultSet rs = stmt.executeQuery();

            if(rs.next())
                return rs.getInt("id"); 

        }
        return -1; // folder doesnt exist 
    }
    
    
    /**
     * Method will delete an email from the database 
     *
     * @param id    of the email to  
     */
    @Override
    public int deleteEmail(int id)throws SQLException
    {      
        String query = "DELETE FROM EmailSys_Emails WHERE id = ?;";
        try(Connection con = DriverManager.getConnection(url, user,pass); 
                PreparedStatement stmt = con.prepareStatement(query))
        {         
            stmt.setInt(1,id);
            return stmt.executeUpdate();
        }
        
    } 
    
    /**
     * Method will create a new folder for the user
     *
     * @param foldername    containing all setting info 
     * @throws SQLException
     */
    @Override
    public void createFolder(String foldername)throws SQLException
    {
        //check if the folder exists       
        int folder_Id = getFolderId(config.getUserEmail(), foldername.toLowerCase());
        
        if(folder_Id == -1)
        {
            String owner = config.getUserEmail();
            String query = "INSERT INTO EmailSys_Folders(folder_name,owner)VALUES(?,?);"; 

            try(Connection con = DriverManager.getConnection(url,user,pass); 
                    PreparedStatement stmt2 = con.prepareStatement(query))
            {
                stmt2.setString(1,foldername.toLowerCase());
                stmt2.setString(2,owner);
                stmt2.executeUpdate();
            }       
        }
        else 
            throw new SQLException("Folder exists");
    }
    
    /**
     * Method will change the databases settings 
     *
     * @param config    containing all setting info 
     */
    public void changeSettings(Configuration config)
    {
        this.config = config;      
        this.url =  config.getDbAddress();
        this.user = config.getDbUser();
        this.pass = config.getDbPass();
    } 
    
    // METHOD BELOW ARE FOR ENSURING THE APPLICATION ARE CONNECT TO 
    // A DATABASE BASE
    
    
    public void setUpDB()throws SQLException
    {
        String query = "SELECT * FROM EmailSys_Accounts"; 
        try(Connection con = DriverManager.getConnection(url,user,pass); 
                PreparedStatement stmt = con.prepareStatement(query);)
        {
           stmt.executeQuery();
        }
        catch(SQLException e)
        {
            seedDatabase();
        }

        if(!checkIfSenderExists())
            addNewAccount();     
    }
    
    private void seedDatabase() {
        
        int i =0; 
        
        final String seedDataScript = loadAsString("sqlScripts\\javaDBSetup.sql");
        try (Connection connection = DriverManager.getConnection(url, user, pass);) {
            for (String statement : splitStatements(new StringReader(seedDataScript), ";")) {
                    connection.prepareStatement(statement).execute();
            }
        } catch (SQLException e) {
                throw new RuntimeException("Failed seeding database", e);
        }
    }

    /**
     * The following methods support the seedDatabse method
     */
    private String loadAsString(final String path) {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
                        Scanner scanner = new Scanner(inputStream)) {
                return scanner.useDelimiter("\\A").next();
        } catch (IOException e) {
                throw new RuntimeException("Unable to close input stream.", e);
        }
    }

    private List<String> splitStatements(Reader reader, String statementDelimiter) {
        
        final BufferedReader bufferedReader = new BufferedReader(reader);
        final StringBuilder sqlStatement = new StringBuilder();
        final List<String> statements = new LinkedList<String>();
        try {
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || isComment(line)) {
                                continue;
                        }
                        sqlStatement.append(line);
                        if (line.endsWith(statementDelimiter)) {
                                statements.add(sqlStatement.toString());
                                sqlStatement.setLength(0);
                        }
                }
                return statements;
        } catch (IOException e) {
                throw new RuntimeException("Failed parsing sql", e);
        }
    }

    private boolean isComment(final String line) {
            return line.startsWith("--") || line.startsWith("//") || line.startsWith("/*");
    }

}
