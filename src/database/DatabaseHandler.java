package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {
	private String DB_LOC = "database/";
	private String FILE_TYPE = ".db";
	private String DB_NAME;
	private String url;
	
	public DatabaseHandler(String DB_NAME) {
		this.DB_NAME = DB_NAME;
		this.url = "jdbc:sqlite:" + this.DB_LOC + this.DB_NAME + this.FILE_TYPE;
	}
	
	public void createNewDatabase() {
		try (Connection conn = DriverManager.getConnection(this.url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
	}
	
	public void createNewTable() {
		
		String sql = "CREATE TABLE IF NOT EXISTS likedFeeds (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	feedItem text NOT NULL\n"
                + ");";
		
		try (Connection conn = DriverManager.getConnection(url);
				
                Statement stmt = conn.createStatement()) {
            	stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
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
	
	public void selectAll(){
        String sql = "SELECT id, feedItem FROM likedFeeds";
        
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("id") +  "\t" + rs.getString("feedItem") + "\t");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
	
    public void insert(String name) {
        String sql = "INSERT INTO likedFeeds(feedItem) VALUES(?)";
 
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
