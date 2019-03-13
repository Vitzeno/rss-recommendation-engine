package recommendation;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import feed.Feed;
import feed.FeedItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.RSSData;
import textClassification.TFIDFCalculator;
import textClassification.UserTopics;
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
	private ObservableList<Feed> StandardFeedList = FXCollections.observableArrayList();
	
	//Used for tfidf calculations
	private List<List<String>> documents = new ArrayList<List<String>>();
	
	
	//Date range is in hours
	private float dateRange = 24f;
	private double dateWeighting = 0.5f; 
	private double likedFeedWeighting = 0.8f;
	private double likedAuthorWeighting = 0.5f;
	
	private boolean useDateWeighting = true;
	private boolean useLikedFeedWeighting = true;
	private boolean useLikedAuthorWeighting = false;
	
	ToolBox toolBox = new ToolBox();
	
	
	/**
	 * This method utilises the tfidf class to
	 * generate a numerical score for each feed based on user interests
	 * as defined in the userTopics class.
	 * @param feed
	 */
	public void setTFIDFScoresPerFeed(Feed feed) {
		TFIDFCalculator tfidfCalc = new TFIDFCalculator();
		UserTopics topics = UserTopics.getInstance();
			
		for(FeedItem item : feed.getMessages()) {	
								
			for(String term : topics.getTerms()) {
				double tfidfScore = tfidfCalc.tfidf(item.getTokens(), documents, term);					
				
				if(tfidfScore > 0) {
					//System.out.print(item.getTitle() + " scored: " + tfidfScore);
					//System.err.println(" Because your interested in " + term);
					item.setScore(tfidfScore);
					item.appendDescription(" | Generated Score: " + tfidfScore);
					recFeed.addToFeed(item);
				}	
			}			
		}
	}

	
	
	public RecommendationEngine(ObservableList<Feed> RSSFeedList) {
		this.StandardFeedList = RSSFeedList;
		initModel();
		documents = RSSDataModel.getAllDocumentTokens();
		
		//RSSDataModel.printTokens();
		//RSSDataModel.printDocuments();	
		//RSSDataModel.printFeeds();
	}
	

	/**
	 * This is the main recommendation method which returns a list 
	 * of recommended feeds based on various metrics
	 * @return a feed object containing recommended feed items
	 * @throws ParseException 
	 */
	public Feed generateRecommendations() throws ParseException {
		recFeed = new Feed("Recommended Feed", null, "Auto generated feed of recommended items", "en-gb", null, null);
		
		RSSDataModel.initialiseFeedsMatrix();
		//RSSDataModel.setUpSimilarityMatrix();
		System.out.println("Continue on main");
		
		//toolBox.printMatrix(RSSDataModel.getFeedItems().get(5).getReducedMatrixValue());
		
		for(Feed feed : StandardFeedList) {
			setTFIDFScoresPerFeed(feed);	
		}
		
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
