package model;

import feed.Feed;
import feed.FeedItem;
import feed.RSSParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RSSData {
	
	private Feed feed;
	private RSSParser parser;
	private final ObservableList<String> RSSList = FXCollections.observableArrayList();
	
	public void parseRSSFeed(String url) {
		parser = new RSSParser(url);
        feed = parser.readFeed();
        System.out.println(feed);
        
        for (FeedItem item : feed.getMessages()) {
            System.out.println(item);
            RSSList.add(item.getTitle() + " | " + item.getDescription());
        }
	}
	
	public void addToList(String item) {
		RSSList.add(item);
	}
	
	public ObservableList<String> getRSSList() {
        return RSSList ;
    }

}
