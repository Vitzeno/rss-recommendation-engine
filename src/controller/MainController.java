package controller;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.Optional;
import database.DatabaseHandler;
import feed.Feed;
import feed.FeedItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.RSSData;
import recommendation.RecommendationEngine;
import textClassification.UserTopics;


/**
 * This class servers as the main view controller
 * @author Mohamed
 *
 */
public class MainController {
	
	private RSSData RSSDataModel;
	private ObservableList<Feed> RSSFeedList = FXCollections.observableArrayList();
	
	private RecommendationEngine recEngine;
	
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
    @FXML
    private TabPane tabs;
    
    /**
     * On click method when item in feed list is selected
     * @param event
     */
    @FXML
    void handleMouseClickF(MouseEvent event) {
		if(event.getButton() == MouseButton.PRIMARY) {
			initFeedItems(lstViewFeedTitles.getSelectionModel().getSelectedItem());
		}
    }
   
    /**
     * On click method when item in feed items list is clicked on, this then opens item in
     * browser window
     * @param event
     * @throws IOException 
     */
    @FXML
    void handleMouseClickFI(MouseEvent event) throws IOException {  
    	if(event.getButton() == MouseButton.PRIMARY) {
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
                controller.load(link);
            	
                Tab newTab = new Tab();
                tabs.getTabs().add(newTab);
                newTab.setContent(browserViewParent);
                newTab.setClosable(true);
                newTab.setText(lstViewFeed.getSelectionModel().getSelectedItem().getTitle());
            }
    	}
    	
    }
    
    @FXML
    void handleRemoveFeed(ActionEvent event) {
    	DatabaseHandler DBHandler = new DatabaseHandler();
    	Feed toDelete = lstViewFeedTitles.getSelectionModel().getSelectedItem();
    	
    	Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    	alert.setTitle("Remove Feed?");
    	alert.setContentText("Are you sure you wish to remove: " + toDelete.getTitle());
    	Optional<ButtonType> answer = alert.showAndWait();
    	
    	if(answer.get() == ButtonType.OK) {
    		DBHandler.deleteFromFeedTable(toDelete.getUrl());
    	}
    }
    
    @FXML
    void handleOpenFeedItem(ActionEvent event) {
    	String link = lstViewFeed.getSelectionModel().getSelectedItem().getLink();
    	String pubDate = lstViewFeed.getSelectionModel().getSelectedItem().getPubDate();
        	
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
    
    @FXML
    void handleRefreshFeedList(ActionEvent event) {
    	//initialize();
    }
    
    /**
     * This method servers to add feeds to the users list of saved feeds
     * @param event
     */
    @FXML
    void addFeed(MouseEvent event) {
    	DatabaseHandler DBHandler = new DatabaseHandler();
    	String toWirte = txtFeed.getText();
    	if(validateURI(txtFeed.getText())) {
    		System.out.println("Adding feed: " + toWirte);
    		txtFeed.setStyle("-fx-text-fill: green;");
    		DBHandler.insertIntoFeedsTable(toWirte);
    		//reader.appendFile(RSSFileName, toWirte);
    		
    		Alert alert = new Alert(AlertType.CONFIRMATION);
    		alert.setTitle("Feed Added");
    		alert.setHeaderText("Feed Added Sucessfully");
    		alert.setContentText("RSS feed has been saved to your list.");
    		alert.showAndWait().ifPresent(rs -> {
    		    if (rs == ButtonType.OK) {
    		        System.out.println("Pressed OK.");
    		    }
    		});
    		
    		
    		//initialize();
    	}	
    	else {
    		System.out.println("Malformed URL provided");
    		//txtFeed.setText("Malformed URL provided");
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
    	    URL urlTest = new URL(url);
    	    URLConnection conn = urlTest.openConnection();
    	    conn.connect();
    	} catch (MalformedURLException e) {
    		txtFeed.clear();
    		Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Error");
    		alert.setHeaderText("Malformed URL");
    		alert.setContentText("URL provided is malformed, please check again and enter a well formed url");
    		alert.showAndWait().ifPresent(rs -> {
    		    if (rs == ButtonType.OK) {
    		        System.out.println("Pressed OK.");
    		    }
    		});
    		e.printStackTrace();
    		return false;
    	} catch (IOException e) {
    		txtFeed.clear();
    		Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Error");
    		alert.setHeaderText("Connection Error");
    		alert.setContentText("Connection to the provided URL cannot be establish, this may be cause by lack of internet connection or invalid URL");
    		alert.showAndWait().ifPresent(rs -> {
    		    if (rs == ButtonType.OK) {
    		        System.out.println("Pressed OK.");
    		    }
    		});
			e.printStackTrace();
			return false;
		}
		return true; 
    }
    
    /**
     * This method initialises the scene
     */
    @FXML
    void initialize() {
    	System.out.println("Controller initialise");
    	//chkOpenInBrowse.setSelected(true);
    	
    	tabs.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
    	//Ensures default tabs are not closable
    	for(Tab tabs : tabs.getTabs())
    		tabs.setClosable(false);
    	
    	initFeed();
    	setUpSettingsValues();
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
    	DatabaseHandler DBHandler = new DatabaseHandler();
    	//add user topics to singleton class
    	UserTopics topics = UserTopics.getInstance();
		topics.addTerms(DBHandler.selectAllFromTopicsTable().toArray(new String[0]));
		System.out.println(topics);
		
    	
    	//clear lstView then add feeds
    	lstViewFeedTitles.getItems().clear();
    	RSSFeedList.clear();
    	if(RSSDataModel == null)
    		initModel();
    	
    	RSSFeedList = RSSDataModel.parseRSSFeeds();
    	
    	for (Feed feed : RSSFeedList) {
    		lstViewFeedTitles.getItems().add(feed);
        } 
    	 	
    	if(recEngine == null)
    		initRecEngine();
    	
    	try {
			lstViewFeedTitles.getItems().add(0, recEngine.generateRecommendations());
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
    	RSSDataModel = RSSData.getInstance();
    }
}
