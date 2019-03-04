package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import feed.Feed;
import feed.FeedItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import textClassification.Tokeniser;
import utilities.RSSParser;
import utilities.Reader;
import utilities.ToolBox;

/**
 * This class serves as the model, it handles all the data and parsing
 * @author Mohamed
 *
 */
public class RSSData {
	
	private static RSSData single_instance = null;
	
	private Reader reader = new Reader();
	private ToolBox toolBox = new ToolBox();
	
	//All feeds
	private ObservableList<Feed> Feeds = FXCollections.observableArrayList();
	private ArrayList<String> feedsURL = new ArrayList<String>();
	
	private String feedURLs;
	
	private List<List<String>> documents = new ArrayList<List<String>>();
	
	//All tokens in all feeds
	private List<String> tokens = new ArrayList<String>();
	private List<FeedItem> feedItems = new ArrayList<FeedItem>();
	
	
	/**
	 * private constructor ensures singleton nature of class
	 */
	private RSSData() {

	}
	
	/**
	 * Alternate Constructor to pass feeds url
	 * @param url
	 */
	private RSSData(String url) {
		this.feedURLs = url;
	}
	
	/**
	 * This method servers to provide an instance of the class
	 * and ensure that only one instance exists
	 * @return
	 */
	public static RSSData getInstance() {
		if(single_instance == null)
			single_instance = new RSSData();
		
		return single_instance;
	}
	
	/**
	 * This method servers to provide an instance of the class
	 * and ensure that only one instance exists, also servers
	 * as an alternate constructor for singleton nature
	 * of this class
	 * @return
	 */
	public static RSSData getInstance(String url) {
		if(single_instance == null)
			single_instance = new RSSData(url);
		
		return single_instance;
	}
	
	/**
	 * This method uses the RSSParser class to parse a list of RSS feeds from the provided URL
	 * containing a list of RSS url's
	 * @param url
	 */
	public ObservableList<Feed> parseRSSFeeds() {
		
		feedsURL = reader.readFile(feedURLs);
    	for (String URL : feedsURL) {
    		Feeds.add(parseRSSFeed(URL));
    	}
    	
    	tokeniseDocuments();
    	
		return Feeds;
	}
	
	/**
	 * This method uses the RSSParser class to parse an RSS feed from the provided URL
	 * @param url
	 * @return
	 */
	private Feed parseRSSFeed(String url) {
		Feed feed;
		RSSParser parser;
		
		parser = new RSSParser(url);
        feed = parser.readFeed();
        
        return feed;
	}
	
	/**
	 * This method servers to set up the list of document tokens
	 * to be used in tfidf calculations
	 */
	public void tokeniseDocuments() {
		Tokeniser tokeniser = new Tokeniser();
		
		for(Feed feed : Feeds) {
			for(FeedItem item : feed.getMessages()) {
				item.setTokens(tokeniser.getTokens(item.getTitle() + item.getDescription()));
				documents.add(item.getTokens());
			}
		}
		
		tokeniseAllTerms();
	}
	
	/**
	 * Creates a list of all tokens from the list of tokens in documents
	 */
	private void tokeniseAllTerms() {
		tokens  = documents.stream().flatMap(x -> x.stream()).collect(Collectors.toList());
	}
	
	public void printTokens() {
		toolBox.printTokens(tokens);
	}
	
	public void printDocuments() {
		toolBox.printDocuments(documents);
	}
	
	/**
	 * Simple method to print all feeds for debugging purposes
	 */
	public void printFeeds() {
		toolBox.printFeeds(Feeds);
	}
	
	public List<List<String>> getAllDocumentTokens() {
		return documents;
	}
	
	public List<FeedItem> getFeedItems() {
		for(Feed feeds : Feeds) {
			for(FeedItem item : feeds.getMessages()) {
				feedItems.add(item);
			}
		}
		return feedItems;
	}
	
	public List<Feed> getFeeds() {
		return Feeds;
	}

	public List<String> getTokens() {
		return tokens;
	}
	
	/**
	 * This method returns a list of items in a specific feed
	 * @param feed
	 * @return
	 */
	public Set<FeedItem> parseRSSFeedItems(Feed feed) {
		return feed.getMessages();
	}
	
}
