package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import model.RSSData;

/**
 * This class servers as the main view controller
 * @author Mohamed
 *
 */
public class MainController {
	
	private RSSData RSSDataModel;
	private String RSSURL = "http://feeds.bbci.co.uk/news/rss.xml";
	
    @FXML
    private Label lblNews;
    @FXML
    private Button btnAdd;
    @FXML
    private ListView<String> lstViewFeed;
   
    @FXML
    void handleMouseClick(MouseEvent event) {
        System.out.println("Clicked on " + lstViewFeed.getSelectionModel().getSelectedItem());
    }
    
    
    /**
     * This method initialises the list view by parsing a URL containing an RSS
     * feed using the RSSDataModel class
     */
    @FXML
    void initialize() {
    	System.out.println("Controller initialize");
    	if(RSSDataModel == null)
    		initModel();
    	
    	RSSDataModel.parseRSSFeed(RSSURL);
    	lstViewFeed.setItems(RSSDataModel.getRSSList());
    }
    
    /*
     * This method only servers to initialise the RSSDataModel object
     */
    public void initModel() {
    	System.out.println("Initialising data model");
    	RSSDataModel = new RSSData();
    }
}
