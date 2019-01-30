package utilities;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import feed.FeedItem;

public class HTMLParser {

	
	public void parseHTML(FeedItem item) {
		
		String url = item.getLink();
		
		try {
			Document doc = Jsoup.connect(url).get();
			System.out.println(doc.title());
			
			Elements ps = doc.getElementsByTag("p");
			Elements h1s = doc.getElementsByTag("h1");
			Elements h2s = doc.getElementsByTag("h2");
			Elements h3s = doc.getElementsByTag("h3");
			Elements h4s = doc.getElementsByTag("h4");
			Elements h5s = doc.getElementsByTag("h5");
			
			for(Element p : ps) 
				System.out.println(p);
			for(Element h1 : h1s) 
				System.out.println(h1);
			for(Element h2 : h2s) 
				System.out.println(h2);
			for(Element h3 : h3s) 
				System.out.println(h3);
			for(Element h4 : h4s) 
				System.out.println(h4);
			for(Element h5 : h5s) 
				System.out.println(h5);
			
		} catch (IOException e) {
			System.err.println("JSOUP IO expection " + e.toString());
			e.printStackTrace();
		}	
	}

}
