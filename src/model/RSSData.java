package model;

import feed.Feed;
import feed.FeedItem;
import feed.RSSParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class serves as the model, it handles all the data and parsing
 * @author Mohamed
 *
 */
public class RSSData {
	
	private Feed feed;
	private RSSParser parser;
	//ObservableList used to ensure compatibility with list view
	private final ObservableList<FeedItem> RSSList = FXCollections.observableArrayList();
	
	/**
	 * This method uses the RSSParser class to parse a RSS feed from the provided URL
	 * @param url
	 */
	public void parseRSSFeed(String url) {
		parser = new RSSParser(url);
        feed = parser.readFeed();
        System.out.println(feed);
        
        for (FeedItem item : feed.getMessages()) {
            //System.out.println(item);
            //RSSList.add(item.getTitle() + " | " + item.getDescription());
            RSSList.add(item);
        }
	}
	
	/**
	 * This method returns the RSS list once it has been parsed
	 * @return
	 */
	public ObservableList<FeedItem> getRSSList() {
        return RSSList;
    }

}
