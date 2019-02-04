package model;



import java.util.Set;
import feed.Feed;
import feed.FeedItem;
import utilities.RSSParser;

/**
 * This class serves as the model, it handles all the data and parsing
 * @author Mohamed
 *
 */
public class RSSData {
	
	private Feed feed;
	private RSSParser parser;
	//ObservableList used to ensure compatibility with list view
	//private final ObservableList<FeedItem> RSSList = FXCollections.observableArrayList();
	
	/**
	 * This method uses the RSSParser class to parse a RSS feed from the provided URL
	 * @param url
	 */
	public Feed parseRSSFeed(String url) {
		parser = new RSSParser(url);
        feed = parser.readFeed();
        
        //System.out.println(feed);
        
        return feed;
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
