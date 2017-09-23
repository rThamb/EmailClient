/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JunitTests.EmailClientTests;

import java.util.Arrays;
import java.util.Collection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import com.ren.emailsystem.*;
import com.ren.emailsystem.beans.*;
import java.io.File;
import java.util.Date;
import jodd.mail.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Renuchan
 */
@RunWith(Parameterized.class)
public class MailModuleTest {
    
    private String to;
    private String subject; 
    private String text;            
    private String html; 
    private String cc; 
    private String bcc; 
    private String embedded;
    private String attachment;
    private String folder; 
    private String expectedAttachInfo;
    
    private CustomEmail mail; 
    
    private final Logger log = LoggerFactory.getLogger(getClass().getName());
    
    
    private EmailClient client; 
    
    
    @Parameterized.Parameters 
    public static Collection<Object[]> data()
    {       
        String path1 = "attachmentsFile\\car1.jpg";
        String path2 = "attachmentsFile\\car2.jpg";
        String path3 = path2 + "," + path1;
        String path4 = "attachmentsFile\\cheese.jpg";
        String path5 = path1 + "," + path4;
        String path6 = "attachmentsFile\\folder.zip";
        
        
        String exp1 = "car2.jpg,car1.jpg";
        String exp3 = "car2.jpg,car1.jpg";
        String exp4 = "car1.jpg,cheese.jpg";
        String exp5 = "folder.zip"; 
        
        
        String path1OfAttach = "attachmentsFile\\car1.jpg";
        String path2OfAttach = "attachmentsFile\\car2.jpg"; 
   
      
      CustomEmail test1 = new CustomEmail();
      test1.to("sp33dycow86@gmail.com");
      test1.from("sp33dyrat86@gmail.com");
      test1.subject("HEy man what up");
      test1.addText("Life is great now");
      test1.setReceivedDate(new Date());
      test1.setFoldername("inbox");
      test1.setIsReceived(true);
      
      CustomEmail test2 = new CustomEmail();
      test2.to("sp33dycow86@gmail.com");
      test2.from("sp33dyrat86@gmail.com");
      test2.subject("LOL");
      test2.addText("REKT");
      test2.cc("sp33dysend2@gmail.com");
      test2.setReceivedDate(new Date());
      test2.setFoldername("inbox");
      test2.setIsReceived(true);
      
      CustomEmail test3 = new CustomEmail();
      test3.to("sp33dycow86@gmail.com");
      test3.from("sp33dyrat86@gmail.com");
      test3.subject("LOL");
      test3.addText("REKT");
      test3.cc("sp33dysend2@gmail.com");
      test3.setReceivedDate(new Date());
      test3.setFoldername("inbox");
      test3.setIsReceived(true);
      
      if(!path1OfAttach.equals(""))
          test3.attach(EmailAttachment.attachment().file(path1OfAttach));
        
        
        Object[] data1 = {"sp33dycow86@gmail.com","Yo test",
            "Hello World",null,"","","",
            path3,
            "sent",exp1,test1};
        
        Object[] data2 = {"sp33dycow86@gmail.com","Yo test",
            "Hello World",null,null,null,null,
            null,
            "sent","",test2};
        
        Object[] data3 = {"sp33dycow86@gmail.com","Yo test",
            "Hello World","","sp33dycow86@gmail.com,sp33dyrat86@gmail.com","","",
            path3,
            "sent",exp3,test3};
        
        Object[] data4 = {"sp33dycow86@gmail.com","Yo test",
            "Hello World","<html><body><h1>My</h1><p>My first paragraph.</p></body></html>","","","",
            path5,
            "sent",exp4,test2};
        
        Object[] data5 = {"sp33dycow86@gmail.com,sp33dyrat86@gmail.com","Yo test",
            "Hello World","<html><body><h1>My</h1><p>My first paragraph.</p></body></html>","","","",
            "",
            "sent","",test1};      
        
        Object[] data6 = {"sp33dycow86@gmail.com,sp33dyrat86@gmail.com","Yo test",
            "Hello World","<html><body><h1>My</h1><p>My first paragraph.</p></body></html>","","",path1,
            path6,
            "sent",exp5,test3};  
        
        
        
        
        Object[][] data = new Object[][]{
                            data1,data2, data3,data4,data5,data6
                            };
        
        return Arrays.asList(data);
    }
    
    
    public MailModuleTest(String to, String subject, String text, 
            String html, String cc, String bcc, String embedded, 
            String attachment, String folder, String expectedAttachInfo, CustomEmail mail) 
    {
        this.to = to;
        this.subject = subject; 
        this.text = text;            
        this.html = html;
        this.cc = cc; 
        this.bcc = bcc; 
        this.embedded = embedded;
        this.attachment = attachment;
        this.folder = folder; 
        this.expectedAttachInfo = expectedAttachInfo;
        this.mail = mail; 
        
        Configuration file = new Configuration();
        
        file.setImapAddress("imap.gmail.com");
        file.setSmtpAddress("smtp.gmail.com");
        file.setUserEmail("sp33dycow86@gmail.com");
        file.setUserEmailPwd("Tester11");
        file.setImapPort(993);
        file.setSmtpPort(465);
        
        client = new EmailClient(file); 
        
    }
   
    @Test(expected=IllegalArgumentException.class)
    public void sendTestExc() 
    {
        CustomEmail d = 
                client.sendEmail(null, subject, text, html, cc, bcc, embedded, attachment);
        
    }
    @Test(expected=IllegalArgumentException.class)
    public void sendTestExc2() 
    {
        CustomEmail d = 
                client.sendEmail(to, null, text, html, cc, bcc, embedded, attachment);
        
    }
    @Test(expected=IllegalArgumentException.class)
    public void sendTestExc3() 
    {
        CustomEmail d = 
                client.sendEmail(to, subject, null, html, cc, bcc, embedded, attachment);
        
    }
        
    @Test
    public void sendTest() 
    {
        CustomEmail c = 
                client.sendEmail(to, subject, text, html, cc, bcc, embedded, attachment);
        
        /*
        
        
        WE WANT TO AVOID USING THE CUSTOMEMAIL EQUALS METHOD DUE TO THE FACT 
        THAT ALSO NEEDS TO BE TESTED
        
        
        We will compare 2 strings containing all the info we expect and another 
        containing what we obtained 
        
        NOTE
        
        bcc is not check due to the fact it is inaccessible thru jodd email
        also embedded attachments fall in the same category as bcc
        we will have to manually check gmail to see if embedded attachment were sent
       
        */
        //null values will be printed as 'null', we want an empty String
        // convert nulls into empty strings
        if(html == null)
            html = ""; 
        if(cc == null)
            cc = "";
        if(embedded == null)
            embedded = "";
        if(attachment == null)
            attachment = "";
        
        //now any null fields obtained from the jodd fields will match our null 
        // field values, which are empty string and not the value 'null'
        
        //form expected String
        String expected = to + "*" + "sp33dycow86@gmail.com" + "*" +
                subject + "*" + text + "" + html + "*" + cc + "*" + expectedAttachInfo + "*" + folder;
        
        //form obtained field 
        String obtained = convertArrayToString(c.getTo()) + "*" + "sp33dycow86@gmail.com" + "*" +
                c.getSubject() + "*" + getContentString(c.getAllMessages()) + "*" + convertArrayToString(c.getCc()) 
                + "*" + formAttachmentString(c.getAttachments()) + "*" + c.getFoldername();
        
        log.info("------------------- TEST CASE -------------------------------");
        log.info("Expected\n" + expected);
        log.info("Obtain\n" + obtained);
        log.info("--------------------------------------------------\n");
        
       assertTrue(expected.equals(obtained)); 
             
    }
   
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
    private String formAttachmentString(List<EmailAttachment> list)
    {
        //email had no attachments
        if(list == null)
            return "";
        
        StringBuilder info = new StringBuilder(""); 
        
        for(EmailAttachment e : list)
        {
            String attach = e.getName() + ",";
            info.append(attach);
        }
        return info.substring(0, info.length() - 1); 
    }
    
    
    
    //METHODS BELOW ARE FOR THE RECEIVE EMAIL
    
    @Test
    public void checkReceivedMails() 
    {
              
        // send a email that we will check to if receive method read in property
        sendTestEmail(mail);     
        
        //pause execution to allow the mail to reach the server
        try{
        Thread.sleep(2000);
        }catch(Exception e)
        {}
        
        /*
            WE WANT TO AVOID USING THE CUSTOM EMAIL EQUALS METHOD DUE TO THE FACT 
            THAT ALSO NEEDS TO BE TESTED
        */
        
        
        
        //we will test the receive emails the same way we tested send
        String expectedDate = mail.getReceivedDate().toString();
        //we dont want the seconds 
       expectedDate = expectedDate.substring(0,(expectedDate.length() - 12));
        String expected = convertArrayToString(mail.getTo()) + "*" + mail.getFrom().getEmail() + "*" +
                mail.getSubject() + "*" + getContentString(mail.getAllMessages()) + "*" + convertArrayToString(mail.getCc()) 
                + "*" + formAttachmentString(mail.getAttachments()) + "*" + mail.getFoldername() + "*" + expectedDate;
        
        CustomEmail[] mails = client.receiveEmail();
        
        //GET THE LAST EMAIL WE SENT 
        CustomEmail e = mails[mails.length - 1];       
        
        String obtainDate = mail.getReceivedDate().toString();
        //we dont want the seconds 
        obtainDate = obtainDate.substring(0,(obtainDate.length() - 12));
        String obtained = convertArrayToString(e.getTo()) + "*" + e.getFrom().getEmail() + "*" +
                e.getSubject() + "*" + getContentString(e.getAllMessages()) + "*" + convertArrayToString(e.getCc()) 
                + "*" + formAttachmentString(e.getAttachments()) + "*" + e.getFoldername() + "*" + obtainDate;
        
        log.info("------------------- TEST CASE -------------------------------");
        log.info("Expected\n" + expected);
        log.info("Obtain\n" + obtained);
        log.info("--------------------------------------------------\n");
        
        assertTrue(expected.equals(obtained));
    }
    
    private void sendTestEmail(CustomEmail email)
    {       
        SmtpServer<SmtpSslServer>smtpServer = SmtpSslServer
                .create("smtp.gmail.com")
                .authenticateWith("sp33dyrat86@gmail.com", "Tester11");
        
    	SendMailSession session = smtpServer.createSession();
    	session.open();
    	session.sendMail(email);
    	session.close();
        
        log.info("Send success");
    }
  
    
    
}
