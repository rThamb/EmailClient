package com.ren.validation;

import com.ren.emailsystem.beans.Configuration;
import com.ren.emailsystem.beans.CustomEmail;
import jodd.mail.*;
import java.sql.*; 

/**
 *  This class is in charge of ensuring the configuration information is valid.
 * 
 * @author Renuchan
 */
public class ConfigValidator {
    
    private Configuration config; 
    
    public ConfigValidator(Configuration config)
    {
        this.config = config; 
    }
    
    /**
     * This method checks all the fields in the configuration object and validates 
     * each field. .
     * 
     * @return                  boolean indicating if all fields are valid.
     * @throws Exception        When an invalid valid is found
     */
    public boolean isValid()throws Exception
    {
        checkAllFieldProvided();
        checkValidEmail();
        checkPorts();
        checkLoginInfo();
        testImap();
        checkDatabaseInfo();
        
        return true; 
    }
    
    /**
     * Checks if all the configuration fields were provided
     * 
     * @throws IllegalArgumentException         iF a field was not provided
     */
    private void checkAllFieldProvided()throws IllegalArgumentException
    {
      
        if(config.getUserEmail().length() == 0)
            throw new IllegalArgumentException("Empty field given");
        
        if(config.getUserEmailPwd().length() == 0)
            throw new IllegalArgumentException("Empty field given");
        
        if(config.getUserName().length() == 0)
            throw new IllegalArgumentException("Empty field given");
        
        if(config.getSmtpAddress().length() == 0)
            throw new IllegalArgumentException("Empty field given");
        
        if(config.getImapAddress().length() == 0)
            throw new IllegalArgumentException("Empty field given");
        
        if(config.getDbAddress().length() == 0)
            throw new IllegalArgumentException("Empty field given");
        
        if(config.getDbPass().length() == 0)
            throw new IllegalArgumentException("Empty field given");
        
        if(config.getDbUser().length() == 0)
            throw new IllegalArgumentException("Empty field given");       

    }
    
    /**
     * This method ensures that the users email is formatted correctly 
     * 
     * @throws IllegalArgumentException     If email address has the wrong format
     */
    private void checkValidEmail()throws IllegalArgumentException
    {
        EmailAddress mail = new EmailAddress(config.getUserEmail());
        if(!mail.isValid())
            throw new IllegalArgumentException("Email Address doesnt exist");
    }
    
    /**
     * This method checks if all ports provided are the valid range
     * 
     * @throws IllegalArgumentException     If email address has the wrong format
     */
    private void checkPorts()throws IllegalArgumentException
    {
        int sPort = config.getSmtpPort();
        int imapPort = config.getImapPort();
        int dbPort = config.getDbPort();
        
        if(sPort < 0 || sPort > 65535)
            throw new IllegalArgumentException("Invalid Port");
        
        if(imapPort < 0 || imapPort > 65535)
            throw new IllegalArgumentException("Invalid Port");
        
        if(dbPort < 0 || dbPort > 65535)
            throw new IllegalArgumentException("Invalid Port");
    }
    
    /**
     * This method checks if the users credentials and SMTP address are valid 
     * 
     * @throws RuntimeException        If credentials or SMTP address isn't valid         
     */
    private void checkLoginInfo()
    {
        //the session will throw an exception if something fails 
        
        String user = config.getUserEmail();
        String pass = config.getUserEmailPwd(); 
        
        CustomEmail email = new CustomEmail();
        
        email.from(config.getUserEmail()).to(config.getUserEmail()).subject("Login")
                .addText("Congratz you logged in successfully");
        
        SmtpServer<SmtpSslServer>smtpServer = SmtpSslServer
                .create(config.getSmtpAddress(), config.getSmtpPort())
                .authenticateWith(config.getUserEmail(), config.getUserEmailPwd());
        
    	SendMailSession session = smtpServer.createSession();
    	session.open();
    	session.sendMail(email);
    	session.close();
    }
    
    /**
     * This method checks if Imap address is valid.
     * 
     * @throws RuntimeException        If credentials aren't valid         
     */
    private void testImap()
    {
        //the session will throw an exception if something fails 
        
        ImapSslServer imapServer = new ImapSslServer(config.getImapAddress(), 
                config.getImapPort(),config.getUserEmail(),
                config.getUserEmailPwd());
        
        //Create session with the server 
    	ReceiveMailSession session = imapServer.createSession();
    	session.open();
        session.close();
    }

    /**
     * This method checks if database information is valid.
     * 
     * @throws SQLException        If credentials or url is invalid          
     */
    private void checkDatabaseInfo()throws SQLException
    {
        String user= config.getDbUser();
        String pass = config.getDbPass();
        int port = config.getDbPort(); 
        
        String url = "jdbc:mysql://" + config.getDbAddress() + ":"+ port +"/cs1437641";
        
        Connection con = DriverManager.getConnection(url,user,pass);
    }
    
}
