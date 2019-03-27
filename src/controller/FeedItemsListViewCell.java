package controller;

import java.io.IOException;

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
            
            setText(null);
            setGraphic(gridPane);
        }
    }

}
