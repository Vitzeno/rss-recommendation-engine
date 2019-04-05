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
import javafx.application.Platform;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.RSSData;
import recommendation.RecommendationEngine;
import settings.Settings;
import textClassification.UserTopics;
import utilities.ToolBox;


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
    private ListView<Feed> lstViewFeeds;
    @FXML
    private ListView<FeedItem> lstViewFeedItems;
    @FXML
    private CheckBox chkOpenInBrowse;
    @FXML
    private Button btnSave;
    @FXML
    private TabPane tabs;
    @FXML
    private AnchorPane root;
    @FXML
    private SplitPane splintPane;
    
    
    @FXML
    private ComboBox<String> cmbReaderFont;
    @FXML
    private ComboBox<String> cmbReaderFontSize;
    @FXML
    private ComboBox<String> cmbOpenMode;
    
    
    @FXML
    private AnchorPane rightAnchorPane;
    @FXML
    private AnchorPane leftAnchorPane;
    

    
    /**
     * On click method when item in feed list is selected
     * @param event
     */
    @FXML
    void handleMouseClickF(MouseEvent event) {
		if(event.getButton() == MouseButton.PRIMARY) {
			initFeedItems(lstViewFeeds.getSelectionModel().getSelectedItem());
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
    		String link = lstViewFeedItems.getSelectionModel().getSelectedItem().getLink();
        	String pubDate = lstViewFeedItems.getSelectionModel().getSelectedItem().getPubDate();
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
                newTab.setText(lstViewFeedItems.getSelectionModel().getSelectedItem().getTitle());
            }
    	}
    	
    }
    
    @FXML
    void handleRemoveFeed(ActionEvent event) {
    	DatabaseHandler DBHandler = new DatabaseHandler();
    	Feed toDelete = lstViewFeeds.getSelectionModel().getSelectedItem();
    	
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
    	String link = lstViewFeedItems.getSelectionModel().getSelectedItem().getLink();
    	String pubDate = lstViewFeedItems.getSelectionModel().getSelectedItem().getPubDate();
        	
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
    	initFeed();
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
    		txtFeed.clear();
    	}	
    	else {
    		System.out.println("Malformed URL provided");
    		//txtFeed.setText("Malformed URL provided");
    		txtFeed.setStyle("-fx-text-fill: red;");
    	}
    }
    
    /**
     * This method serves to verify if the URI provided is syntactically valid or not
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
    	initSettings();
    	
    	tabs.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
    	//Ensures default tabs are not closable
    	for(Tab tabs : tabs.getTabs())
    		tabs.setClosable(false);
    	
    	initFeed();
    	
    	/* Ensures that split pane divider does not resize when windows is resized */
    	SplitPane.setResizableWithParent(leftAnchorPane, false);
    	SplitPane.setResizableWithParent(rightAnchorPane, false);
    	splintPane.setDividerPositions(0.20);
    	

    }
    
    
    public void initSettings() {
    	cmbReaderFont.getItems().clear();
    	cmbReaderFontSize.getItems().clear();
    	cmbOpenMode.getItems().clear();
    	
    	cmbReaderFont.getItems().addAll("System", "Serif", "Segoe UI", "Calibri", "Calibri Light", "SansSerif", "Unispace");
    	cmbReaderFontSize.getItems().addAll("12", "14", "16", "18", "20", "22", "24", "26", "28", "30", "32", "34", "36", "38", "40", "42", "44", "46", "48", "50");
    	cmbOpenMode.getItems().addAll("Browser", "Reader");
    	
    	Settings settings = Settings.getSettings();
    	chkOpenInBrowse.setSelected(settings.isOpenInBrowser());
    	
    	cmbReaderFont.setValue(settings.getReaderFont());
    	cmbReaderFontSize.setValue("" + settings.getReaderFontSize());
    	if(settings.openInBrowser)
    		cmbOpenMode.setValue("Browser");
    	else
    		cmbOpenMode.setValue("Reader");

    }
    

    @FXML
    void saveSettings(MouseEvent event) {
    	Settings settings = Settings.getSettings();
    	
    	settings.setReaderFont(cmbReaderFont.getSelectionModel().getSelectedItem());
    	
    	if(ToolBox.intToString(cmbReaderFontSize.getSelectionModel().getSelectedItem()) == -1)
    		settings.setReaderFontSize(20);
    	else
    		settings.setReaderFontSize(ToolBox.intToString(cmbReaderFontSize.getSelectionModel().getSelectedItem()));
    	
    	if(cmbOpenMode.getSelectionModel().getSelectedItem().equalsIgnoreCase("browser")) 
    		settings.setOpenInBrowser(true);
    	else 
    		settings.setOpenInBrowser(false);
    	
    	Settings.writeSettingsToFile(settings);
    	initSettings();
    }
    
    /**
     * This method initialises the list of feed items for each feed
     * @param feed
     */
    public void initFeedItems(Feed feed) {
    	lstViewFeedItems.getItems().clear();
    	for (FeedItem item : feed.getMessages()) {
    		lstViewFeedItems.getItems().add(item);
    	}
    	
    	lstViewFeedItems.setCellFactory(feedItemsListView -> new FeedItemsListViewCell());
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
    	lstViewFeeds.getItems().clear();
    	RSSFeedList.clear();
    	if(RSSDataModel == null)
    		initModel();
    	
    	RSSFeedList = RSSDataModel.parseRSSFeeds();
    	
    	lstViewFeeds.setItems(RSSFeedList);
    	 	
    	if(recEngine == null)
    		initRecEngine();
    	
    	new Thread(){
			@Override
			public void run() {
				try {
					Feed recFeed = recEngine.generateRecommendations();
					Platform.runLater( () -> {
						lstViewFeeds.getItems().add(0, recFeed);
			    	});
				} catch (ParseException e) {
					Alert alert = new Alert(AlertType.ERROR);
		    		alert.setTitle("Error");
		    		alert.setHeaderText("Unable to generate recommendations");
		    		alert.setContentText("This could be due to a drop in connection, or a failed RSS parse");
		    		alert.showAndWait().ifPresent(rs -> {
		    		    if (rs == ButtonType.OK) {
		    		        System.out.println("Pressed OK.");
		    		    }
		    		});
					e.printStackTrace();
				}
			}
		}.start();
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
