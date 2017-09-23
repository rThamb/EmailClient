package com.ren.userinterface;


import com.ren.emailsystem.beans.CustomEmail;
import com.ren.emailsystem.EmailClient;
import com.ren.validation.EmailValidator;
import java.io.File;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jodd.mail.EmailMessage;
import jodd.mail.MailAddress;
import java.io.*; 
import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import jodd.mail.EmailAttachment;


/**
 *
 * This class is the Mail Details view's controller, it is in charge of handling 
 * all tasks related to displaying email contents and extracting content to send.
 * 
 * @author Renuchan
 */
public class MailDetailsController {
    
    @FXML 	
    private HTMLEditor mailContentBox;
    
    @FXML
    private Text toLbl; 
    @FXML
    private Text subLbl;
    @FXML
    private Text ccLbl;
    @FXML
    private Text bccLbl;
    @FXML
    private TextField toTF;
    @FXML
    private TextField subTF;
    @FXML
    private TextField ccTF;
    @FXML
    private TextField bccTF;
    @FXML
    private Button sendBTN;
    @FXML
    private Button attachBtn;
    @FXML
    private Button saveAttBtn;
    @FXML
    private Button replyBtn;
    
    private String attachPath = "";
    
    
    private CustomEmail email;
    private EmailClient mailSystem;
    private ResourceBundle rBundle;
    
    
    public MailDetailsController() {
        super();
    }
    
    
    public void setEmail(CustomEmail email)
    {
        this.email = email;
    }
    public CustomEmail getEmail()
    {
        return email;
    }
    
    
    /**
     * This method is called by the javafx framework. It will disable all the controls
     * on startup. 
     */
    @FXML
    public void initialize() 
    {
        sendBTN.setVisible(false);
        attachBtn.setVisible(false);
        saveAttBtn.setVisible(false);
        replyBtn.setVisible(false);
        bccTF.setEditable(false);
    }
    /**
     * This method will convert the emails contents into 1 string value
     * 
     * @param list          Object carrying the messages
     * @return              string containing the emails content
     */
    private String getContentString(List<EmailMessage> list)
    {
        //email had no content
        if(list == null)
            return ""; 
        StringBuilder info = new StringBuilder(""); 
        
        for(EmailMessage m : list)
       {   
           //will concat all content into 1 large string 
           info.append(m.getContent());     
       }
        
        return info.toString(); 
    }
    
    /**
     * This method will convert the email addresses into 1 string value
     * 
     * @param list          Object carrying the addresses
     * @return              string containing the email addresses
     */
    private String convertArrayToString(MailAddress[] list)
    {
        //email had no field
        if(list == null)
            return "";
              
        StringBuilder info = new StringBuilder("");
        
        for(MailAddress ele : list)
            info.append(ele.getEmail() + ",");
        
        String str = info.toString();
        
        if(str.equals(""))
            return "";
        else
            return str.substring(0, str.length() - 1);
    }
    
    
    /**
     * This method will display all the information in the clicked email
     * 
     */
    public void displayMail() {
       
        if(email != null)
        {
        toTF.setText(email.getFrom().getEmail());
        subTF.setText(email.getSubject());
        ccTF.setText(convertArrayToString(email.getCc()));
        
        sendBTN.setVisible(false);
        attachBtn.setVisible(false);
        bccTF.setEditable(false);
        replyBtn.setVisible(true);
               
        mailContentBox.setHtmlText(getContentString(email.getAllMessages()));
        
        if(email.getAttachments() != null)
        {
            if(email.getAttachments().size() != 0)      
                saveAttBtn.setVisible(true);       
            else
                saveAttBtn.setVisible(false);
        }
        else
            saveAttBtn.setVisible(false);
        
        }
    }
    /**
     * This method will allow the user to save the attachments of the email he
     * received 
     */
    @FXML
    public void saveFile()
    {
        Stage stage = new Stage();
        
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(rBundle.getString("dirSavFolder"));
        File selectedDirectory = chooser.showDialog(stage);
        
        if(selectedDirectory != null && selectedDirectory.exists())
        {
            String path = selectedDirectory.getAbsolutePath();
            writeToDir(path);
        }
        stage.close();
    }
    
    /**
     * This method will write the file onto the user local machine
     * 
     * @param path      Destination of the attachments on the users machine
     */
    private void writeToDir(String path)
    {       
        List<EmailAttachment> attachs = email.getAttachments();
        
        try{
            for(EmailAttachment att : attachs)
            {           
                try(FileOutputStream fos = new FileOutputStream(path + "\\" + att.getName()))
                {
                    fos.write(att.toByteArray());
                }
            }
        }
        catch(IOException e)
        {
            displayMsg(rBundle.getString("errorSave"));
            e.printStackTrace();
        }        
    }

    /**
     * This method clears the view of the composer mail when clicked
     */
    public void clearFields()
    {
        toTF.setText("");
        subTF.setText("");
        ccTF.setText("");
        bccTF.setText("");     
        mailContentBox.setHtmlText("");
    }
    
    /**
     * This method reveals button needed for sending emails
     * 
     */
    public void sendEmail()
    {
        sendBTN.setVisible(true);
        bccTF.setEditable(true);
        attachBtn.setVisible(true);
        saveAttBtn.setVisible(false);
        replyBtn.setVisible(false);
    }

    
    /**
     * This method will allows the user to select a file on their machine 
     * when button is clicked
     */
    @FXML
    public void attach()
    {
        attachPath = attachmentSelector();
    }
    private String attachmentSelector()
    {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(rBundle.getString("attachSelect"));
        File file = fileChooser.showOpenDialog(stage);   
        
        if(file != null && file.exists())
        {
            return file.getAbsolutePath();
        }
        
        return "";
    }

    /**
     * Allows the user to reply to a email they received
     */
    @FXML
    public void onReply()
    {
        toTF.setText(email.getFrom().getEmail());
        subTF.setText(email.getSubject());
        ccTF.setText("");
        bccTF.setText("");
        
        mailContentBox.setHtmlText("<br/><br/>--------------------<br/><br/>" + 
                getContentString(email.getAllMessages()));
        
        sendBTN.setVisible(true);
        attachBtn.setVisible(true);
        saveAttBtn.setVisible(false);
        replyBtn.setVisible(false);
     
    }
    
    
    /**
     * This method reads all the information provided by the user and sends 
     * the info to the receiver(s).
     * 
     */
    @FXML
    public void submitEmail()
    {
        createEmailMsg();
    }
    
    /**
     * This method will read all the information in the composer view
     */
    private void createEmailMsg()
    {
        String to = toTF.getText();
        String sub = subTF.getText();
        String cc = ccTF.getText();
        String bcc = bccTF.getText();
        
        String content = mailContentBox.getHtmlText();
        
        try{
            checkFields(to,sub,cc,bcc,content);
            mailSystem.sendEmail(to, sub, "", content, cc, bcc, "", attachPath);
            displayMsg(rBundle.getString("emailSentGood"));
        }
        catch(Exception e)
        {
            displayMsg(e.getMessage());
        }
    
        
    }
    
    /**
     * This method checks all the fields the user provided when sending an email 
     * 
     * @param to
     * @param sub
     * @param cc
     * @param bcc
     * @param content
     * @throws Exception                If any field is invalid
     */
    private void checkFields(String to, String sub, String cc, String bcc, String content)
            throws Exception 
    {
        EmailValidator checker = new EmailValidator();
        
        if(to != null)
        {
            if(to.equals(""))
               throw new IllegalArgumentException("Provide a to field");
            
            checker.checkEmails(to);
        }
        else
            throw new IllegalArgumentException("Provide a to field");
        
        if(cc != null && !cc.equals(""))
            checker.checkEmails(cc);
                       
        if(bcc != null && !bcc.equals(""))
            checker.checkEmails(bcc);
        
        if(content == null || content.equals(""))
            throw new IllegalArgumentException("Provide Content");
   
    }
    
    /**
     * Displays any messages during the runtime of the application
     * @param msg 
     */
    private void displayMsg(String msg)
    {
        Alert alert = new Alert(Alert.AlertType.NONE, msg, ButtonType.OK);
        alert.showAndWait();
        
        if (alert.getResult() == ButtonType.OK) {
            alert.close();
        }     
        
    }
    
    public void setResource(ResourceBundle bun)
    {
        rBundle = bun;
    }
    
    public void setEmailClient(EmailClient system)
    {
        this.mailSystem = system;
    }
    
}
