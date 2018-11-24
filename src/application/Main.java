package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setTitle("RSS Reader");
			//primaryStage.setResizable(false);
			primaryStage.setMinWidth(1024);
			primaryStage.setMinHeight(576);
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,1280, 720);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			//Don't forget this boiler plate code again, always a pain to remember 
			Parent content = FXMLLoader.load(getClass().getClassLoader().getResource("ReaderView.fxml"));
			root.setCenter(content);
			
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
