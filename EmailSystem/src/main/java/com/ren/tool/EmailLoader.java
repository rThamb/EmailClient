package com.ren.tool;

import com.ren.emailsystem.EmailClient;

/**
 *  This class will be use to read email addresses of the current user and 
 *  store it in the database.
 * 
 * @author Renuchan
 */
public class EmailLoader {
 
    private EmailClient system;
    
    public EmailLoader(EmailClient system)
    {
        this.system = system; 
    }
    
    /**
     * This method will be invoked to load and store the current users emails 
     */
    public void loadMails()
    {
        System.out.println("EmailLoader: In the load method going to load emails");
        
        system.receiveEmail();
        
        System.out.println("EmailLoader: Finished loading all mails");
        
        
    }
    
    
    
    
}
