/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ren.emailsystem.beans;

import jodd.mail.*;
import java.util.*; 
import java.util.List; 
import java.util.Date;
import javax.mail.Flags;

/**
 *  This class will hold information regarding all emails used by EmailClient
 * 
 * @author Renuchan Thambirajah 
 */
public class CustomEmail extends Email{
    
    private String foldername; 
    
    //received Mail Fields 
    
    private Date receivedDate;
    private boolean isReceived; 
    private int messageNumber; 
    
    private Flags flag;
    private List<ReceivedEmail> attachedMessages; 
    
    private int id; 
    

    public CustomEmail()
    {
        super();
    }
    
    //Getters amd Setters    
    public void setFoldername(String name)
    {
        foldername = name; 
    }
    
    public String getFoldername()
    {
        return foldername; 
    }
    public Date getReceivedDate() {
	return receivedDate;
    }

    public void setFlag(Flags flag)
    {
        this.flag = flag;
    }
    public Flags getFlag()
    {
        return flag;
    }
    
    public List<ReceivedEmail> getAttachedMessages()
    {
        return attachedMessages; 
    }
    public void setAttachedMessages(List<ReceivedEmail> emails)
    {
        this.attachedMessages = emails; 
    }
    
    public void setReceivedDate(Date receivedDate) {
	this.receivedDate = receivedDate;
    }

    public boolean getIsReceived() {
	return isReceived;
    }

    public void setIsReceived(boolean r) {
	this.isReceived = r;
    }

    public int getMessageNumber() {
	return messageNumber;
    }

    public void setMessageNumber(int messageNumber) {
	this.messageNumber = messageNumber;
    }
    
    public int getId(){
        return id; 
    }
    public void setId(int id){
        this.id = id;
    }
    
    
    /**
     * This method will compare an object and determine if both objects are 
     * the same
     * 
     * @param obj            object to compare  
     * 
     * @return               comparison result    
     */
    @Override
    public boolean equals(Object obj)
    {
        if(obj == null)
            return false;
        if(this.getClass() != obj.getClass())
            return false;
                  
        CustomEmail email = (CustomEmail)obj; 
                
        String subject = email.getSubject();
        String from = email.getFrom().getEmail();
        MailAddress[] to = email.getTo();
        MailAddress[] cc = email.getCc();
        MailAddress[] bcc = email.getBcc();
        List<EmailAttachment> attachments = email.getAttachments();
        List<EmailMessage> content = email.getAllMessages();
        
        if(getSubject().equals(subject))
        {
            if(getFrom().getEmail().equals(from))
                if(checkLists(to,cc,bcc,attachments))
                    return checkContent(content);
                else
                    return false; 
            else
                return false; 
        }
        else
            return false;
           
    }
    private boolean checkLists(MailAddress[] to, MailAddress[] cc, MailAddress[] bcc, 
           List<EmailAttachment> attachments)
    {
        boolean areEqual; 
        
        if(checkListOfMails(getTo(), to))
        {
            if(checkListOfMails(getCc(),cc))
            {
                if(checkListOfMails(getBcc(), bcc))
                {
                    if(checkAttachments(attachments))
                        areEqual = true;
                    else 
                        areEqual = false;                           
                }
                else
                    areEqual = false;
            }
            else 
                areEqual = false; 
        }
        else
            areEqual = false; 
        
        return areEqual; 
    }
    
    private boolean checkListOfMails(MailAddress[] curObj, MailAddress[] otherObj)
    {
        if(curObj.length != otherObj.length)
            return false; 
        else
            for(int i = 0; i < curObj.length; i++)
                if(!(curObj[i].getEmail().equals(otherObj[i].getEmail())))
                    return false;
            
        return true; 
    }
    
    private boolean checkAttachments(List<EmailAttachment> attachments)
    {
        //null is return if empty
        List<EmailAttachment> curAttachment = getAttachments();
        
        if(attachments == null && curAttachment == null)
            return true;
        
        //check if one is empty and another is not
        // we want to avoid NullPointerExceptions
        if((attachments == null && curAttachment != null) || 
                (attachments != null && curAttachment == null))
            return false; 
            
            
        if(attachments.size() != curAttachment.size())
            return false; 
        
        for(int i = 0; i < curAttachment.size(); i++)
        {
            EmailAttachment curAtt = curAttachment.get(i); 
            EmailAttachment otherAtt = attachments.get(i);
            //check the name of the attachments and ensure they are the same 
            if(!curAtt.getName().equals(otherAtt.getName()))
                return false; 
        }
        return true; 
    }
    private boolean checkContent(List<EmailMessage> list)
    {
        List<EmailMessage> curObj = getAllMessages();
        
        if(list.size() != curObj.size())
            return false; 
        
        for(int i = 0; i < list.size(); i++)
        {
            EmailMessage curObjEle = curObj.get(i); 
            EmailMessage otherObj = list.get(i); 
            
            if(otherObj.getMimeType().equalsIgnoreCase(curObjEle.getMimeType()))
            {
                if(!otherObj.getContent().equalsIgnoreCase(curObjEle.getContent()))
                    return false;
            }
            else
                return false;
        }
        
        return true; 
    }
    
    @Override
     public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.from);
        hash = 13 * hash + Objects.hashCode(this.to);
        hash = 13 * hash + Objects.hashCode(this.cc);
        hash = 13 * hash + Objects.hashCode(this.subject);
        hash = 13 * hash + Objects.hashCode(this.messages);
        hash = 13 * hash + Objects.hashCode(this.attachments);
        return hash;
    }

}
