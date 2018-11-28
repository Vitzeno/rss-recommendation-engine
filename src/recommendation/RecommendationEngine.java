package recommendation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import feed.Feed;
import feed.FeedItem;
import feed.Reader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.RSSData;

/**
 * This class handles all the recommendations produced
 * @author Mohamed
 *
 */
public class RecommendationEngine {
	
	//Format RSS provides pubDate in, to be used to parse into date object
	String pattern = "EEE, dd MMM yyyy HH:mm:ss";
	SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
	//One hour in milliseconds
	private long hour = 3600;
	Date currentDate = new Date();
	
	
	private String LikedRSSFileName = "likedFeeds";
	private ArrayList<String> feedsURL = new ArrayList<String>();
	private RSSData RSSDataModel;
	private Reader reader = new Reader();
	
	
	private Feed recFeed = new Feed("Recommended Feed", null, "Auto generated feed of recommended items", "en-gb", null, null);
	private ObservableList<Feed> StandardFeedList = FXCollections.observableArrayList();
	private ObservableList<Feed> LikedFeedList = FXCollections.observableArrayList();
	
	//Date range is in hours
	private int dateRange = 24;
	private double dateWeighting = 0.5f; 
	private double likedFeedWeighting = 0.8f;
	private double likedAuthorWeighting = 0.5f;
	
	private boolean useDateWeighting = true;
	private boolean useLikedFeedWeighting = true;
	private boolean useLikedAuthorWeighting = true;

	
	
	
	public RecommendationEngine(ObservableList<Feed> RSSFeedList) {
		this.StandardFeedList = RSSFeedList;
		initLikedFeeds();
		for(Feed feed : LikedFeedList) {
			System.out.println("Liked feeds: " + feed);
		}
		
		for(Feed feed : StandardFeedList) {
			System.out.println("Standaerd feeds: " + feed);
		}
	}
	
	/**
	 * This method gets all the feeds users have liked from the liked
	 * feeds file
	 */
	public void initLikedFeeds() {
    	if(RSSDataModel == null)
    		initModel();
    	
    	feedsURL = reader.readFile(LikedRSSFileName);
    	for (String URL : feedsURL) {
    		LikedFeedList.add(RSSDataModel.parseRSSFeed(URL));
    	}
	}
	
	/**
	 * This is the main recommendation method which returns a list 
	 * of recommended feeds based on various metrics
	 * @return
	 */
	public Feed generateRecommendations() {
		
		for(Feed feed : LikedFeedList) {
			for(FeedItem item : feed.getMessages()) {
				recFeed.addToFeed(item);
			}
		}

		return recFeed;
	}
	
	
	
	
	public void printDate(FeedItem feedItem) {
		try {
			Date date = dateFormat.parse(feedItem.getPubDate());
			System.out.println("Parsed date is: " + date);
			System.out.println("Current date is: " + currentDate);
			
		} catch (ParseException e) {
			System.err.println("Error parse failed");
			e.printStackTrace();
		}
	}
	
    /*
     * This method only servers to initialise the RSSDataModel object
     */
    public void initModel() {
    	System.out.println("Initialising data model");
    	RSSDataModel = new RSSData();
    }

	public int getDateRange() {
		return dateRange;
	}

	public void setDateRange(int dateRange) {
		this.dateRange = dateRange;
	}

	public double getDateWeighting() {
		return dateWeighting;
	}

	public void setDateWeighting(double dateWeighting) {
		this.dateWeighting = dateWeighting;
	}

	public double getLikedFeedWeighting() {
		return likedFeedWeighting;
	}

	public void setLikedFeedWeighting(double likedFeedWeighting) {
		this.likedFeedWeighting = likedFeedWeighting;
	}

	public double getLikedAuthorWeighting() {
		return likedAuthorWeighting;
	}

	public void setLikedAuthorWeighting(double likedAuthorWeighting) {
		this.likedAuthorWeighting = likedAuthorWeighting;
	}

	public boolean isUseDateWeighting() {
		return useDateWeighting;
	}

	public void setUseDateWeighting(boolean useDateWeighting) {
		this.useDateWeighting = useDateWeighting;
	}

	public boolean isUseLikedFeedWeighting() {
		return useLikedFeedWeighting;
	}

	public void setUseLikedFeedWeighting(boolean useLikedFeedWeighting) {
		this.useLikedFeedWeighting = useLikedFeedWeighting;
	}

	public boolean isUseLikedAuthorWeighting() {
		return useLikedAuthorWeighting;
	}

	public void setUseLikedAuthorWeighting(boolean useLikedAuthorWeighting) {
		this.useLikedAuthorWeighting = useLikedAuthorWeighting;
	}
}
