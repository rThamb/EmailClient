package com.ren.userinterface;

import com.ren.emailsystem.EmailClient;
import com.ren.emailsystem.beans.Configuration;
import com.ren.emailsystem.beans.CustomEmail;
import com.ren.persistence.DBManagerImp;
import com.ren.persistence.PropertiesManager;
import com.ren.start.MainAppFX;
import com.ren.tool.EmailLoader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;

/**
 * This class is the main view's controller, it is in charge of handling 
 * all core tasks of the email system
 * 
 * @author Renuchan
 */
public class RootController {
    

    private TreeViewController treeCon;
    private QuickViewController quickViewCon;
    private MailDetailsController detailsCon;
    
    private Configuration file; 
    private EmailClient mailSystem;
    private DBManagerImp database; 
    private PropertiesManager pm = new PropertiesManager("src/main/resources");
    
    @FXML
    private ResourceBundle rBundle;

    @FXML
    private AnchorPane treeViewBox; 
    
    @FXML
    private AnchorPane listBox; 
    
    @FXML
    private AnchorPane composerBox; 
    
    /**
     * This method is called to assemble the entire view of the controller
     */
    @FXML
    public void initialize() 
    {
        getLanguage();
        setConfig();
        if(file != null)
        {
            setDatabase();
            setSystem();
            insertComposerView();
            insertListView();
            insertTreeView();
        }
    }
    
    /**
     * This method is called to insert the TreeView into the main View
     */
    private void insertTreeView()
    {
        
        try {
            FXMLLoader loader = new FXMLLoader();

            loader.setLocation(MainAppFX.class
                            .getResource("/fxml/TreeDisplay.fxml"));
            AnchorPane treeView = (AnchorPane) loader.load();

            // Give the controller the data object.
            treeCon = loader.getController();
            
            treeCon.setDatabase(database);
            treeCon.setQuickViewController(quickViewCon);
            treeCon.displayTree();

            treeViewBox.getChildren().add(treeView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * This method is called to insert the ListView into the main View
     */
    private void insertListView()
    {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(rBundle);

            loader.setLocation(MainAppFX.class
                            .getResource("/fxml/MailQuickDisplay.fxml"));
            AnchorPane View = (AnchorPane) loader.load();

            // Give the controller the data object.
            quickViewCon = loader.getController();
            quickViewCon.setMailDetailsController(detailsCon);
            quickViewCon.setDatabase(database);

            listBox.getChildren().add(View);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This method is called to insert the TreeView into the main View
     */
    private void insertComposerView()
    {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(rBundle);

            loader.setLocation(MainAppFX.class
                            .getResource("/fxml/composerDisplay.fxml"));
            AnchorPane View = (AnchorPane) loader.load();

            // Give the controller the data object.
            detailsCon = loader.getController();
            detailsCon.setEmailClient(mailSystem);
            detailsCon.setResource(rBundle);
            

            composerBox.getChildren().add(View);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This method is called to update all the view when the user has changed 
     * email accounts
     * 
     * @throws Exception       If unable to retrieve and save emails to the database
     */
    public void refreshForNewUser() throws Exception
    {
        try{
            setConfig();


            setDatabase();
            setSystem();

            treeCon.setDatabase(database);
            quickViewCon.setDatabase(database);
            detailsCon.setEmailClient(mailSystem);


            detailsCon.setEmailClient(mailSystem);

            quickViewCon.setMailDetailsController(detailsCon);

            treeCon.setQuickViewController(quickViewCon);
            treeCon.displayTree();    
        }
        catch(SQLException e)
        {
            displayMsg(e.getMessage());
        }
    }   
    
    /**
     * Will load the configuration form when button is clicked to allow the user to change 
     * the settings
     */
    @FXML
    private void onSettingChange() 
    {   
        PropertiesManager pm = new PropertiesManager("src/main/resources");
        
        try{
            Stage stage = new Stage();
            
            URL path = Paths.get("src/main/resources/fxml/ConfigForm.fxml").toUri().toURL();

            //set fxml path in loader
            //give builder factory for suitable instance constructing
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(rBundle);
            loader.setLocation(path);
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            //load into scene
            Scene scene = new Scene(loader.load());

            ConfigController Controller = (ConfigController) loader.getController();

            Controller.setProManager(pm);
            Controller.setConfig(this.file);
            Controller.setCurrentStage(stage);
            Controller.setRootController(this);

            //set settings on stage, add grid scene, and show
            stage.setTitle("Mail Client");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * This method sets up the view to allow the user to send an email
     */
    @FXML
    private void onCompose() {
        detailsCon.sendEmail();
        detailsCon.clearFields();
        
        displayMsg(rBundle.getString("composeMsg"));
    }
    
    /**
     * This method will add a folder to the users folders when clicked
     */
    @FXML
    private void onAddFolder() {
        inputFolderName(rBundle.getString("addFolderMsg"));
    }
    /**
     * Asks the user for the name of the new folder
     * @param msg 
     */
    private void inputFolderName(String msg)
    {   
        TextInputDialog box = new TextInputDialog("");
        box.setTitle(msg);
        
        Optional<String> result = box.showAndWait();
        result.ifPresent(name -> createFolder(name));      
    }
    /**
     * This method will write the new folder to the database
     * @param foldername        new folder name
     */
    private void createFolder(String foldername)
    {     
        try {
            if(!database.getUsersFolders().contains(foldername))
            {
                database.createFolder(foldername);
                treeCon.displayTree();
            }
        } catch (SQLException ex) {
            displayMsg(ex.getMessage());
        }
    }
    
    /**
     * Allow the user to move an email to another folder
     */
    @FXML
    private void onMoveMail()
    {
        CustomEmail email = detailsCon.getEmail();
        
        if(email != null)
        {
            getUsersFolder();
        }
    }
    /**
     * Gets the users folder from the database
     */
    private void getUsersFolder()
    {
        Stage stage = new Stage();
        
        List<String> folders = null;
        
        try{    
        folders = database.getUsersFolders();
        }catch(SQLException e)
        {
            
        }
        
        if(folders != null)
        {
            showFolderOptions(folders);
        }
    }
    /**
     * Gives the user a selection of folders to move to move mail to
     * 
     * @param folders       users folders 
     */
    private void showFolderOptions(List<String> folders)
    {
        Stage stage = new Stage();
        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);
        root.setHgap(folders.size());
        root.setVgap(folders.size());
        root.setPadding(new Insets(25, 25, 25, 25));

        int index = 0;
        for(String folder : folders)
        {

            Button btn = new Button();
            btn.setText(folder);
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    updateMailFolder(folder);
                    stage.close();
                }
            });

            root.add(btn, 0, index);
            index++;
        }
        Scene scene = new Scene(root, 300, 300);     
        stage.setTitle("New");
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * Write the move to the database
     * @param foldername 
     */
    private void updateMailFolder(String foldername)
    {
        try {
            int id = detailsCon.getEmail().getId();
            database.updateEmail(id, foldername);
        } catch (SQLException ex) {
            Logger.getLogger(RootController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Allows the user to delete a email when clicked
     */
    @FXML
    private void onDeleteMail()
    {
        showConfirmation();
    }
    /**
     * prompts a confirmation box to ensure the user wanted to do this action
     */
    private void showConfirmation()
    {
        Alert alert = new Alert(AlertType.CONFIRMATION, rBundle.getString("deleteMsg"), ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        
        if (alert.getResult() == ButtonType.YES) {
            
            try {
                
                if(detailsCon.getEmail() != null)
                {
                    int id = detailsCon.getEmail().getId();
                    database.deleteEmail(id);
                    quickViewCon.displayTheTable();
                }

            } catch (SQLException ex) {
                
            }
            alert.close();
        }
        else
        {
            alert.close();
        }    
    }
    
    /**
     * Allows the user to delete a folder 
     */
    @FXML 
    private void onDeleteFold()
    {
        String foldername = quickViewCon.getFolderName();
        
        if(foldername != null)
        {
            if(!foldername.equals("inbox") && !foldername.equals("sent"))
            {
                Alert alert = new Alert(AlertType.CONFIRMATION, 
                        rBundle.getString("confirmFold") + " " + foldername, 
                        ButtonType.YES, ButtonType.NO);
                alert.showAndWait();

                if (alert.getResult() == ButtonType.YES) {

                    deleteFolder(foldername);
                    alert.close();
                }
                else
                {
                    alert.close();
                }  
            }
            else
            {
                Alert alert = new Alert(AlertType.NONE, 
                        rBundle.getString("deniedDelMsg"),
                        ButtonType.OK);
                alert.showAndWait();
            }
                
        }
    }
    /**
     * write the delete of the folder to the database
     * @param name      name of the folder
     */
    private void deleteFolder(String name)
    {
        try{
            database.deleteFolder(name);
            treeCon.displayTree();
        }
        catch(SQLException e)
        {
            displayMsg(rBundle.getString("errorDelete"));
        }
    }
    
    /**
     * This method when called, loads any unread emails and write it into the 
     * database
     */
    @FXML
    private void onRefresh() {
        try {
            mailSystem.receiveEmail();            
            treeCon.displayTree();
            
        } catch (SQLException ex) {
            displayMsg(ex.getMessage());
        }
    }
    /**
     * This method closes the entire application
     */
    @FXML
    private void onExit() {
        System.exit(0);
    }
    
    /**
     * This method loads a dialog box, to provide instructions about the application 
     */
    @FXML 
    private void onInfo()
    {
        String msg = "";
        
        msg = rBundle.getString("TBO1") + "\n\n" + rBundle.getString("TBO2") + "\n\n" +
              rBundle.getString("TBO3") + "\n\n" + rBundle.getString("TBO4") + "\n\n" + 
              rBundle.getString("TBO5") + "\n\n" + rBundle.getString("TBO6") + "\n\n" +
              rBundle.getString("TBO7") ; 
        
        Alert alert = new Alert(AlertType.NONE, msg, ButtonType.OK);
        alert.showAndWait();
    }
    
    public void setSystem()
    {
        this.mailSystem = new EmailClient(file);
        mailSystem.setDatabase(database);
        //load and store emails
        Alert alert = new Alert(AlertType.NONE, rBundle.getString("loadMsg"), ButtonType.OK); 
        alert.showAndWait();
        EmailLoader loader = new EmailLoader(mailSystem);
        System.out.println("Going to load");
        loader.loadMails();
        
    }
    
    
    public void setDatabase()
    {
        this.database = new DBManagerImp(file);
    }
    
    public void setConfig()
    {
        try {
            file = pm.loadProperties();
        } catch (IOException ex) {
            System.out.println("failed to read");
        }
    }
    
    private void displayMsg(String msg)
    {        
        Alert alert = new Alert(AlertType.NONE, msg, ButtonType.OK);
        alert.showAndWait();
        
        if (alert.getResult() == ButtonType.OK) {
            alert.close();
        }      
    }
    
    public void setResourceBundle(ResourceBundle bun)
    {
        this.rBundle = bun;
    }
    
    /**
     * This method allows the user to select the language of the application
     * 
     */
    public void getLanguage()
    {
        Locale defaultLocale = Locale.getDefault();
        rBundle = ResourceBundle.getBundle("MessagesBundle", defaultLocale);
        
        ButtonType but = new ButtonType(rBundle.getString("engBtn"));
        ButtonType but2 = new ButtonType(rBundle.getString("frBtn"));
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, rBundle.getString("langMsg"), but, but2);
        alert.showAndWait();
        
        if (alert.getResult() == but2) {
            Locale currentLocale = new Locale("fr", "CA");
            rBundle = ResourceBundle.getBundle("MessagesBundle", currentLocale);
        }
        
        alert.close();
    }

    
}
