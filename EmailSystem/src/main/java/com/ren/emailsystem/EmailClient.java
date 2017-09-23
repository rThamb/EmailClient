/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ren.emailsystem;

import com.ren.emailsystem.beans.Configuration;
import com.ren.emailsystem.beans.CustomEmail;
import java.io.File;
import java.util.List;
import java.util.Optional;

import javax.mail.Flags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.mail.Email;
import jodd.mail.EmailAddress;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailFilter;
import jodd.mail.EmailMessage;
import jodd.mail.ImapSslServer;
import jodd.mail.MailAddress;
import jodd.mail.ReceiveMailSession;
import jodd.mail.ReceivedEmail;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;
import jodd.mail.SmtpSslServer;

import javax.mail.AuthenticationFailedException; 
import java.net.UnknownHostException;

import com.ren.emailsystem.beans.*; 
import com.ren.persistence.DBManagerImp;
import java.sql.SQLException;

/**
 *
 * This class allows user to send and receive emails
 * 
 * @author Renuchan Thambirajah
 */
public class EmailClient implements MailModule
{   
    private Configuration config;
    private DBManagerImp database;
    
    //Logging purposes 
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    public EmailClient(Configuration file)
    {
        config = file; 
        database = new DBManagerImp(file);
        
    }
        
    /**
     * This method will send email objects, with the field given. Assumes valid 
     * emails and attachment paths are provided
     * 
     * @param to            list of receivers, comma separated 
     * @param subject       subject of the email
     * @param text          text content of the email 
     * @param html          embedded html for the email
     * @param cc            list of cc receivers, comma separated 
     * @param bcc           list of bcc receivers, comma separated
     * @param embedded      list of file paths to embed to email, comma separated
     * @param attachment    list of file paths to attach to email, comma separated
     * 
     * @return                              The email object that was sent
     * @throws IllegalArgumentException     If to and subject fields are not provided   
     */
    @Override
    public CustomEmail sendEmail(String to, String subject, String text, 
            String html, String cc, String bcc, String embedded, 
            String attachment) throws IllegalArgumentException
    {                   
        CustomEmail email = formEmail(to, subject, text, html, cc, bcc,embedded, attachment); 
        
        log.info("Sending Email with info: \n" + 
                to + "\n" + subject + "\n" + text + "\n" + html + "\n" + cc
                + "\n" + bcc + "\n" + embedded + "\n" + attachment);      
    	
        int numAttachments = 0;
        if(email.getAttachments() != null)
                numAttachments = email.getAttachments().size();       
        
        //create a session and send email           
        SmtpServer<SmtpSslServer>smtpServer = SmtpSslServer
                .create(config.getSmtpAddress(), config.getSmtpPort())
                .authenticateWith(config.getUserEmail(), config.getUserEmailPwd());
        
    	SendMailSession session = smtpServer.createSession();
    	session.open();
    	session.sendMail(email);
    	session.close();
        
        //attach the email if it disappeared
        
        if(numAttachments != 0)
        {
            if(email.getAttachments().size() != numAttachments)
            {
                if(embedded != null && (!embedded.isEmpty()))
                {
                    String[] item = embedded.split(",");
                    for(String path : item)
                        email.embed(EmailAttachment.attachment()
                            .bytes(new File(path)));
                }
            }
        }
        
        email.setFoldername("sent");
        
        try{
            database.writeEmail(email);
        }catch(SQLException e)
        {
            log.info("Database write Failed");
        }
        log.info("Send Success");  
        
        return email; 
    }
    
    private CustomEmail formEmail(String to, String subject, String text,
            String htmlContent, String cc, String bcc, String embedded, 
            String attachment) throws IllegalArgumentException
    {
        if(to == null || subject == null || text == null)
            throw new IllegalArgumentException("Provide all important fields");
        //will allow text to be empty string  
        if(to.isEmpty() || subject.isEmpty())
            throw new IllegalArgumentException("Provide all important fields");
        
        //comma serperated list of emails
        String[] receivers = to.split(","); //will create array of size 1 if no , 
               
        CustomEmail email = new CustomEmail(); 
        
        //add the important content
        email.from(config.getUserEmail()).to(receivers).subject(subject).addText(text); 
        
        //Add html if present 
        if(htmlContent != null && (!htmlContent.isEmpty()))
            email.addHtml(htmlContent); 
        
        if(cc != null && (!cc.isEmpty()))
        {
            String[] allCc = cc.split(",");            
            email.cc(allCc);
        }
        
        if(bcc != null && (!bcc.isEmpty()))
        {
            String[] allBcc = bcc.split(",");          
            email.bcc(allBcc);
        }
        
        //add attachment 
        if(embedded != null && (!embedded.isEmpty()))
        {
            String[] item = embedded.split(",");
            for(String path : item)
                email.embed(EmailAttachment.attachment()
                    .bytes(new File(path)));
        }
        if(attachment != null && (!attachment.isEmpty()))
        {
            String[] item = attachment.split(",");
            for(String path : item)
            email.attach(EmailAttachment.attachment().file(path));
        }       
        return email; 
    }
    
    
    
    
    /**
     * This method will receive unread emails 
     * 
     * @return           A list of unread emails
     */
    @Override
    public CustomEmail[] receiveEmail() 
    {
        
        ImapSslServer imapServer = new ImapSslServer(config.getImapAddress(), 
                config.getImapPort(),config.getUserEmail(),
                config.getUserEmailPwd());
        
        //Create session with the server 
    	ReceiveMailSession session = imapServer.createSession();
    	session.open();
    	
    	//Obtain all messages that have not been read 
    	ReceivedEmail[] emails = session.receiveEmailAndMarkSeen
    			(EmailFilter.filter().flag(Flags.Flag.SEEN, false));
 	session.close();
        
        CustomEmail[] mails = convertEmails(emails); 
        
        if(mails != null)
        {
            try{
                for(CustomEmail email:mails)
                    database.writeEmail(email);
            }catch(SQLException e)
            {
                log.info("Database write Failed");
            }

            log.info("Finished Receiving Emails");
        }
        return mails; 
    }
    
    private CustomEmail[] convertEmails(ReceivedEmail[] emails)
    {
        CustomEmail[] convertedEmails = null;
        
        if(emails != null)
        {
            convertedEmails = new CustomEmail[emails.length];
            
            for(int i = 0; i < emails.length; i++)
            {
                convertedEmails[i] = formEmailFromReceive(emails[i]);
                convertedEmails[i].setFoldername("inbox");
            }
        }
            
        return convertedEmails;
    }
    
    
    /**
     * This method will create an instance using info from a received mail  
     * 
     * @param email            Obj to recreate from  
     */
    private CustomEmail formEmailFromReceive(ReceivedEmail email)
    {
        CustomEmail mail = new CustomEmail();
        
        mail.setTo(email.getTo());
        mail.setFrom(email.getFrom());
        mail.setSubject(email.getSubject());       
        mail.setCc(email.getCc());
        mail.setBcc(email.getBcc());
        
        mail.setReceivedDate(email.getReceiveDate());
        mail.setIsReceived(true);
        mail.setFlag(email.getFlags());
        mail.setMessageNumber(email.getMessageNumber());
        mail.setAttachedMessages(email.getAttachedMessages()); 
       
        //get the html and the text of the email
        List<EmailMessage> content =  email.getAllMessages(); 
    
       //store it to the object currenting making 
       //JODD appends \n and \r to the end of content, deal with this issue
       for(EmailMessage m : content)
       {                 
           if(m.getMimeType().equalsIgnoreCase("text/plain"))
               mail.addText(m.getContent().replace("\n", "").replace("\r", ""));
           else if(m.getMimeType().equalsIgnoreCase("text/html"))
               mail.addHtml(m.getContent().replace("\n", "").replace("\r", ""));
       }
       
       // this array will contain all the attachments but embedded ones  
       List<EmailAttachment> allAttachment = email.getAttachments();
             
       //store to the email object currenting making     
       
       if(allAttachment != null)
        for(EmailAttachment a : allAttachment)      
            mail.attach(a);
        
        return mail;
    }   
   
    public void setDatabase(DBManagerImp database)
    {
        this.database = database; 
    }
  
}
