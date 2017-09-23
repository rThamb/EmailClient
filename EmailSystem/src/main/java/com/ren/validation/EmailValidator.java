package com.ren.validation;

import jodd.mail.*; 

/**
 * This class will be used to valid email user wishes to send during the 
 * applications runtime.
 * 
 * @author Renuchan
 */
public class EmailValidator {
    
    public EmailValidator()
    {}
    
    /**
     * This method checks if the sending info is formatted properly and is valid
     * 
     * @param addresses         string containing the receivers information 
     * @throws Exception        if invalid info is present 
     */
    public void checkEmails(String addresses)throws Exception 
    {
        String[] list = addresses.split(",");
        for(String ele:list)
            checkEmail(ele);
    }
    
    /**
     * This method checks if the email address is valid
     * 
     * @param email         string containing the receivers information 
     * @throws Exception 
     */
    private void checkEmail(String email)throws IllegalArgumentException
    {
        EmailAddress mail = new EmailAddress(email);
        
        if(!mail.isValid())
            throw new IllegalArgumentException("Email: " + mail + "\nIs not a valid email");
    }
    
    
}
