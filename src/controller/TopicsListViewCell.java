package controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

public class TopicsListViewCell extends ListCell<String> {

    @FXML
    private Label lblTopic;
    @FXML
    private Button btnDelete;
    @FXML
    private GridPane gridPane;
    
    private FXMLLoader loader;
    
    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if(empty || item == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getClassLoader().getResource("TopicsListViewCell.fxml"));
                loader.setController(this);

                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            lblTopic.setText(item);;
            
            setText(null);
            setGraphic(gridPane);
        }
    }
}
