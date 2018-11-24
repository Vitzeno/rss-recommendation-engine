package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import model.RSSData;

public class MainController {
	
	private RSSData dataModel;
	
    @FXML
    private Label lblNews;
    @FXML
    private Button btnAdd;
    @FXML
    private ListView<String> lstViewFeed;
    
    
    @FXML
    void initialize() {
    	System.out.println("Controller initialize");
    	if(dataModel == null)
    		initModel();
    	
    	dataModel.parseRSSFeed("http://feeds.bbci.co.uk/news/rss.xml");
    	lstViewFeed.setItems(dataModel.getRSSList());
    }
    
    public void initModel() {
    	System.out.println("Initialising data model");
    	dataModel = new RSSData();
    }
}
