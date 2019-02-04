package recommendation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import feed.Feed;
import feed.FeedItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.RSSData;
import textClassification.TFIDFCalculator;
import textClassification.Tokeniser;
import textClassification.UserTopics;
import utilities.Reader;

/**
 * This class handles all the recommendations produced
 * @author Mohamed
 *
 */
public class RecommendationEngine {
	
	//Format RSS provides pubDate in, to be used to parse into date object
	private String pattern = "EEE, dd MMM yyyy HH:mm:ss";
	private SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
	//One hour in milliseconds
	private long hour = 3600000;
	private Date currentDate = new Date();
	
	
	private String LikedRSSFileName = "likedFeeds";
	private ArrayList<String> feedsURL = new ArrayList<String>();
	private RSSData RSSDataModel;
	private Reader reader = new Reader();
	
	
	private Feed recFeed;
	private ObservableList<Feed> StandardFeedList = FXCollections.observableArrayList();
	private ObservableList<FeedItem> LikedFeedItemList = FXCollections.observableArrayList();
	
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
	
	private int totalWeights = 0;
	//This is the value that determines if a item should be entered into the feed
	//TODO: Think about exposing this value in the UI to allow for some altering
	private float thresholdValue = 20;
	
	
	/**
	 * This method servers to set up the list of document tokens
	 * to be used in tfidf calulations
	 */
	public void initDocumentsList() {
		Tokeniser tokeniser = new Tokeniser();
		
		for(Feed feed : StandardFeedList) {
			for(FeedItem item : feed.getMessages())
				documents.add(tokeniser.getTokens(item.getTitle() + item.getDescription()));
		}
		
		System.out.println(documents);
		System.out.println("Feed size " + documents.size());
	}
	
	/**
	 * This method utilises the tokeniser and tfidf classes to
	 * generate a numerical score for each feed based on user interests
	 * as defined in the userTopics class.
	 * @param feed
	 */
	public void setTFIDFScoresPerFeed(Feed feed) {
		Tokeniser tokeniser = new Tokeniser();
		TFIDFCalculator tfidfCalc = new TFIDFCalculator();
		UserTopics topics = UserTopics.getInstance();
		
		List<String> document = new ArrayList<String>();
			
		for(FeedItem item : feed.getMessages()) {	
			document = tokeniser.getTokens(item.getTitle() + item.getDescription());
			
			//System.out.println(document);
						
			for(String term : topics.getTerms()) {
				double tfidfScore = tfidfCalc.tfidf(document, documents, term);
						
				
				if(tfidfScore > 0) {
					System.out.print(item.getTitle() + " scored: " + tfidfScore);
					System.err.println(" Beacuse your intrested in " + term);
					item.setScore(tfidfScore);
					item.appendDescription(" | Generated Score: " + tfidfScore);
					recFeed.addToFeed(item);
				}	
			}			
		}
	}

	
	
	public RecommendationEngine(ObservableList<Feed> RSSFeedList) {
		this.StandardFeedList = RSSFeedList;
		initLikedFeeds();
		initDocumentsList();
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
			
			setTFIDFScoresPerFeed(feed);
			
			for(FeedItem item : feed.getMessages()) {
				
//				float itemValue = 1f;
//				Date date = dateFormat.parse(item.getPubDate());
//				
//				//If date within number of hours of date range
//				if(currentDate.getTime() - date.getTime() <= dateRange * hour) {
//					//System.out.println("Date within 24: " + item.getPubDate());
//					itemValue += generateScoreForDateRange(currentDate.getTime() - date.getTime());
//					itemValue *= dateWeighting;
//					//System.out.println("Item value after date calc: " + itemValue);
//				}
//				
//				if(containsName(LikedFeedItemList, item.getGuid())) {
//					//System.out.println("Contained: " + item);
//					itemValue += 100f * likedFeedWeighting;
//					//System.out.println("Item value after feed calc: " + itemValue);
//				}
//					
//				
//				itemValue /= totalWeights;
//				System.out.println("Final Item value: " + itemValue);
//				System.out.println();
//				
//				if(itemValue >= thresholdValue)
//					recFeed.addToFeed(item);
				
			}
			
		}
	
		//System.out.println("Total weigths: " + totalWeights);
		//System.out.println("Threshold value: " + thresholdValue);
		//TODO: Sort items by score value before adding them to recommended feed
		
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
