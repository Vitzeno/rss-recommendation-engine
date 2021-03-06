package controller;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
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
    private Label lblTitile;
    @FXML
    private TextField txtSearch;
    @FXML
    private RadioButton rdbLimitSearch;
    
    @FXML
    private ColorPicker clrPicker;
    @FXML
    private CheckBox chkIncreasedCoverage;
    
    
    @FXML
    private ComboBox<String> cmbReaderFont;
    @FXML
    private ComboBox<String> cmbReaderFontSize;
    @FXML
    private ComboBox<String> cmbOpenMode;
    @FXML
    private ComboBox<String> cmbTheme;
    
    @FXML
    private MenuItem cntMenuSave;
    
    
    @FXML
    private ListView<String> lstTopics;
    @FXML
    private Button btnAddTopic;
    @FXML
    private TextField txtTopic;
    
    @FXML
    private AnchorPane rightAnchorPane;
    @FXML
    private AnchorPane leftAnchorPane;
    
    Thread recThread = new Thread();
    
    Feed currentFeed = new Feed();
    
    /**
     * On click method when item in feed list is selected
     * @param event
     */
    @FXML
    void handleMouseClickF(MouseEvent event) {
    	lblTitile.setText(lstViewFeeds.getSelectionModel().getSelectedItem().getTitle());
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
    		initFeed();
    	}
    }
    
    @FXML
    void handleSaveFeedItem(ActionEvent event) {
    	DatabaseHandler DBHandler = new DatabaseHandler();
    	FeedItem toAdd = lstViewFeedItems.getSelectionModel().getSelectedItem();

		DBHandler.insertIntoLikedItemsTable(toAdd.getTitle(), toAdd.getDescription(), toAdd.getLink(), toAdd.getAuthor(), toAdd.getGuid(), toAdd.getPubDate());
    	System.out.println("Saving " + toAdd.getTitle());
    	toAdd.setSaved(true);

    	initFeed();
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
    
    @FXML
    void handleRefreshFeedItemsList(ActionEvent event) {
    	initFeedItems(currentFeed);
    }
    

    @FXML
    void handleRefreshTopicsList(ActionEvent event) {
    	initTopicsList();
    }
    
    
    @FXML
    void searchFeedItem(MouseEvent event) {
    	System.out.println("Search clicked");
    	String searchTerm = txtSearch.getText();
    	List<FeedItem> result = new ArrayList<FeedItem>();
    	
    	if(rdbLimitSearch.isSelected()) {
    		result = currentFeed.getMessages().stream()
			.filter(e -> 
			e.getTitle().toLowerCase().contains(searchTerm.toLowerCase()) || e.getDescription().toLowerCase().contains(searchTerm.toLowerCase()))
			.collect(Collectors.toList());
    		
    	} else {
    		result = RSSDataModel.getFeedItems().stream()
    				.filter(e -> 
    				e.getTitle().toLowerCase().contains(searchTerm.toLowerCase()) || e.getDescription().toLowerCase().contains(searchTerm.toLowerCase()))
    				.collect(Collectors.toList());
    	}
    	
		lstViewFeedItems.getItems().clear();
		lstViewFeedItems.getItems().addAll(result);
    }
    
    /**
     * This method servers to add feeds to the users list of saved feeds
     * @param event
     */
    @FXML
    void addFeed(MouseEvent event) {
    	DatabaseHandler DBHandler = new DatabaseHandler();
    	String toWirte = txtFeed.getText().toLowerCase();
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
    		initFeed();
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
    	    conn.setConnectTimeout(3000);
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
    	lstViewFeedItems.setCellFactory(feedItemsListView -> new FeedItemsListViewCell());
    	lstViewFeeds.setCellFactory(feedsListView -> new FeedListViewCell());
    	lstTopics.setCellFactory(topics -> new TopicsListViewCell());
    	initSettings();
    	
    	tabs.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
    	//Ensures default tabs are not closable
    	for(Tab tabs : tabs.getTabs())
    		tabs.setClosable(false);
    	
    	initFeed();
    	initTopicsList();
    	
    	/* Ensures that split pane divider does not resize when windows is resized */
    	SplitPane.setResizableWithParent(leftAnchorPane, false);
    	SplitPane.setResizableWithParent(rightAnchorPane, false);
    	splintPane.setDividerPositions(0.15);
    }
    
    /**
     * Gets and sets settings values from config file
     */
    public void initSettings() {
    	cmbReaderFont.getItems().clear();
    	cmbReaderFontSize.getItems().clear();
    	cmbOpenMode.getItems().clear();
    	cmbTheme.getItems().clear();
    	
    	cmbReaderFont.getItems().addAll("System", "Serif", "Segoe UI", "Calibri", "Calibri Light", "SansSerif", "Unispace");
    	cmbReaderFontSize.getItems().addAll("12", "14", "16", "18", "20", "22", "24", "26", "28", "30", "32", "34", "36", "38", "40", "42", "44", "46", "48", "50");
    	cmbOpenMode.getItems().addAll("Browser", "Reader");
    	cmbTheme.getItems().addAll("Modena", "Lapis Light");
    	
    	Settings settings = Settings.getSettings();
    	chkOpenInBrowse.setSelected(settings.isOpenInBrowser());
    	
    	cmbReaderFont.setValue(settings.getReaderFont());
    	cmbReaderFontSize.setValue("" + settings.getReaderFontSize());
    	
    	if(settings.openInBrowser)
    		cmbOpenMode.setValue("Browser");
    	else
    		cmbOpenMode.setValue("Reader");
    	
    	cmbTheme.setValue(settings.getReaderTheme());
    	
    	clrPicker.setValue(Color.web(settings.getAccentColour()));
    	
    	switch(settings.getReaderTheme()) {
		case "Verdant Dark":
			clrPicker.setDisable(true);
			break;
		case "Lapis Light":
			clrPicker.setDisable(false);
			break;
		case "Modena":
			clrPicker.setDisable(true);
			break;
		default:
			clrPicker.setDisable(false);
			break;
		}
    	
    	if(settings.isIncreasedCoverage())
    		chkIncreasedCoverage.setSelected(true);
    		
    		
    }
    
    /**
     * Saves new settings values to config file
     * @param event
     */
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
    	
    	settings.setReaderTheme(cmbTheme.getSelectionModel().getSelectedItem());
    	
    	settings.setAccentColour(ToolBox.cleanColour(clrPicker.getValue().toString()));
    	
    	if(chkIncreasedCoverage.isSelected())
    		settings.setIncreasedCoverage(true);
    	else
    		settings.setIncreasedCoverage(false);
    	
    	Settings.writeSettingsToFile(settings);
    	
    	Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Settings Saved");
		alert.setHeaderText("Settings Saved Sucessfully");
		alert.setContentText("New settings have been saved.");
		alert.showAndWait().ifPresent(rs -> {
		    if (rs == ButtonType.OK) {
		        System.out.println("Pressed OK.");
		    }
		});
    	
    	initSettings();
    }
    
    /**
     * This method initialises the list of feed items for each feed
     * @param feed
     */
    public void initFeedItems(Feed feed) {
    	currentFeed = feed;
    	lstViewFeedItems.getItems().clear();
    	for (FeedItem item : feed.getMessages()) {
    		lstViewFeedItems.getItems().add(item);
    	}
    	
    }
    
    /**
     * This method initialises the list view of feeds by parsing a URL containing an RSS
     * feed using the RSSDataModel class
     */
    @SuppressWarnings("deprecation")
	public void initFeed() {
    	DatabaseHandler DBHandler = new DatabaseHandler();
    	Settings settings = Settings.getSettings();
    	//add user topics to singleton class
    	UserTopics topics = UserTopics.getInstance();
    	topics.clearTerms();
		topics.addTerms(DBHandler.selectAllFromTopicsTable().toArray(new String[0]));
		System.out.println(topics);
		
		Feed savedFeeds = DBHandler.selectAllFromLikedItemsTable();
		
		if(recThread.isAlive()) {
    		System.out.println("Thread still alive");
    		recThread.stop();
    	}
    	
    	/* clear lstView then add feeds */
    	lstViewFeeds.getItems().clear();
    	RSSFeedList.clear();
    	

    	
    	/* init RSS data class*/
    	if(RSSDataModel == null)
    		initModel();
    	/* init rec engine class */
    	if(recEngine == null)
    		initRecEngine();  		
    	
    	if(settings.isIncreasedCoverage())
    		RSSDataModel.coverageScale = 0.75;
    	else
    		RSSDataModel.coverageScale = 0.5;
    	
		/* parse all feeds  */
    	RSSFeedList = RSSDataModel.parseRSSFeeds();
    	
    	/* if feeds are not empty populate table and generate recommendations */
    	if(!RSSFeedList.isEmpty()) {
    		lstViewFeeds.setItems(RSSFeedList);
    		recThread = new Thread(){
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
    		};
    		recThread.start();
    	}
		
    	/* populate saved feeds if they exist */
		if(!savedFeeds.getMessages().isEmpty()) 
			lstViewFeeds.getItems().add(0, savedFeeds);

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
    
    /**
     * Populates the topics table
     */
    public void initTopicsList() {
    	lstTopics.getItems().clear();
    	DatabaseHandler DBHandler = new DatabaseHandler();
    	ArrayList<String> topics = DBHandler.selectAllFromTopicsTable();
    	
    	if(!topics.isEmpty())
    		lstTopics.getItems().addAll(topics);
    }
    
    /**
     * Removes the selected topics from the database
     * @param event
     */
    @FXML
    void deleteTopic(ActionEvent event) {
    	DatabaseHandler DBHandler = new DatabaseHandler();
    	String toDelete = lstTopics.getSelectionModel().getSelectedItem();
    	
    	Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    	alert.setTitle("Remove Topic?");
    	alert.setContentText("Are you sure you wish to remove: " + toDelete);
    	Optional<ButtonType> answer = alert.showAndWait();
    	
    	if(answer.get() == ButtonType.OK) {
    		DBHandler.deleteFromTopicsTable(toDelete);
    		initTopicsList();
    	}
    }
   
    /**
     * Verifies then adds a topic to the database
     * @param event
     */
    @FXML
    void addTopic(MouseEvent event) {
    	DatabaseHandler DBHandler = new DatabaseHandler();
    	if(!txtTopic.getText().isEmpty()) {
	    	System.out.println("Topic added " + ToolBox.cleanString(txtTopic.getText()));
	    	DBHandler.insertIntoTopicsTable(ToolBox.cleanString(txtTopic.getText().toLowerCase()));
	    	
	    	Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Topic Added");
			alert.setHeaderText("Topic Added Sucessfully");
			alert.setContentText("Topics has been added and will be used in recommendations.");
			alert.showAndWait().ifPresent(rs -> {
			    if (rs == ButtonType.OK) {
			        System.out.println("Pressed OK.");
			    }
			});
    	} else {
    		Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Topic NOT Added");
			alert.setHeaderText("Topic Empty");
			alert.setContentText("Please enter a valid topic to add.");
			alert.showAndWait().ifPresent(rs -> {
			    if (rs == ButtonType.OK) {
			        System.out.println("Pressed OK.");
			    }
			});
    	}
    	
    	
		txtTopic.clear();
		initTopicsList();
    }
    
}
