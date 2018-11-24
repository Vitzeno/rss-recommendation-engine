package feed;

public class FeedItem {
	
	String title;
    String description;
    String link;
    String author;
    String guid;

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

	@Override
    public String toString() {
        return "FeedMessage [title=" + title + ", description=" + description + ", link=" + link + ", author=" + author + ", guid=" + guid + "]";
    }
}
