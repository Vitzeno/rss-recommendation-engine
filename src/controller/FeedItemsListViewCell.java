package controller;

import java.io.IOException;
import database.DatabaseHandler;
import feed.FeedItem;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

public class FeedItemsListViewCell extends ListCell<FeedItem> {
	
    @FXML
    private Label lblDescription;
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblExtras;
    @FXML
    private Button btnLike;
    @FXML
    private GridPane gridPane;
    
    private FXMLLoader loader;
    
    DatabaseHandler DBHandler = new DatabaseHandler();
   

    @Override
    protected void updateItem(FeedItem item, boolean empty) {
        super.updateItem(item, empty);

        if(empty || item == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getClassLoader().getResource("FeedItemListViewCell.fxml"));
                loader.setController(this);

                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            lblTitle.setText(item.getTitle());;
            lblDescription.setText(item.getDescription());
            lblExtras.setText(item.getExraDecription());
            
            btnLike.setOnMouseClicked((event) -> {
            	FeedItem toAdd = getItem();
            	if(!toAdd.isSaved()) {
            		DBHandler.insertIntoLikedItemsTable(toAdd.getTitle(), toAdd.getDescription(), toAdd.getLink(), toAdd.getAuthor(), toAdd.getGuid(), toAdd.getPubDate());
                	System.out.println("Saving " + toAdd.getTitle());
                	toAdd.setSaved(true);
            	} else {
            		DBHandler.deleteFromLikedItemsTable(toAdd.getLink());
            		System.out.println("Removing " + toAdd.getTitle());
                	toAdd.setSaved(false);
            	}
            	updateItem(item, false);
        	});
            
            if(item.isSaved()) {
            	System.out.println("Saved item " + item.getTitle());
            	btnLike.setStyle("base-button-colour: #00C7FF");
            	btnLike.setText("Unsave");
            } else {
            	btnLike.setStyle("base-button-colour: white");
            	btnLike.setText("Save");
            }
            
            
            
            setText(null);
            setGraphic(gridPane);
        }
    }


}
