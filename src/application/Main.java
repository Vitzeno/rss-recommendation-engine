package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			
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
