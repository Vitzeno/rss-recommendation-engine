package recommendation;

import java.text.ParseException;
import feed.Feed;
import javafx.collections.ObservableList;
import model.RSSData;

/**
 * This class handles all the recommendations produced
 * @author Mohamed
 *
 */
public class RecommendationEngine {
	
	private RSSData RSSDataModel;
	private Feed recFeed;

	
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
		recFeed = new Feed("Recommended Feeds", "", "Auto generated feed of recommended items", "en-gb", "", "", "");
	
		recFeed.addToFeed(RSSDataModel.getRecommendations());
		
		return recFeed;
	}
	
    /*
     * This method only servers to initialise the RSSDataModel object
     */
    public void initModel() {
    	System.out.println("Initialising recEngine data model");
    	RSSDataModel = RSSData.getInstance();
    }
}
