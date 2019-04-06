package application;
	
import database.DatabaseHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import model.RSSData;
import settings.Settings;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			/**
			 * First initialisation of RSSData singleton class
			 * contains data to be used by most classes in this 
			 * project and must "live" for the project lifetime
			 */
			RSSData.getInstance();
			Settings settings = Settings.getSettings();
			String stylesheet = null;
			
			DatabaseHandler DBHandler = new DatabaseHandler();
			DBHandler.createDatabase();
			DBHandler.createTables();
			
			//DBHandler.setUpDefaultValues();
			
			DBHandler.selectAllFromFeedsTable();
			DBHandler.selectAllFromTopicsTable();
			DBHandler.selectAllFromLikedItemsTable();
			
			System.setProperty("http.agent", "Chrome");

					
			primaryStage.setTitle("RSS Reader");
			primaryStage.setMinWidth(640);
			primaryStage.setMinHeight(480);
			
			Parent content = FXMLLoader.load(getClass().getClassLoader().getResource("ReaderView.fxml"));
		    
			Scene scene = new Scene(content);
			
			switch(settings.getReaderTheme()) {
			case "Verdant Dark":
				stylesheet = "dark.css";
				break;
			case "Lapis Light":
				stylesheet = "light.css";
				break;
			case "Modena":
				stylesheet = "application.css";
				break;
			default:
				stylesheet = "light.css";
				break;
			}
			
		    scene.getStylesheets().add(getClass().getResource(stylesheet).toExternalForm());
		    
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
