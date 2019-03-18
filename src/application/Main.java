package application;
	
import database.DatabaseHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import model.RSSData;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	
	private String RSSFileName = "standardFeeds";
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			/**
			 * First initialisation of RSSData singleton class
			 * contains data to be used by most classes in this 
			 * project and must "live" for the project lifetime
			 */
			RSSData.getInstance(RSSFileName);
			
			
			DatabaseHandler DBHandler = new DatabaseHandler("test");
			DBHandler.createNewDatabase();
			DBHandler.createNewTable();
			//DBHandler.insert("Lol");
			//DBHandler.insert("Lolwarhw");
			DBHandler.selectAll();
			
			primaryStage.setTitle("RSS Reader");
			primaryStage.setMinWidth(640);
			primaryStage.setMinHeight(480);
			
			Parent content = FXMLLoader.load(getClass().getClassLoader().getResource("ReaderView.fxml"));
		    
			Scene scene = new Scene(content);
		    scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		    
		    primaryStage.setScene(scene);
		    primaryStage.show();
		    
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
