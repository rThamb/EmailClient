package com.ren.start;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;
import java.nio.file.Paths;
import com.ren.userinterface.ConfigController; 
import com.ren.persistence.PropertiesManager; 
import com.ren.emailsystem.beans.Configuration;
import com.ren.userinterface.RootController;
import com.ren.validation.ConfigValidator;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Locale;

import static javafx.application.Application.launch;
import static javafx.application.Application.launch;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import static javafx.application.Application.launch;
import static javafx.application.Application.launch;
/**
 *
 * @author Renuchan
 */
public class MainAppFX extends Application {
    
    private Configuration config;
    private ResourceBundle msg;
   
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        PropertiesManager pm = new PropertiesManager("src/main/resources");
        getLanguage();
        this.config = pm.loadProperties(); 
     
        if(config == null || !checkConfig())
        {
            this.config = new Configuration();
            
            loadConfig(primaryStage, pm);
        }
        else
            loadRoot(primaryStage, pm);

    }
    
    /**
     * This method will check if the config file contains valid info
     * @return   boolean            if valid or not
     */
    public boolean checkConfig()
    {
        boolean valid = false;
        
        ConfigValidator checker = new ConfigValidator(config);
        
        try{
            checker.isValid();
            valid = true;
        }catch(Exception e)
        {
            valid = false;
        }
        return valid; 
        
    }
    /**
     * This method get the default language of the system
     * 
     */
    public void getLanguage()
    {
        Locale defaultLocale = Locale.getDefault();
        msg = ResourceBundle.getBundle("MessagesBundle", defaultLocale);
    }
    
    /**
     * This method load the config scene
     */
    public void loadConfig(Stage primaryStage, PropertiesManager pm)
    {
        FXMLLoader conloader = null;

        try{ 

            URL path = Paths.get("src/main/resources/fxml/ConfigForm.fxml").toUri().toURL();

            //set fxml path in loader
            //give builder factory for suitable instance constructing
            conloader = new FXMLLoader();
            //conloader.setResources(msg);

            conloader.setLocation(path);
            conloader.setBuilderFactory(new JavaFXBuilderFactory());
            //load into scene
            Scene scene = new Scene(conloader.load());

           ConfigController Controller = (ConfigController) conloader.getController();

            Controller.setProManager(pm);
            Controller.setConfig(this.config);

            //set settings on stage, add grid scene, and show
            primaryStage.setTitle("Mail Client");
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();
        }catch(Exception e)
        {
            e.printStackTrace();
            displayErrorMsg(msg.getString("errorLoadCon"));
        }
    }
    /**
     * This method load the root scene
     */
    public void loadRoot(Stage primaryStage, PropertiesManager pm)
    {
        
        FXMLLoader loader = null;
        try{
        URL path = Paths.get("src/main/resources/fxml/RootViewContainer.fxml").toUri().toURL();

        loader = new FXMLLoader();
        loader.setResources(msg);
        loader.setLocation(path);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        //load into scene
        Scene mainScene = new Scene(loader.load());

        RootController controller = (RootController) loader.getController();

        primaryStage.setTitle("Email Client");
        primaryStage.setResizable(true);
        primaryStage.setScene(mainScene);
        primaryStage.show();
        }catch(IOException e)
        {
            e.printStackTrace();
            displayErrorMsg(msg.getString("errorLoadMain"));
        }
    }
    /**
     * This method displays messages 
     */
    private void displayErrorMsg(String msg)
    {        
        Alert alert = new Alert(Alert.AlertType.NONE, msg, ButtonType.OK);
        alert.showAndWait();
        
        if (alert.getResult() == ButtonType.OK) {
            alert.close();
        }      
    }
    
    
    
    
    public static void main(String[] args) {
        launch(args);
    }
    
}