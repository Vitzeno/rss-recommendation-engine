package controller;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import feed.Feed;
import feed.FeedItem;
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
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.RSSData;
import recommendation.RecommendationEngine;
import utilities.HTMLParser;
import utilities.Reader;
import javafx.scene.Node;

/**
 * This class servers as the main view controller
 * @author Mohamed
 *
 */
public class MainController {
	
	private String RSSFileName = "standardFeeds";
	private RSSData RSSDataModel;
	private ArrayList<String> feedsURL = new ArrayList<String>();
	private ObservableList<Feed> RSSFeedList = FXCollections.observableArrayList();
	private Reader reader = new Reader();
	
	private RecommendationEngine recEngine;
	
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
    @FXML
    private CheckBox chkFeedDate;
    @FXML
    private CheckBox chkLikedFeeds;
    @FXML
    private CheckBox chkLikedAuthors;
    @FXML
    private Slider sldDateRange;
    @FXML
    private Slider sldLikedFeeds;
    @FXML
    private Slider sldLikedAuthors;
    @FXML
    private TextField txtDateRange;
    @FXML
    private Button btnSave;
    
    
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
    	String pubDate = lstViewFeed.getSelectionModel().getSelectedItem().getPubDate();
    	if(chkOpenInBrowse.isSelected()) {
        	
            System.out.println("Clicked on " + link + pubDate);
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
            //System.out.println("Link passed: " + link);
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
    	chkOpenInBrowse.setSelected(true);
    	initFeed();
    	setUpSettingsValues();

    	//initFeed(recommended);
    }
    
    /**
     * This method uses the recEngine object, therefore it must called
     * after that object is initialised to avoid errors. In this case it 
     * must be called after initFeed()
     */
    public void setUpSettingsValues() {
    	sldDateRange.setMax(1);
    	sldDateRange.setMin(0);
    	sldDateRange.setValue(recEngine.getDateWeighting());
    	
    	txtDateRange.setText(String.valueOf(recEngine.getDateRange()));
    	
    	sldLikedFeeds.setMax(1);
    	sldLikedFeeds.setMin(0);
    	sldLikedFeeds.setValue(recEngine.getLikedFeedWeighting());
    	
    	sldLikedAuthors.setMax(1);
    	sldLikedAuthors.setMin(0);
    	sldLikedAuthors.setValue(recEngine.getLikedAuthorWeighting());
    	
    	chkFeedDate.setSelected(recEngine.isUseDateWeighting());
    	chkLikedFeeds.setSelected(recEngine.isUseLikedFeedWeighting());
    	chkLikedAuthors.setSelected(recEngine.isUseLikedAuthorWeighting());
    }
    
    /**
     * This method gets the data from the settings view and passes it 
     * to the recommendation object
     * @param event
     */
    @FXML
    void saveRecommendationSettings(MouseEvent event) {
    	recEngine.setUseDateWeighting(chkFeedDate.isSelected());
    	recEngine.setDateWeighting(sldDateRange.getValue());
    	recEngine.setDateRange(Integer.parseInt(txtDateRange.getText()));
    	
    	recEngine.setUseLikedFeedWeighting(chkLikedFeeds.isSelected());
    	recEngine.setLikedFeedWeighting(sldLikedFeeds.getValue());
    	
    	recEngine.setUseLikedAuthorWeighting(chkLikedAuthors.isSelected());
    	recEngine.setLikedAuthorWeighting(sldLikedAuthors.getValue());
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
    	
    	if(recEngine == null)
    		initRecEngine();
    	
    	try {
			lstViewFeedTitles.getItems().add(recEngine.generateRecommendations());
		} catch (ParseException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * This method only serves to  initialise the RecommendationEngine object
     */
    public void initRecEngine() {
    	System.out.println("Initialising recEngine model");
    	recEngine = new RecommendationEngine(RSSFeedList);
    }
  
    /*
     * This method only servers to initialise the RSSDataModel object
     */
    public void initModel() {
    	System.out.println("Initialising data model");
    	RSSDataModel = new RSSData();
    }
}
