/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JunitTests.Persistence;

import com.ren.emailsystem.beans.Configuration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.*;

import com.ren.persistence.PropertiesManager;

/**
 *
 * @author Renuchan
 */
public class ProMan {
    
    public ProMan() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    //@Test
    public void TestSave(){
    
        PropertiesManager man = new PropertiesManager("");
           
        Configuration file = new Configuration();
        
        file.setImapAddress("imap.gmail.com");
        file.setSmtpAddress("smtp.gmail.com");
        file.setUserEmail("sp33dycow86@gmail.com");
        file.setUserEmailPwd("Tester11");
        file.setImapPort(993);
        file.setSmtpPort(465);
        file.setUserName("Bill");
        file.setDbAddress("waldo.....");
        file.setDbUser("casca");
        file.setDbPass("password");
        file.setDbPort(3306);
        
        try{    
            man.saveProperties(file);
            assertTrue(true);
        }catch(IOException e)
        {
            assertTrue(false);
        }
        
        
    }
    
    @Test
    public void TestWrite()
    {
        PropertiesManager man = new PropertiesManager("");
        Configuration con = null;
        
        try{    
            con = man.loadProperties();
            assertTrue(true);
        }catch(IOException e)
        {
            assertTrue(false);
        }
   /*     
        File file = new File("emailSystem.properties");
        
        if(file.exists())
            file.delete(); 
     */   
        int i = 0; 
    }
}
