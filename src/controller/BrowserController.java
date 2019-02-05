package controller;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import utilities.HTMLParser;

/**
 * This class servers as a controller for the in built browser view
 * @author Mohamed
 *
 */
public class BrowserController {
	
	private String URL;

    @FXML
    private Button btnBack;
    @FXML
    private TextArea txtArticle;
    @FXML
    private Button btnOpenInBrowse;
    
    @FXML
    void goBack(MouseEvent event) throws IOException {
    	Parent browserViewParent = FXMLLoader.load(getClass().getClassLoader().getResource("ReaderView.fxml"));
    	Scene browserViewScene = new Scene(browserViewParent);
    	Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
    	
    	window.setScene(browserViewScene);
        window.show();
    }
    
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
    		
    		//webView.getEngine().load(URL);
    	}	
    	else
    		System.out.println("Malformed URL passed: " + URL);
    }
    
    private void displayText(String text) {
    	txtArticle.setStyle("-fx-highlight-text-fill: firebrick; -fx-font-size: 20px;");
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
            new URL(url).toURI(); 
            return true; 
        } 
        catch (Exception e) { 
            return false; 
        } 
    }

}
