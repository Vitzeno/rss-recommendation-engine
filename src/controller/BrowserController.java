package controller;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import settings.Settings;
import utilities.HTMLParser;

/**
 * This class servers as a controller for the in built browser view
 * @author Mohamed
 *
 */
public class BrowserController {
	
	private String URL;

    @FXML
    private TextArea txtArticle;
    @FXML
    private Button btnOpenInBrowse;

    
    @FXML
    void openInBrowser(MouseEvent event) {
    	System.out.println("Clicked on " + URL) ;
        Desktop desktop = Desktop.getDesktop();
        try {
			desktop.browse(new URI(URL));
		} catch (IOException e) {
			System.err.println("IO Error");
			e.printStackTrace();
		} catch (URISyntaxException e) {
			System.err.println("Invalid URI syntax");
			e.printStackTrace();
		}
    }
    
    /**
     * This method initialises the scene
     */
    @FXML
    void initialize() {
    	
    }
    
    public void load(String URL) {
    	this.URL = URL;
    	if(validateURI(URL)) {
    		HTMLParser htmlParser = new HTMLParser();
    		System.out.println("Link to open: " + URL);
    		
    		displayText(htmlParser.parseHTML(URL));
    	}	
    	else
    		System.out.println("Cannot connect: " + URL);
    }
    
    private void displayText(String text) {
    	txtArticle.setStyle("-fx-highlight-text-fill: firebrick; -fx-font-family: " + Settings.getSettings().getReaderFont() +";" + " -fx-font-size: " + Settings.getSettings().getReaderFontSize() + "px;");
    	txtArticle.setWrapText(true);
    	txtArticle.setText(text);
    }
    
    /**
     * This method server to verify if the URI provided is syntactically valid or not
     * @param uri
     * @return
     */
    private boolean validateURI(String url) {
    	try {
    	    URL urlTest = new URL(url);
    	    URLConnection conn = urlTest.openConnection();
    	    conn.connect();
    	} catch (IOException e) {
    		Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Error");
    		alert.setHeaderText("Connection Error");
    		alert.setContentText("Connection to the provided URL cannot be establish, this may be cause by lack of internet connection or invalid URL");
    		alert.showAndWait().ifPresent(rs -> {
    		    if (rs == ButtonType.OK) {
    		        System.out.println("Pressed OK.");
    		    }
    		});
			e.printStackTrace();
			return false;
		}
		return true; 
    }

}
