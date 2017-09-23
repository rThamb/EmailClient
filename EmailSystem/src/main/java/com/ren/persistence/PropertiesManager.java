
package com.ren.persistence;

import com.ren.emailsystem.beans.Configuration;

import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;
import static java.nio.file.Paths.get;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 *
 * @author Renuchan
 */
public class PropertiesManager {
 
    private String path;  
    
    public PropertiesManager(String path)
    {
        this.path = path; 
    }
    
    private Properties storeToProperty(Configuration con)
    {
        Properties pro = new Properties();
        
        pro.setProperty("username", con.getUserName()); 
        pro.setProperty("userEmail", con.getUserEmail());
        pro.setProperty("userPass", con.getUserEmailPwd()); 
        pro.setProperty("smtpAdd", con.getSmtpAddress());
        pro.setProperty("imapAdd", con.getImapAddress()); 
        pro.setProperty("smtpPort", con.getSmtpPort() + "");
        pro.setProperty("imapPort", con.getImapPort() + ""); 
        pro.setProperty("dbAdd", con.getDbAddress());
        pro.setProperty("dbPort", con.getDbPort() + ""); 
        pro.setProperty("dbUserEmail", con.getDbUser());
        pro.setProperty("dbUserPass", con.getDbPass()); 
        
        return pro; 
    }
    
    /**
     * Returns a Configuration object with the contents of the properties file
     *
     * @param path Must exist, will not be created
     * @param propFileName Name of the properties file
     * @return The bean loaded with the properties
     * @throws IOException
     */
    public Configuration loadProperties() throws IOException {

        Properties pro = new Properties();

        Path txtFile = get(path, "emailSystem" + ".properties");

        Configuration con = new Configuration();

        // File must exist
        if (Files.exists(txtFile)) {
            try (InputStream propFileStream = newInputStream(txtFile);) {
                pro.load(propFileStream);
            }
            
            con.setUserName(pro.getProperty("username")); 
            con.setUserEmail(pro.getProperty("userEmail"));
            con.setUserEmailPwd(pro.getProperty("userPass")); 
            con.setSmtpAddress(pro.getProperty("smtpAdd"));
            con.setImapAddress(pro.getProperty("imapAdd"));           
            con.setDbAddress(pro.getProperty("dbAdd"));
            con.setDbPort(Integer.parseInt(pro.getProperty("dbPort"))); 
            con.setDbUser(pro.getProperty("dbUserEmail"));
            con.setDbPass(pro.getProperty("dbUserPass"));
            
            try{
                con.setSmtpPort(Integer.parseInt(pro.getProperty("smtpPort")));
                con.setImapPort(Integer.parseInt(pro.getProperty("imapPort"))); 
                con.setDbPort(Integer.parseInt(pro.getProperty("dbPort")));
            }catch(IllegalArgumentException e)
            {
                con.setSmtpPort(0);
                con.setImapPort(0);
                con.setDbPort(0); 
            }
        }
        else 
            return null;
        return con;
    }

    /**
     * Creates a plain text properties file based on the parameters
     *
     * @param path Must exist, will not be created
     * @param propFileName Name of the properties file
     * @param mailConfig The bean to store into the properties
     * @throws IOException
     */
    public final void saveProperties(Configuration con) throws IOException {

        Properties prop = storeToProperty(con);
        
        Path txtFile = get(path, "emailSystem" + ".properties");

        // Creates the file or if file exists it is truncated to length of zero
        // before writing
        try (OutputStream propFileStream = newOutputStream(txtFile)) {
            prop.store(propFileStream, "System Properties");
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
}
