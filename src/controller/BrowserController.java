package controller;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * This class servers as a controller for the in built browser view
 * @author Mohamed
 *
 */
public class BrowserController {

    @FXML
    private Button btnBack;

    @FXML
    private WebView webView;
    

    //private String URL;
    
    @FXML
    void goBack(MouseEvent event) throws IOException {
    	Parent browserViewParent = FXMLLoader.load(getClass().getClassLoader().getResource("ReaderView.fxml"));
    	Scene browserViewScene = new Scene(browserViewParent);
    	Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
    	
    	window.setScene(browserViewScene);
        window.show();
    }
    
    /**
     * This method initialises the scene
     */
    @FXML
    void initialize() {
    	
    }
    
    public void load(String URL) {
    	if(validateURI(URL)) {
    		System.out.println("Link to open: " + URL);
    		webView.getEngine().load(URL);
    	}	
    	else
    		System.out.println("Malformed URL passed: " + URL);
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
    
   
    /*
    public void setURL(String URL) {
    	if(validateURI(URL))
    		this.URL = URL;
    }
    
    public String getURL() {
    	return this.URL;
    }
    */
}
