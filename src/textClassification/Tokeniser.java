package textClassification;

import java.util.ArrayList;
import java.util.List;

/**
 * This class serves to transform pieces of text into tokens which can then be 
 * used by other classes, most notably for term  frequency analysis
 * @author Mohamed
 *
 */
public class Tokeniser {
	
	private int minTokenSize;
	
	/**
	 * Constructor which takes minimum size for tokens
	 * useful for filtering out small terms such if needed
	 * @param minTokenSize
	 */
	public Tokeniser(int minTokenSize) {
		this.minTokenSize = minTokenSize;
	}
	
	/**
	 * Minimum size for tokens initialised to 0
	 * if no argument is passed to constructor
	 */
	public Tokeniser() {
		this(0);
	}
	
	/**
	 * This method returns a list of tokens given a 
	 * document in string form
	 * @param doc
	 * @return
	 */
	public List<String> getTokens(String doc) {
		//create an array of string by splitting by space
		String[] rawTokens = doc.trim().split("\\s+");
		
		List<String> tokens = new ArrayList<String>();
		
		for(String currentToken : rawTokens) {
			String cleanToken = currentToken.trim().toLowerCase().replaceAll("[^A-Za-z]+", "");
			
			if(cleanToken.length() > minTokenSize)
				tokens.add(cleanToken);
		}
		
		return tokens;
	}
}
