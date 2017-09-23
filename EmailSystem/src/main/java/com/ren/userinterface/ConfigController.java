
package com.ren.userinterface;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.util.converter.NumberStringConverter;
import java.io.IOException; 

import com.ren.emailsystem.beans.Configuration;
import com.ren.persistence.PropertiesManager; 
import com.ren.validation.ConfigValidator;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 *
 * This class is the configuration view's controller, it is in charge of handling 
 * all tasks the configuration is in charge of performing. 
 * 
 * @author Renuchan
 */
public class ConfigController {
    
    private Configuration config;
    private PropertiesManager pm; 
    private Stage stage; 
    private RootController controller; 

    @FXML
    private TextField userNameTB;

    @FXML
    private TextField userEmailTB;

    @FXML
    private PasswordField userEmailPassTB;

    @FXML
    private TextField smtpAddTB;

    @FXML
    private TextField imapAddTB;

    @FXML
    private TextField smtpPortTB;

    @FXML
    private TextField imapPortTB;

    @FXML
    private TextField dbAddTB;

    @FXML
    private TextField dbPortTB;

    @FXML
    private TextField dbUserTB;

    @FXML
    private PasswordField dbPassTB;

    /**
     * Default constructor creates an instance of FishData that can be bound to
     * the form
     */
    public ConfigController() {
            super();          
    }

    /**
     * This method is automatically called after the fxml file has been loaded.
     * This code binds the properties of the data bean to the JavaFX controls.
     * Changes to a control is immediately written to the bean and a change to
     * the bean is immediately shown in the control.
     */

    @FXML
    private void initialize() 
    {
        if(config != null)
        {
            Bindings.bindBidirectional(userNameTB.textProperty(), config.getUserNamePro());
            Bindings.bindBidirectional(userEmailTB.textProperty(), config.getUserEmailPro());
            Bindings.bindBidirectional(userEmailPassTB.textProperty(), config.getUserEmailPwdPro());
            Bindings.bindBidirectional(smtpAddTB.textProperty(), config.getSmtpAddressPro());
            Bindings.bindBidirectional(imapAddTB.textProperty(), config.getImapAddressPro());
            Bindings.bindBidirectional(smtpPortTB.textProperty(), config.getSmtpPortPro(), new NumberStringConverter());
            Bindings.bindBidirectional(imapPortTB.textProperty(), config.getImapPortPro(), new NumberStringConverter());
            Bindings.bindBidirectional(dbAddTB.textProperty(), config.getDbAddressPro());
            Bindings.bindBidirectional(dbPortTB.textProperty(), config.getDbPortPro(), new NumberStringConverter());
            Bindings.bindBidirectional(dbUserTB.textProperty(), config.getDbUserPro());
            Bindings.bindBidirectional(dbPassTB.textProperty(), config.getDbPassPro());
        }
    }

    /**
     * This method checks and saves the information provided to a file
     * 
     * @param event
     */
    @FXML
    void saveCongfiguration(ActionEvent event)throws IOException {
            
        ConfigValidator validator = new ConfigValidator(config);
        
        try{
            validator.isValid();
            pm.saveProperties(config);

            if(controller != null)
            {
                controller.refreshForNewUser();
            }
            
            stage.close();
        }
        catch(Exception e)
        {
            displayErrorMsg(e.getMessage() + "Failed to update with new user info");
            e.printStackTrace();
        }            
    }
    
    /**
     * This method displays any error messages
     * 
     * @param msg           msg to display
     */
    private void displayErrorMsg(String msg)
    {
        
        Alert alert = new Alert(Alert.AlertType.NONE, msg, ButtonType.OK);
        alert.showAndWait();
        
        if (alert.getResult() == ButtonType.YES) {
            alert.close();
        }       
    }
    
    public void setProManager(PropertiesManager pm)
    {
        this.pm = pm;
    }
    
    public void setConfig(Configuration config)
    {
        this.config = config;
        initialize();
    }
    
    public void setCurrentStage(Stage stage)
    {
        this.stage = stage;
    }
    
    public void setRootController(RootController controller)
    {
        this.controller = controller; 
    }

}
