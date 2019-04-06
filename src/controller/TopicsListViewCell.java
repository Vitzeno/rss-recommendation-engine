package controller;

import java.io.IOException;
import java.util.Optional;
import database.DatabaseHandler;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class TopicsListViewCell extends ListCell<String> {

    @FXML
    private Label lblTopic;
    @FXML
    private Button btnDelete;
    @FXML
    private GridPane gridPane;
    
    private FXMLLoader loader;
    DatabaseHandler DBHandler = new DatabaseHandler();
    
    public TopicsListViewCell() {
    	super();
    	
    	setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {              
            	System.out.println("Topic to Delete: " + getItem());                
            }
        });
    	
	}
    
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

            lblTopic.setText(item);
            btnDelete.setOnMouseClicked((event) -> {
            	String toDelete = getItem();
            	Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            	alert.setTitle("Remove Topic?");
            	alert.setContentText("Are you sure you wish to remove: " + toDelete);
            	Optional<ButtonType> answer = alert.showAndWait();
            	
            	if(answer.get() == ButtonType.OK) {
            		DBHandler.deleteFromTopicsTable(toDelete);
            	}
        	});
            
            setText(null);
            setGraphic(gridPane);
        }
    }
}
