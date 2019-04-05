package controller;

import java.io.IOException;
import feed.Feed;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class FeedListViewCell extends ListCell<Feed> {
	
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblDescription;
    @FXML
    private GridPane gridPane;
    
    private FXMLLoader loader;

    @Override
    protected void updateItem(Feed item, boolean empty) {
        super.updateItem(item, empty);

        if(empty || item == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getClassLoader().getResource("FeedListViewCell.fxml"));
                loader.setController(this);

                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(item.getTitle().equalsIgnoreCase("Recommended Feeds"))
            	lblTitle.setTextFill(Color.web("#DC143C"));
            else
            	lblTitle.setTextFill(Color.web("#000000"));
            
            lblTitle.setText(item.getTitle());;
            lblDescription.setText(item.getDescription());
            
            setText(null);
            setGraphic(gridPane);
        }
    }
}
