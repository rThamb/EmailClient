/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JunitTests.BeansTest;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import com.ren.emailsystem.beans.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import jodd.mail.*; 

import java.io.*; 

/**
 *
 * @author Renuchan
 */
@RunWith(Parameterized.class)
public class CustomEmailTest {
    
    private CustomEmail m1;
    private CustomEmail m2;
    private boolean expected; 
   
    
    
    @Parameterized.Parameters 
    public static Collection<Object[]> data()
    {
        
      //please insert a path with a   
        
      String path1 = "attachmentsFile\\car1.jpg";
      String path2 = "attachmentsFile\\car2.jpg";            
        
      CustomEmail test1 = new CustomEmail();
      test1.to("sp33dycow86@gmail.com");
      test1.from("sp33dyrat86@gmail.com");
      test1.subject("HEy man what up");
      test1.addText("Life is great now");
      test1.setFoldername("sent");

      
      CustomEmail test2 = new CustomEmail();
      test2.to("sp33dycow86@gmail.com");
      test2.from("sp33dyrat86@gmail.com");
      test2.subject("LOL");
      test2.addText("REKT");
      test2.cc("master_doomknight@hotmail.com");
      test2.setFoldername("sent");
      
      CustomEmail test3 = new CustomEmail();
      test3.to("sp33dycow86@gmail.com");
      test3.from("sp33dyrat86@gmail.com");
      test3.subject("LOL");
      test3.addText("REKT");
      test3.cc("master_doomknight@hotmail.com");
      test3.setFoldername("sent");
      if(!path1.equals(""))
        test3.attach(EmailAttachment.attachment().file(path1)); 
      
      CustomEmail test4 = new CustomEmail();
      test4.to("sp33dycow86@gmail.com");
      test4.from("sp33dyrat86@gmail.com");
      test4.subject("LOL");
      test4.addText("REKT");
      test4.cc("master_doomknight@hotmail.com");
      test4.setFoldername("sent");
      if(!path1.equals(""))
        test4.attach(EmailAttachment.attachment().file(path1)); 

      CustomEmail test5 = new CustomEmail();
      test5.to("sp33dycow86@gmail.com");
      test5.from("sp33dyrat86@gmail.com");
      test5.subject("HEy man what up");
      test5.addText("Life is great now");
      test5.setFoldername("sent");
      
      CustomEmail test6 = new CustomEmail();
      test6.to("sp33dycow86@gmail.com");
      test6.from("sp33dyrat86@gmail.com");
      test6.subject("LOL");
      test6.addText("REKT");
      test6.cc("master_doomknight@hotmail.com");
      test6.setFoldername("sent");
      
      CustomEmail test7 = new CustomEmail();
      test7.to("sp33dycow86@gmail.com");
      test7.from("sp33dyrat86@gmail.com");
      test7.subject("LOL");
      test7.addText("REKT");
      test7.addHtml("<html><head></head><body><p>Yo</p></body></html>");
      test7.cc("master_doomknight@hotmail.com");
      test7.setFoldername("sent");
      
      
      CustomEmail test9 = new CustomEmail();
      test9.to("sp33dycow86@gmail.com");
      test9.from("sp33dyrat86@gmail.com");
      test9.subject("LOL");
      test9.addText("REKT");
      test9.cc("master_doomknight@hotmail.com");
      test9.setFoldername("sent");
      if(!path2.equals(""))
        test9.attach(EmailAttachment.attachment().file(path2));
      
      
        Object[] data1 = {test1, test6, false}; 
        Object[] data2 = {test1,test5,true}; 
        Object[] data3 = {test6, test7, false}; 
        
        Object[] data4 = {test3,test4,true}; 
        Object[] data5 = {test1, test2, false}; 
        Object[] data6 = {test6,test2,true}; 
        
        Object[] data7 = {test6, test4, false}; 
        Object[] data8 = {test5,test7,false};
        Object[] data9 = {test4,test9,false};
        
        
        Object[][] data = new Object[][]{
                            data1,data2, data3,data4,data5, data6,data7,data8,data9
                            };
        
        return Arrays.asList(data);
    }
    
    
    
    
    
    public CustomEmailTest(CustomEmail m1, CustomEmail m2, boolean expected) {
        
        this.m1 = m1;
        this.m2 = m2; 
        this.expected = expected; 
    }
    
    
    @Test
    public void TestEquals() 
    {
        boolean obtained = m1.equals(m2);
        
        assertEquals(expected, obtained); 
        
    }

}
