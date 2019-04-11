package utilities;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HTMLParser {

	/**
	 * This method returns a string representing a simplified version of the article provided
	 * via URL
	 * @param url of the article to parse 
	 * @return a string of article text, filtering out irrelevant material
	 */
	public String parseHTML(String url) {
		
		StringBuilder result = new StringBuilder();
		
		try {
			Document doc = Jsoup.connect(url).get();
			result.append(doc.title() + "\n");
				
			doc.body().getAllElements().stream()
            .filter(element -> !element.className().toLowerCase().matches(".*(menu|header|footer|logo|nav|search|link|button|btn|ad).*"))
            .filter(element -> element.tagName().equals("p") || element.tagName().equals("h") || element.tagName().equals("h1") || element.tagName().equals("h2") || element.tagName().equals("h3"))
            .forEach(element -> {
                if (element.hasText()) {
                	result.append(element.text() + "\n");
                }
        });
			
			
		} catch (IOException e) {
			System.err.println("JSOUP IO expection " + e.toString());
			e.printStackTrace();
		}	
		
		return result.toString();
	}

}
