/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JunitTests.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import com.ren.persistence.*;
import com.ren.emailsystem.beans.Configuration;
import com.ren.emailsystem.beans.CustomEmail;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.*;
import jodd.mail.EmailAttachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import junit.framework.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
/**
 *
 * @author Renuchan
 */

@RunWith(Parameterized.class)
public class DbManagerTest {
    
    private DBManagerImp db; 
    private final Logger log = LoggerFactory.getLogger(getClass().getName());
    private Configuration con;
            
    private CustomEmail cus;
    private String foldername; 
    
    
    
    
    @Parameterized.Parameters 
    public static Collection<Object[]> data()
    {       
        String path1 = "attachmentsFile\\car1.jpg";  
     
        CustomEmail test1 = new CustomEmail();
      test1.to("sp33dycow86@gmail.com");
      test1.from("sp33dyrat86@gmail.com");
      test1.subject("HEy man what up");
      test1.addText("Life is great now");
      test1.addHtml("");
      test1.cc("master_doomknight@hotmail.com");
      test1.setReceivedDate(new java.util.Date());
      test1.setFoldername("sent");

      
      CustomEmail test2 = new CustomEmail();
      test2.to("sp33dycow86@gmail.com");
      test2.from("sp33dyrat86@gmail.com");
      test2.subject("LOL");
      test2.addText("REKT");
      test2.addHtml("");
      test2.cc("master_doomknight@hotmail.com");
      test2.setReceivedDate(new java.util.Date());
      test2.setFoldername("sent");
      
      CustomEmail test3 = new CustomEmail();
      test3.to("sp33dycow86@gmail.com");
      test3.from("sp33dyrat86@gmail.com");
      test3.subject("LOL");
      test3.addText("REKT");
      test3.addHtml("");
      test3.cc("master_doomknight@hotmail.com");
      test3.setReceivedDate(new java.util.Date());
      test3.setFoldername("sent");
      if(!path1.equals(""))
        test3.attach(EmailAttachment.attachment().file(path1)); 
      

           Object[] data1 = {test1, "trash"};
           Object[] data2 = {test2, "draft"};
           Object[] data3 = {test3, "spam"};

           Object[][] data = new Object[][]{data1,data2, data3};

        return Arrays.asList(data);
    }
    
    
    
    
    
    public DbManagerTest(CustomEmail mail, String folder) {
        
        this.cus = mail; 
        foldername = folder; 
        
    }
    
    @Before
    public void setUp(){
        
        Configuration con = new Configuration();
        
        con.setUserName("Jim");
        con.setUserEmail("sp33dycow86@gmail.com");
        con.setUserEmailPwd("Tester11");
        con.setDbAddress("jdbc:mysql://waldo2.dawsoncollege.qc.ca:3306/cs1437641");
        con.setDbPort(3306); 
        con.setDbUser("CS1437641");
        con.setDbPass("ionickja");
        
        this.con = con;  

        db = new DBManagerImp(con);          
        
        seedDatabase();
        
    }
    

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void TestWrite() {
            
       ArrayList<CustomEmail> list = null;
      int index = 0; 
      try{
        db.writeEmail(cus);
        list = db.retrieveEmail(cus.getFoldername());       
         
        int id = cus.getId();
        for(int i = 0; i < list.size(); i++)
        {
            if(id == list.get(i).getId())
                index = i;
        } 
        CustomEmail zz = list.get(index); 
        
        //impossible to do the task at the same time
        cus.setReceivedDate(zz.getReceivedDate()); 
        
      }catch(Exception e){
          log.info(e.getMessage() + "FAILED EXCEPTION");
      }

      

        assertEquals(cus, list.get(index));            
    }
    
    @Test
    public void TestUpdate()
    {
       //Assumes on email with id 1 exists 
       int expected = 1;
       int obtain = 0;
        try{
            obtain = db.updateEmail(1,"extras");
        }catch(SQLException e)
        {}      
        assertEquals(expected, obtain);       
    }
    
    
    
    @Test
    public void TestCreateFolder()
    {
        boolean assertStmt = false; 
        
        try{
            db.createFolder(foldername);
            assertStmt = true;
        }catch(SQLException e)
        {
            assertStmt = false; 
        }
       
        if(assertStmt)
        {
            String query = "SELECT folder_name, owner from EmailSys_Folders WHERE owner = ? AND folder_name = ?;"; 
        
            try(Connection connection = DriverManager.getConnection(con.getDbAddress(), con.getDbUser(), con.getDbPass()); 
                PreparedStatement stmt = connection.prepareStatement(query))
            {                
                stmt.setString(1,con.getUserEmail());
                stmt.setString(2, foldername);
                
                ResultSet rs = stmt.executeQuery();
                assertStmt = rs.next();
            }
            catch(SQLException e)
            {
                assertStmt = false; 
            }    
        }   

        assertTrue(assertStmt); 
       
    }
    
   @Test
    public void TestDelete()
    {
       //Assumes on email with id 1 exists 
       int expected = 1;
       int obtain = 0;
        try{
            obtain = db.deleteEmail(1);
        }catch(SQLException e)
        {}      
        assertEquals(expected, obtain);       
    }
    
    @Test 
    public void TestGetUsersFolders()
    {    
        String[] expected = {"sent", "extras"}; 
        boolean valid = true; 
        try{
            
            ArrayList<String> folders = new ArrayList<String>();
            
            folders = db.getUsersFolders();
            
            if(folders.size() == expected.length)
            {
                for(int i = 0; i < expected.length; i++)
                {
                    String a = folders.get(i);
                    String b = expected[i]; 
                    int k =0;
                    if(!(folders.get(i).equals(expected[i])))
                        valid = false; 
                }
                
            }      
        }
        catch(SQLException e)
        {
            valid = false;
        }
        
        assertTrue(valid);
    }
    

    
     // METHODS TO CREATE THE DATABASE
     private void seedDatabase() {
        
        final String seedDataScript = loadAsString("javaDBSetup.sql");
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://waldo2.dawsoncollege.qc.ca:3306/cs1437641", "CS1437641","ionickja")) {
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
