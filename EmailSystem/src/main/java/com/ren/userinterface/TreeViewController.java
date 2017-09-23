package com.ren.userinterface;

import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;

import com.ren.userinterface.QuickViewController;
import com.ren.emailsystem.beans.CustomEmail;
import com.ren.persistence.DBManagerImp;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.scene.image.Image;

/**
 * This class is the Tree View view's controller, it is in charge of handling 
 * all tasks related displaying all folders the user owns
 * 
 * @author Renuchan
 */
public class TreeViewController {
    
    private DBManagerImp database;
    private QuickViewController QuickMailViewController;

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @FXML
    private TreeView<String> MailTreeView;
    
    @FXML
    ObservableList<String> allFolders; 

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        
    }

    /**
     * The Root calls this method to provide a reference to the
     * database
     * 
     * @param database
     * @throws SQLException
     */
    public void setDatabase(DBManagerImp database) {
            this.database = database;
    }
    
    /**
     * The Root calls this method to provide a reference to the
     * QuickViewController from which it can request a reference to the
     * TreeView. With theTreeView reference it can change the selection in the
     * TableView.
     * 
     * @param QuickMailViewController
     */
    public void setQuickViewController(QuickViewController QuickMailViewController) {
           this.QuickMailViewController = QuickMailViewController;
    }

    /**
     * Build the tree from the database
     * 
     * @throws SQLException
     */
    public void displayTree() throws SQLException {
        
        
        List<String> folders = database.getUsersFolders();
        
        MailTreeView.setRoot(new TreeItem(new String("Root")));

        // This cell factory is used to choose which field in the FihDta object
        // is used for the node name
        MailTreeView.setCellFactory((e) -> new TreeCell<String>(){
                
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                        setText(item);
                        setGraphic(getTreeItem().getGraphic());
                } else {
                        setText("");
                        setGraphic(null);
                }
            }
        });
       
        //convert the list into observable items 
        allFolders = FXCollections
                .observableArrayList();
        for(String fold : folders)
            allFolders.add(fold);
        
        

        // Build an item for each mail and add it to the root
        if (allFolders != null) {
            for (String fd : allFolders) {
                TreeItem<String> item = new TreeItem<>(fd);
                
                Image img = new Image("images/folders.png");           
                ImageView treeImg = new ImageView(img);
                item.setGraphic(treeImg);
                item.setValue(fd);             
                MailTreeView.getRoot().getChildren().add(item);
            }
        }

        // Open the tree
        MailTreeView.getRoot().setExpanded(true);

        // Listen for selection changes and show the fishData details when
        // changed.
        MailTreeView.getSelectionModel().selectedItemProperty()
                        .addListener((observable, oldValue, newValue) -> showMailDetailsTree(newValue));
        
        
}

    /**
     * Using the reference to the QuickMailViewController it can change the
     * selected row in the TableView It also displays all the emails that
     * corresponds to the selected node.
     * 
     * @param folder            name of the folder
     */
    private void showMailDetailsTree(TreeItem<String> folder) {

       if(folder != null)
       {
        String folderName = folder.getValue();
        try{
            QuickMailViewController.setFolderName(folderName);
            QuickMailViewController.displayTheTable();
        }
        catch(SQLException e)
        {
            System.out.println("TreeViewController: Failed to load mail in folders");
        }
       }
    }
    
}
