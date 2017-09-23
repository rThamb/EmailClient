package com.ren.userinterface;

import com.ren.emailsystem.beans.Configuration;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

import com.ren.emailsystem.beans.CustomEmail;
import com.ren.persistence.DBManagerImp; 
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class is the Mail List view's controller, it is in charge of handling 
 * all tasks related to displaying a list of emails depending on the folder clicked.
 * 
 * @author Renuchan
 */
public class QuickViewController {
    
    private DBManagerImp database; 
    private String foldername; 
    private MailDetailsController detailsCon; 

    
    @FXML
    private AnchorPane MailTable;
    
    @FXML
    private TableView<CustomEmail> MailDataTable;

    @FXML
    private TableColumn<CustomEmail, String> fromCol;

    @FXML
    private TableColumn<CustomEmail, String> subCol;
    
    @FXML
    private TableColumn<CustomEmail, String> dateCol;
    

    public QuickViewController() {
            super();          
    }
    
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {

        // Connects the property in the FishData object to the column in the
        // table
        fromCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFrom().toString()));

        subCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getSubject().toString()));

        dateCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getReceivedDate().toString()));

        adjustColumnWidths();

        
        MailDataTable.getSelectionModel()
                     .selectedItemProperty()
                     .addListener((observable, oldValue, newValue) -> showEmailDetails(newValue));
        
    }
    
    
    /**
     * 
     * This method calls the controller of the composer to display the contents 
     * of the email it passes to it. 
     * 
     * @param mail      Email to display to the user
     */
    public void showEmailDetails(CustomEmail mail)
    {
        detailsCon.setEmail(mail);
        detailsCon.displayMail();
        
    }
    
    
    
    /**
     * Sets the width of the columns based on a percentage of the overall width
     * 
     * This needs to enhanced so that it uses the width of the anchor pane it is
     * in and then changes the width as the table grows.
     */
    private void adjustColumnWidths() {
        // Get the current width of the table
        double width = MailTable.getPrefWidth();
        // Set width of each column
        fromCol.setPrefWidth(width * .33);
        subCol.setPrefWidth(width * .67);
        dateCol.setPrefWidth(width * .33);
    }
    
    
    
    /**
     * The table displays the Mail data
     * 
     * @throws SQLException
     */
    public void displayTheTable() throws SQLException {
        // Add observable list data to the table
        MailDataTable.setItems(getEmails());
    }
    
    /**
     * Convert the string array obtained by the database into observable objects
     * 
     * @return                          list of observable objects
     * @throws SQLException             if database read failed 
     */
    private ObservableList<CustomEmail> getEmails() throws SQLException
    {
        List<CustomEmail> emails = this.database.retrieveEmail(foldername);
        
        ObservableList<CustomEmail> mails = FXCollections
                .observableArrayList();
        
        for(CustomEmail ele : emails)       
            mails.add(ele);
        
        return mails; 
        
        
    }
    
    public void setFolderName(String foldername)
    {
        this.foldername = foldername; 
    }
    
    public String getFolderName()
    {
        return foldername; 
    }
    
    public void setDatabase(DBManagerImp db)
    {
        database = db;  
    }
    
    public void setMailDetailsController(MailDetailsController deatilsCon)
    {
        this.detailsCon = deatilsCon; 
    }
    
    
    
    /**
     * The TreeViewController needs a reference to the this controller. With
     * that reference it can call this method to retrieve a reference to the
     * TableView and change its selection
     * 
     * @return          reference to this controller
     */
    public TableView<CustomEmail> getMailDataTable() {
            return MailDataTable;
    }
    
    
    
}
