package controller;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import feed.FeedItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
	private ObservableList<FeedItem> RSSList = FXCollections.observableArrayList();
	
    @FXML
    private Label lblNews;
    @FXML
    private Button btnAdd;
    @FXML
    private ListView<FeedItem> lstViewFeed;
   
    /**
     * On click method when item in list is clicked on
     * @param event
     */
    @FXML
    void handleMouseClick(MouseEvent event) {
    	String link = lstViewFeed.getSelectionModel().getSelectedItem().getLink();
        System.out.println("Clicked on " + link);
        Desktop desktop = Desktop.getDesktop();
        try {
			desktop.browse(new URI(link));
		} catch (IOException e) {
			System.err.println("IO Error");
			e.printStackTrace();
		} catch (URISyntaxException e) {
			System.err.println("Invalid URI syntax");
			e.printStackTrace();
		}
    }
    
    
    /**
     * This method initialises the list view by parsing a URL containing an RSS
     * feed using the RSSDataModel class
     */
    @FXML
    void initialize() {
    	System.out.println("Controller initialise");
    	if(RSSDataModel == null)
    		initModel();
    	
    	RSSDataModel.parseRSSFeed(RSSURL);
    	RSSList = RSSDataModel.getRSSList();
    	
    	for (FeedItem item : RSSList) {
    		lstViewFeed.getItems().add(item);
        }
        
    }
    
    /*
     * This method only servers to initialise the RSSDataModel object
     */
    public void initModel() {
    	System.out.println("Initialising data model");
    	RSSDataModel = new RSSData();
    }
}
