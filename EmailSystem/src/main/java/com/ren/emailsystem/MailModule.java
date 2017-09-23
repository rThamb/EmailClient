/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ren.emailsystem;

import com.ren.emailsystem.beans.*; 

/**
 * 
 * Defines functionality of a Mail Client,  that is able to send and 
 * receive email
 *
 * @author Renuchan Thambirajah
 */
public interface MailModule {
    
    CustomEmail sendEmail(String to, String subject, String text,
            String html, String cc, String bcc, String embedded, 
            String attachment);
    
    CustomEmail[] receiveEmail();
    
}
