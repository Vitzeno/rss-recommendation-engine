package feed;

import java.util.List;
import java.util.TreeSet;

/**
 * This class servers as a collection of feed items, each feed has a set of attributes which are
 * set up in the constructor
 * @author Mohamed
 *
 */
public class Feed {
	
	final String title;
    final String link;
    final String description;
    final String language;
    final String copyright;
    final String pubDate;

    final TreeSet<FeedItem> items = new TreeSet<FeedItem>();

    /**
     * The constructor takes a set of feed attributes and sets them up
     * @param title
     * @param link
     * @param description
     * @param language
     * @param copyright
     * @param pubDate
     */
    public Feed(String title, String link, String description, String language, String copyright, String pubDate) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.language = language;
        this.copyright = copyright;
        this.pubDate = pubDate;
    }
    
    public Feed() {
    	this.title = "";
        this.link = "";
        this.description = "";
        this.language = "";
        this.copyright = "";
        this.pubDate = "";
    }
    
    /**
     * This method returns an ArrayList of feed items
     * @return List of feed items
     */
    public TreeSet<FeedItem> getMessages() {
        return items;
    }
    
    /**
     * This method adds a feed item to the list of feed items
     * within the current feed
     * @param feedItem
     */
    public void addToFeed(FeedItem feedItem) {
    	this.items.add(feedItem);
    }
    
    /**
     * This method adds a list of feed items in the form
     * of a feed object to the feed
     * @param feed
     */
    public void addToFeed(Feed feed) {
    	for(FeedItem item : feed.getMessages()) {
    		this.items.add(item);
    	}
    }
    
    /**
     * Adds a list of feed items to the feed
     * @param items
     */
    public void addToFeed(List<FeedItem> items) {
    	for(FeedItem item : items) {
    		this.items.add(item);
    	}
    }
    
    /** 
     * This method returns the title of the feed
     * @return Feed Title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * This method returns the feed link
     * @return link to feed 
     */
    public String getLink() {
        return link;
    }
    
    /**
     * This method returns the feed description
     * @return feed description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * This method returns the feed language
     * @return feed language
     */
    public String getLanguage() {
        return language;
    }
    
    /**
     * This method returns the feed copyright data
     * @return feed copyright data
     */
    public String getCopyright() {
        return copyright;
    }
    
    /**
     * This method returns the feed publication data
     * @return feed publication data
     */
    public String getPubDate() {
        return pubDate;
    }
    
    /**
     * This method determines what to return when the toString method is invoked on the feed object
     */
    @Override
    public String toString() {
        //return "Feed [copyright=" + copyright + ", description=" + description + ", language=" + language + ", link=" + link + ", pubDate=" + pubDate + ", title=" + title + "]";
    	return title;
    }
    
}
