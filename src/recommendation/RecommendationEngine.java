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
	private long hour = 3600000;
	Date currentDate = new Date();
	
	
	private String LikedRSSFileName = "likedFeeds";
	private ArrayList<String> feedsURL = new ArrayList<String>();
	private RSSData RSSDataModel;
	private Reader reader = new Reader();
	
	
	private Feed recFeed;
	private ObservableList<Feed> StandardFeedList = FXCollections.observableArrayList();
	private ObservableList<FeedItem> LikedFeedItemList = FXCollections.observableArrayList();
	
	
	//Date range is in hours
	private float dateRange = 24f;
	private double dateWeighting = 0.5f; 
	private double likedFeedWeighting = 0.8f;
	private double likedAuthorWeighting = 0.5f;
	
	private boolean useDateWeighting = true;
	private boolean useLikedFeedWeighting = true;
	private boolean useLikedAuthorWeighting = false;
	
	private int totalWeights = 0;
	private int thresholdValue = 20;

	
	
	
	public RecommendationEngine(ObservableList<Feed> RSSFeedList) {
		this.StandardFeedList = RSSFeedList;
		initLikedFeeds();
	}
	
	/**
	 * This method gets all the feeds users have liked from the liked
	 * feeds file
	 */
	public void initLikedFeeds() {
		ObservableList<Feed> LikedFeedList = FXCollections.observableArrayList();
		
    	if(RSSDataModel == null)
    		initModel();
    	
    	//TODO: Optimise to avoid iterating over list twice
    	feedsURL = reader.readFile(LikedRSSFileName);
    	for (String URL : feedsURL) {
    		LikedFeedList.add(RSSDataModel.parseRSSFeed(URL));
    	}
    	
    	for (Feed feed : LikedFeedList) {
    		for(FeedItem item : feed.getMessages()) {
    			LikedFeedItemList.add(item);
			}
    	}
	}
	
	/**
	 * This is the main recommendation method which returns a list 
	 * of recommended feeds based on various metrics
	 * @return a feed object containing recommended feed items
	 * @throws ParseException 
	 */
	public Feed generateRecommendations() throws ParseException {
		recFeed = new Feed("Recommended Feed", null, "Auto generated feed of recommended items", "en-gb", null, null);
		//float itemValue = 1f;
		setTotalWeights();
		
		for(Feed feed : StandardFeedList) {
			for(FeedItem item : feed.getMessages()) {
				float itemValue = 1f;
				Date date = dateFormat.parse(item.getPubDate());
				//If date within number of hours of date range
				if(currentDate.getTime() - date.getTime() <= dateRange * hour) {
					System.out.println("Date within 24: " + item.getPubDate());
					itemValue = generateScoreForDateRange(currentDate.getTime() - date.getTime());
					System.out.println("Item value after date calc: " + itemValue);
					itemValue *= dateWeighting;
				}
				
				
				
				itemValue /= totalWeights;
				System.out.println("Final Item value: " + itemValue);
				if(itemValue >= thresholdValue)
					recFeed.addToFeed(item);
			}
		}
		
		System.out.println("Total weigths: " + totalWeights);
		return recFeed;
	}
	
	/**
	 * This method essentially returns a percentage value representing the time difference between
	 * the current time and the the publication time
	 * @param timeGap
	 * @return
	 */
	public float generateScoreForDateRange(long timeGap) {
		return Math.abs((timeGap/(dateRange * hour) * 100) -100);
	}
	
	/**
	 * This helper function simply set the value of totalWeights
	 * to use in the calculations
	 */
	public void setTotalWeights() {
		totalWeights = 0;
		if(useDateWeighting) {
			totalWeights++;
		}
		if(useLikedFeedWeighting) {
			totalWeights++;
		}
		if(useLikedAuthorWeighting) {
			totalWeights++;
		}
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
    	System.out.println("Initialising recEngine data model");
    	RSSDataModel = new RSSData();
    }

	public float getDateRange() {
		return dateRange;
	}

	public void setDateRange(float dateRange) {
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
