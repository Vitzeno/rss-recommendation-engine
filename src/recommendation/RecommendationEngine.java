package recommendation;

import java.text.ParseException;
import feed.Feed;
import feed.FeedItem;
import javafx.collections.ObservableList;
import model.RSSData;
import utilities.ToolBox;

/**
 * This class handles all the recommendations produced
 * @author Mohamed
 *
 */
public class RecommendationEngine {
	
	//Format RSS provides pubDate in, to be used to parse into date object
	//private String pattern = "EEE, dd MMM yyyy HH:mm:ss";
	//private SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
	//One hour in milliseconds
	private long hour = 3600000;
	//private Date currentDate = new Date();
	
	
	private RSSData RSSDataModel;
	
	private Feed recFeed;
	
	
	//Date range is in hours
	private float dateRange = 24f;
	private double dateWeighting = 0.5f; 
	private double likedFeedWeighting = 0.8f;
	private double likedAuthorWeighting = 0.5f;
	
	private boolean useDateWeighting = true;
	private boolean useLikedFeedWeighting = true;
	private boolean useLikedAuthorWeighting = false;
	
	ToolBox toolBox = new ToolBox();
	
	
	public RecommendationEngine(ObservableList<Feed> RSSFeedList) {
		initModel();
	}
	

	/**
	 * This is the main recommendation method which returns a list 
	 * of recommended feeds based on various metrics
	 * @return a feed object containing recommended feed items
	 * @throws ParseException 
	 */
	public Feed generateRecommendations() throws ParseException {
		recFeed = new Feed("Recommended Feeds", null, "Auto generated feed of recommended items", "en-gb", null, null);
		
		RSSDataModel.initialiseFeedsMatrix();
		
		recFeed.addToFeed(RSSDataModel.setTFIDFScores());
		
		return recFeed;
	}
	
	/**
	 * This method uses a lambda expression to find if a string is contained
	 * within the getGuid method of the a function within a list.
	 * I this case the guid acts a unique identifier and the list is
	 * of liked feeds
	 * @param list List to search
	 * @param name GUID to search for
	 * @return returns true if found
	 */
	public boolean containsName(final ObservableList<FeedItem> list, final String name){
	    return list.stream().filter(o -> o.getGuid().equals(name)).findFirst().isPresent();
	}
	
	
	/**
	 * This method essentially returns a percentage value representing the time difference between
	 * the current time and the the publication time. All time calculations performed in
	 * milliseconds 
	 * @param timeGap this is the difference in time between the pubDate and current time
	 * @return returns a percentage value representing the time difference
	 */
	public float generateScoreForDateRange(long timeGap) {
		return Math.abs((timeGap/(dateRange * hour) * 100) -100);
	}
	
	
    /*
     * This method only servers to initialise the RSSDataModel object
     */
    public void initModel() {
    	System.out.println("Initialising recEngine data model");
    	RSSDataModel = RSSData.getInstance();
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
