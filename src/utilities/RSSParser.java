package utilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

import feed.Feed;
import feed.FeedItem;

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
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method parses the RSS from the provided URL, it uses the Streaming API for XML (StAX) API to parse the RSS
     * feed which is in XML format
     * @return a feed object which contains a list of feed items or null
     */
    public Feed readFeed() {
        Feed feed = null;
        try {
            boolean isFeedHeader = true;
            String description = "";
            String title = "";
            String link = "";
            String language = "";
            String copyright = "";
            String author = "";
            String pubdate = "";
            String guid = "";

            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = read();
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            
            
            while(eventReader.hasNext()) {
            	XMLEvent event = eventReader.nextEvent();
      
            	switch(event.getEventType()) {
            		//Read opening tag of XML
            		case XMLStreamConstants.START_ELEMENT:
            			String name = event.asStartElement().getName().getLocalPart();
            			//Deals with the feed header
            			if(name.equalsIgnoreCase(ITEM) || name.equalsIgnoreCase(ENTRY)) {
            				if (isFeedHeader) {
                                isFeedHeader = false;
                                feed = new Feed(title, link, description, language, copyright, pubdate);
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
                            FeedItem message = new FeedItem();
                            message.setAuthor(author);
                            message.setDescription(description);
                            message.setGuid(guid);
                            message.setLink(link);
                            message.setTitle(title);
                            message.setPubDate(pubdate);
                            feed.getMessages().add(message);
                            //event = eventReader.nextEvent();
                            //continue;
                        }
            			break;
            	}
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
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
        try {
            return url.openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}