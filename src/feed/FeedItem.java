package feed;

/**
 * This class represents each item in the feed with a set of attributes, mostly consists of self explanatory setters and getters
 * @author Mohamed
 *
 */
public class FeedItem {
	
	String title;
    String description;
    String link;
    String author;
    String guid;
    String pubDate;
    double score = 0.0;

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
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
}
