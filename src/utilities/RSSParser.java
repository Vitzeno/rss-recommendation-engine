package utilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import database.DatabaseHandler;
import feed.Feed;
import feed.FeedItem;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

/**
 * This class handles all parsing of RSS
 * @author Mohamed
 *
 */
public class RSSParser {
	
	static final String TITLE = "title";
    static final String DESCRIPTION = "description";
    static final String CHANNEL = "channel";
    static final String LANGUAGE = "language";
    static final String COPYRIGHT = "copyright";
    static final String LINK = "link";
    static final String AUTHOR = "author";
    static final String ITEM = "item";
    static final String ENTRY = "entry";
    static final String PUB_DATE = "pubDate";
    static final String GUID = "guid";

    final URL url;

    /**
     * The constructor take a URL to parse
     * @param feedUrl
     */
    public RSSParser(String feedUrl) {
    	try {
    		this.url = new URL(feedUrl);
    	    URLConnection conn = this.url.openConnection();
    	    conn.setConnectTimeout(3000);
    	    conn.connect();
    	} catch (MalformedURLException e) {
    		Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Error");
    		alert.setHeaderText("Malformed URL " + feedUrl);
    		alert.showAndWait().ifPresent(rs -> {
    		    if (rs == ButtonType.OK) {
    		        System.out.println("Pressed OK.");
    		    }
    		});
    		e.printStackTrace();
    		throw new RuntimeException(e);
    	} catch (IOException e) {
    		Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Error");
    		alert.setHeaderText("Connection Error");
    		alert.setContentText("Connection error may be caused by lack of internet. Please check your connection and try again.");
    		alert.showAndWait().ifPresent(rs -> {
    		    if (rs == ButtonType.OK) {
    		        System.out.println("Pressed OK.");
    		    }
    		});
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    	
    	/*
        try {
            this.url = new URL(feedUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        */
    }

    /**
     * This method parses the RSS from the provided URL, it uses the Streaming API for XML (StAX) API to parse the RSS
     * feed which is in XML format
     * @return a feed object which contains a list of feed items or null
     * @throws XMLStreamException 
     */
    public Feed readFeed() {
        Feed feed = new Feed();
        
        boolean isFeedHeader = true;
        String description = "";
        String title = "";
        String link = "";
        String language = "";
        String copyright = "";
        String author = "";
        String pubdate = "";
        String guid = "";
        String url = this.url.toString();
        
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        InputStream in = read();
        
            
        XMLEventReader eventReader = null;
		try {
			eventReader = inputFactory.createXMLEventReader(in);
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
        
        if(!(eventReader == null) && !(in == null)) {
        	//System.out.println("event reader not null");
        	while(eventReader.hasNext()) {
            	//System.out.println("event reader has next");
            	try {
                	XMLEvent event = eventReader.nextEvent();
          
                	switch(event.getEventType()) {
                		//Read opening tag of XML
                		case XMLStreamConstants.START_ELEMENT:
                			String name = event.asStartElement().getName().getLocalPart();
                			//Deals with the feed header
                			if(name.equalsIgnoreCase(ITEM) || name.equalsIgnoreCase(ENTRY)) {
                				if (isFeedHeader) {
                                    isFeedHeader = false;
                                    feed = new Feed(title, link, description, language, copyright, pubdate, url);
                                }
                                //event = eventReader.nextEvent();
                			}
                			
                			//Deals with each tag name
                			switch (name) {
                            case TITLE:
                                title = getCharacterData(event, eventReader);
                                break;
                            case DESCRIPTION:
                                description = getCharacterData(event, eventReader);
                                break;
                            case LINK:
                                link = getCharacterData(event, eventReader);
                                break;
                            case GUID:
                                guid = getCharacterData(event, eventReader);
                                break;
                            case LANGUAGE:
                                language = getCharacterData(event, eventReader);
                                break;
                            case AUTHOR:
                                author = getCharacterData(event, eventReader);
                                break;
                            case PUB_DATE:
                                pubdate = getCharacterData(event, eventReader);
                                break;
                            case COPYRIGHT:
                                copyright = getCharacterData(event, eventReader);
                                break;
                            }
                			break;
                		
                		//Read closing tag of XML
                		case XMLStreamConstants.END_ELEMENT:
                			if (event.asEndElement().getName().getLocalPart() == (ITEM)) {
                                FeedItem item = new FeedItem();
                                item.setAuthor(author);
                                item.setDescription(description);
                                item.setGuid(guid);
                                item.setLink(link);
                                item.setTitle(title);
                                item.setPubDate(pubdate);
                                feed.getMessages().add(item);
                                //event = eventReader.nextEvent();
                                //continue;
                            }
                			break;
                	}
                } catch (XMLStreamException e ) {
                	e.printStackTrace();
                	
                	DatabaseHandler DBHandler = new DatabaseHandler();
                	String toDelete = this.url.toString();
                	
                	Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                	alert.setTitle("Error");
                	alert.setContentText("Error regarding:   " + toDelete + "\n" 
                				+ "\n"
                				+ "It is causing parsing issues, it may not be a valid RSS feed, do you wish to remove it?");
                	Optional<ButtonType> answer = alert.showAndWait();
                	
                	if(answer.get() == ButtonType.OK) {
                		DBHandler.deleteFromFeedTable(toDelete);
                	}
        			
        			return feed;
                }
            }     	
        }
         
        return feed;
    }
    
    /**
     * This helper method server to read character data from provided 
     * XML events
     * @param event
     * @param eventReader
     * @return the data in String form
     * @throws XMLStreamException
     */
    private String getCharacterData(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {
        String result = "";
        event = eventReader.nextEvent();
        if (event instanceof Characters) {
            result = event.asCharacters().getData();
        }
        return result;
    }
    
    /** 
     * This helper method servers to open a URL stream
     * @return an open URL stream
     */
    private InputStream read() {
    	InputStream stream = null;
        try {
        	URLConnection conn = this.url.openConnection();
        	conn.setConnectTimeout(3000);
    	    conn.connect();
    	    stream = conn.getInputStream();
            return stream;
        } catch (IOException e) {
        	e.printStackTrace();
        	
        	DatabaseHandler DBHandler = new DatabaseHandler();
        	String toDelete = this.url.toString();
        	
        	Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        	alert.setTitle("Error");
        	alert.setContentText("Error regarding:   " + toDelete + "\n" 
        				+ "\n"
        				+ "403 returned, connection is valid but resourse is forbidden.\n"
        				+ "\n"
        				+ "It is causing parsing issues, it may not be a valid RSS feed, do you wish to remove it?");
        	Optional<ButtonType> answer = alert.showAndWait();
        	
        	if(answer.get() == ButtonType.OK) {
        		DBHandler.deleteFromFeedTable(toDelete);
        	}
        	
            
        }
		return stream;
    }

}
