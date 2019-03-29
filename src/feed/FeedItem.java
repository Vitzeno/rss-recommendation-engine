package feed;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math3.linear.RealMatrix;

/**
 * This class represents each item in the feed with a set of attributes, mostly consists of self explanatory setters and getters
 * @author Mohamed
 *
 */
public class FeedItem implements Comparable<FeedItem> {
	
	//Intrinsic properties of feed items
	String title = "";
    String description = "";
    String extraDecription = "";
	String link = "";
    String author = "";
    String guid = "";
    String pubDate = "";
    //Properties of feed item to be calculated on each run
    double tfidfScore = 0;
    RealMatrix reducedMatrixValue;
    
    public String getExraDecription() {
		return extraDecription;
	}

	public void setExraDecription(String extraDecription) {
		this.extraDecription = extraDecription;
	}

	public RealMatrix getReducedMatrixValue() {
		return reducedMatrixValue;
	}

	public void setReducedMatrixValue(RealMatrix reducedMatrixValue) {
		this.reducedMatrixValue = reducedMatrixValue;
	}

	Set<String> tokens = new HashSet<String>();
    

	public Set<String> getTokens() {
		return tokens;
	}
	
	public void addToken(String token) {
		tokens.add(token);
	}

	public void setTokens(Set<String> tokens) {
		this.tokens = tokens;
	}

	public double getScore() {
		return tfidfScore;
	}
	
	/**
	 * Each feed item can have more than one tfidf score generated 
	 * due to multiple user topics available therefore scores 
	 * should be appended
	 * @param score
	 */
	public void setScore(double score) {
		this.tfidfScore += score;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getLink() {
		return link;
	}

	public String getAuthor() {
		return author;
	}

	public String getGuid() {
		return guid;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void appendDescription(String description) {
		setDescription(this.getDescription() + description);
	}

	public void setLink(String link) {
		this.link = link;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}
	
    public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	
	/**
     * This method determines what to return when the toString method is invoked on the feed item object
     */
	@Override
    public String toString() {
        //return "FeedMessage [title=" + title + ", description=" + description + ", link=" + link + ", author=" + author + ", guid=" + guid + "]";
		return title + " | " + description;
    }
	
	/**
	 * This method overrides the equal method to be used when
	 * comparing FeedItem objects. This is essential in ensuring 
	 * the set data structure can compare objects meaningfully
	 */
    @Override
    public boolean equals(Object obj) {
    	if (this == obj)
    		return true;
    	if (obj == null)
    		return false;
    	FeedItem item = (FeedItem) obj;
    	if (this.getGuid() != item.getGuid())
    		return false;
  
    	return true;
    }
    
    /**
     * Tree-set comparison relies on this method, it it returns
     * 0 the two items are the same. This relies on the equal method which 
     * was previously overridden.
     * 
     * Criteria used by tree set: 
     * 
     * Positive:  Current instance is greater than Other.
     * Zero:  Two instances are equal.
     * Negative: Current instance is smaller than Other.
     * 
     * where: 
     * 		current instance = item
     * 		other = this
     * 
     */
	@Override
	public int compareTo(FeedItem item) {
		if(this.equals(item))
			return 0;
		return this.getScore() < item.getScore() ? 1 : -1;
	}
}
