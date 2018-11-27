package controller;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import feed.Feed;
import feed.FeedItem;
import feed.Reader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.RSSData;
import javafx.scene.Node;

/**
 * This class servers as the main view controller
 * @author Mohamed
 *
 */
public class MainController {
	
	private String RSSFileName = "myFeeds";
	private RSSData RSSDataModel;
	private ArrayList<String> feedsURL = new ArrayList<String>();
	private ObservableList<Feed> RSSFeedList = FXCollections.observableArrayList();
	private Reader reader = new Reader();
	
	//private HashMap<Feed, List<FeedItem>> feedToList = new HashMap<Feed, List<FeedItem>>();
	
    @FXML
    private Label lblNews;
    @FXML
    private Button btnAdd;
    @FXML
    private TextField txtFeed;
    @FXML
    private ListView<Feed> lstViewFeedTitles;
    @FXML
    private ListView<FeedItem> lstViewFeed;
    @FXML
    private CheckBox chkOpenInBrowse;
    
    
    /**
     * On click method when item in feed list is selected
     * @param event
     */
    @FXML
    void handleMouseClickF(MouseEvent event) {
    	initFeedItems(lstViewFeedTitles.getSelectionModel().getSelectedItem());
    }
   
    /**
     * On click method when item in feed items list is clicked on, this then opens item in
     * browser window
     * @param event
     * @throws IOException 
     */
    @FXML
    void handleMouseClickFI(MouseEvent event) throws IOException {  	   
    	String link = lstViewFeed.getSelectionModel().getSelectedItem().getLink();
    	if(chkOpenInBrowse.isSelected()) {
        	
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
        else {
        	FXMLLoader loader = new FXMLLoader();
        	loader.setLocation(getClass().getClassLoader().getResource("BrowserView.fxml"));
        	
        	Parent browserViewParent = loader.load();
        	
        	//access the controller and call a method
            BrowserController controller = loader.getController();
            System.out.println("Link passed: " + link);
            controller.load(link);
        	
        	Scene browserViewScene = new Scene(browserViewParent);
        	Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        	
        	window.setScene(browserViewScene);
            window.show();
        }
    }
    
    /**
     * This method servers to add feeds to the users list of saved feeds
     * @param event
     */
    @FXML
    void addFeed(MouseEvent event) {
    	String toWirte = txtFeed.getText();
    	if(validateURI(txtFeed.getText())) {
    		System.out.println("Adding feed: " + toWirte);
    		txtFeed.setStyle("-fx-text-fill: green;");
    		reader.appendFile(RSSFileName, toWirte);
    		initialize();
    	}	
    	else {
    		System.out.println("Malformed URL provided");
    		txtFeed.setText("Malformed URL provided");
    		txtFeed.setStyle("-fx-text-fill: red;");
    	}
    }
    
    /**
     * This method server to verify if the URI provided is syntactically valid or not
     * @param uri
     * @return
     */
    private boolean validateURI(String url) {
    	try { 
            new URL(url).toURI(); 
            return true; 
        } 
        catch (Exception e) { 
            return false; 
        } 
    }
    
    /**
     * This method initialises the scene
     */
    @FXML
    void initialize() {
    	System.out.println("Controller initialise");
    	initFeed();
    }
    
    
    /**
     * This method initialises the list of feed items for each feed
     * @param feed
     */
    public void initFeedItems(Feed feed) {
    	//System.out.println(feedToList.get(feed));
    	lstViewFeed.getItems().clear();
    	for (FeedItem item : feed.getMessages()) {
    		lstViewFeed.getItems().add(item);
    	}
    }
    
    /**
     * This method initialises the list view of feeds by parsing a URL containing an RSS
     * feed using the RSSDataModel class
     */
    public void initFeed() {
    	lstViewFeedTitles.getItems().clear();
    	RSSFeedList.clear();
    	if(RSSDataModel == null)
    		initModel();
    	
    	feedsURL = reader.readFile(RSSFileName);
    	for (String URL : feedsURL) {
    		RSSFeedList.add(RSSDataModel.parseRSSFeed(URL));
    	}
    	
    	for (Feed feed : RSSFeedList) {
    		//feedToList.put(feed, feed.getMessages());
    		lstViewFeedTitles.getItems().add(feed);
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
