/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ren.persistence;

import com.ren.emailsystem.beans.*; 
import java.sql.*; 
import java.util.ArrayList;
/**
 *
 * @author Renuchan
 */
public interface DBManageable 
{

    int writeEmail(CustomEmail email)throws SQLException; 
	
    int updateEmail(int id, String foldername)throws SQLException;
	
    ArrayList<CustomEmail> retrieveEmail(String foldername)throws SQLException;
    
    public void createFolder(String foldername)throws SQLException;
    
    public int deleteEmail(int id)throws SQLException;
    
    public void changeSettings(Configuration config);
    
    public ArrayList<String> getUsersFolders() throws SQLException;
	
	 
}
