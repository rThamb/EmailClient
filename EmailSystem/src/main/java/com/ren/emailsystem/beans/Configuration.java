package com.ren.emailsystem.beans;

import java.io.Serializable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *  Defines a class containing all configuration information that would be 
 *  used from a MailModule Class  
 * 
 * @author Renuchan Thambirajah
 */
public class Configuration implements Serializable{
       
    
    private StringProperty UserName; 
    private StringProperty userEmail;
    private StringProperty userEmailPwd;
    
    private StringProperty smtpAddress;
    private StringProperty imapAddress;
    private IntegerProperty smtpPort; 
    private IntegerProperty imapPort; 
    
    private StringProperty dbAddress; 
    private IntegerProperty dbPort; 
    private StringProperty dbUserName;
    private StringProperty dbPass; 
    
    
    public Configuration()
    {
        this("","","","","",465,993,3306,"","",""); 
    }
    
    
    public Configuration(String userEmail, String userEmailPwd, 
            String smtpAddress, String imapAddress, String dbAdress, 
            int smtpPort, int imapPort, int dbPort, String username, 
            String dbUserName, String dbPass)
    {      
        this.UserName = new SimpleStringProperty(username);
        this.userEmail = new SimpleStringProperty(userEmail);
        this.userEmailPwd = new SimpleStringProperty(userEmailPwd);
    
        this.smtpAddress = new SimpleStringProperty(smtpAddress);
        this.imapAddress = new SimpleStringProperty(imapAddress);
        this.smtpPort = new SimpleIntegerProperty(smtpPort); 
        this.imapPort = new SimpleIntegerProperty(imapPort);
    
        this.dbAddress = new SimpleStringProperty(dbAdress); 
        this.dbPort = new SimpleIntegerProperty(dbPort);
        this.dbUserName = new SimpleStringProperty(dbUserName);
        this.dbPass = new SimpleStringProperty(dbPass);
        
    }

    public String getUserEmail() {
        return userEmail.get();
    }

    public void setUserEmail(String userEmail) {
        this.userEmail.set(userEmail);
    }
    
    public String getUserEmailPwd() {
        return userEmailPwd.get();
    }

    public void setUserEmailPwd(String userEmailPwd) {
        this.userEmailPwd.set(userEmailPwd);
    }

    public String getSmtpAddress() {
        return smtpAddress.get();
    }

    public void setSmtpAddress(String smtpAddress) {
        this.smtpAddress.set(smtpAddress);
    }

    public String getImapAddress() {
        return imapAddress.get();
    }

    public void setImapAddress(String imapAddress) {
        this.imapAddress.set(imapAddress);
    }

    public int getSmtpPort() {
        return smtpPort.get();
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort.set(smtpPort);
    }

    public int getImapPort() {
        return imapPort.get();
    }

    public void setImapPort(int imapPort) {
        this.imapPort.set(imapPort);
    }

    public String getDbAddress() {
        
        return dbAddress.get();
    }

    public void setDbAddress(String databaseAddress) {
        this.dbAddress.set(databaseAddress);
    }

    public int getDbPort() {
        return dbPort.get();
    }

    public void setDbPort(int port) {
            this.dbPort.set(port);
    }
    
    public void setDbUser(String name){
        this.dbUserName.set(name);
    }
    
    public String getDbUser(){
        return dbUserName.get(); 
    }
    
    public void setDbPass(String name){
        this.dbPass.set(name);
    }
    
    public String getDbPass(){
        return dbPass.get(); 
    }
    
    public void setUserName(String name){
        this.UserName.set(name);
        int i = 0; 
    }
    
    public String getUserName(){
        return UserName.get(); 
    }
   
    //getters for the property object 
	
	public StringProperty getUserEmailPro() {
        return userEmail;
    }
	public StringProperty getUserEmailPwdPro() {
        return userEmailPwd;
    }
	public StringProperty getSmtpAddressPro() {
        return smtpAddress;
    }
	public StringProperty getImapAddressPro() {
        return imapAddress;
    }
	public IntegerProperty getSmtpPortPro() {
        return smtpPort;
    }
	public IntegerProperty getImapPortPro(){
        return imapPort;
    }
	public StringProperty getDbAddressPro() {
        
        return dbAddress;
    }
	public IntegerProperty getDbPortPro() {
        return dbPort;
    }
	public StringProperty getDbUserPro(){
        return dbUserName; 
    }
	public StringProperty getDbPassPro(){
        return dbPass; 
    }
	public StringProperty getUserNamePro(){
        return UserName; 
    }
	
}
