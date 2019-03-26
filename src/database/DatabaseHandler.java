package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseHandler {
	private String DB_LOC = "database/";
	private String FILE_TYPE = ".db";
	private String DB_NAME = "RSS_DB";
	private String url = "jdbc:sqlite:" + this.DB_LOC + this.DB_NAME + this.FILE_TYPE;
	
	/**
	 * 
	 * @param DB_NAME
	 */
	public DatabaseHandler() {
	}
	
	
	/**
	 * This method create the database using predefined member variables as parameters
	 */
	public void createDatabase() {
		try (Connection conn = DriverManager.getConnection(this.url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created, if not already existing.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
	}
	
	/**
	 * This method creates all the tables in the database, if does not recreate them if they
	 * already exist so this method should be call on start 
	 */
	public void createTables() {
		createFeedsTable();
		createTopicsTable();
		createLikedItemsTable();
	}
	
	/**
	 * This method creates the table of feed urls
	 */
	public void createFeedsTable() {
		String sql = "CREATE TABLE IF NOT EXISTS feeds (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	feed text NOT NULL UNIQUE\n"
                + ");";
		
		try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            	stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
	}
	
	/**
	 * This method creates the table of user topics
	 */
	public void createTopicsTable() {
		String sql = "CREATE TABLE IF NOT EXISTS topics (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	topic text NOT NULL UNIQUE\n"
                + ");";
		
		try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            	stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
	}
	
	/*
	 * This method creates the table of liked feed items (has many more parameters)
	 */
	public void createLikedItemsTable() {
		String sql = "CREATE TABLE IF NOT EXISTS likedItems (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	title text NOT NULL,\n"
                + "	description text,\n"
                + "	link text NOT NULL UNIQUE,\n"
                + "	author text,\n"
                + "	guid text,\n"
                + "	pubDate text\n"
                + ");";
		
		try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            	stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
	}
	
	
	public ArrayList<String> selectAllFromFeedsTable(){
        String sql = "SELECT DISTINCT feed FROM feeds";
        
        ArrayList<String> feeds = new ArrayList<String>();
        
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
                feeds.add(rs.getString("feed"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return feeds;
    }
	
    public void insertIntoFeedsTable(String name) {
        String sql = "INSERT INTO feeds(feed) VALUES(?)";
 
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
	            pstmt.setString(1, name);
	            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public ArrayList<String> selectAllFromTopicsTable() {
    	String sql = "SELECT DISTINCT topic FROM topics";
    	
    	ArrayList<String> topics = new ArrayList<String>();
        
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
                topics.add(rs.getString("topic"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return topics;
    }
    
    public void insertIntoTopicsTable(String name) {
    	String sql = "INSERT INTO topics(topic) VALUES(?)";
    	 
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
	            pstmt.setString(1, name);
	            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void selectAllFromLikedItemsTable() {
    	String sql = "SELECT id, title, description, link, author, guid, pubDate FROM likedItems";
        
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("id") +  "\t" 
                		+ rs.getString("title") + "\t" 
                		+ rs.getString("description") + "\t" 
                		+ rs.getString("link") + "\t" 
                		+ rs.getString("author") + "\t" 
                		+ rs.getString("guid") + "\t" 
                		+ rs.getString("pubDate") + "\t");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void insertIntoLikedItemsTable(String title, String description, String link, String author, String guid, String pubDate) {
    	String sql = "INSERT INTO likedItems(title, description, link, author, guid, pubDate) VALUES(?, ?, ?, ?, ?, ?)";
   	 
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
	            pstmt.setString(1, title);
	            pstmt.setString(2, description);
	            pstmt.setString(3, link);
	            pstmt.setString(4, author);
	            pstmt.setString(5, guid);
	            pstmt.setString(6, pubDate);
	            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void setUpDefaultValues() {
    	insertDefaultFeeds();
    	insertDefaultTopics();
    }
    
    private void insertDefaultTopics() {
    	insertIntoTopicsTable("weather");
    	insertIntoTopicsTable("brexit");
    	insertIntoTopicsTable("apple");
    	insertIntoTopicsTable("facebook");
    	insertIntoTopicsTable("samsung");
    	insertIntoTopicsTable("traffic");
    	insertIntoTopicsTable("car");
    	insertIntoTopicsTable("sale");
    	insertIntoTopicsTable("EU");
    	insertIntoTopicsTable("sports");
    }
    
    private void insertDefaultFeeds() {
    	insertIntoFeedsTable("http://feeds.bbci.co.uk/news/rss.xml");
    	insertIntoFeedsTable("https://www.wired.com/feed/rss");
    	insertIntoFeedsTable("http://www.espn.com/espn/rss/news");
    	insertIntoFeedsTable("https://xkcd.com/rss.xml");
    	insertIntoFeedsTable("https://gizmodo.com/rss");
    	insertIntoFeedsTable("http://feeds.macrumors.com/MacRumors-All");
    	insertIntoFeedsTable("https://store.steampowered.com/feeds/news.xml");
    	insertIntoFeedsTable("http://m.highwaysengland.co.uk/feeds/rss/UnplannedEvents.xml");
    	insertIntoFeedsTable("https://kotaku.com/rss");
    	insertIntoFeedsTable("http://feeds.ign.com/ign/video-guides");
    	insertIntoFeedsTable("https://www.nottinghampost.com/news/?service=rss");
    }
    
    private Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(this.url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
