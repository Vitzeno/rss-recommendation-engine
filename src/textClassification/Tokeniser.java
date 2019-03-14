package textClassification;

import java.util.HashSet;
import java.util.Set;

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
	 * This method returns a set of tokens given a 
	 * document in string form
	 * @param doc
	 * @return
	 */
	public Set<String> getTokens(String doc) {
		//create an array of string by splitting by space
		String[] rawTokens = doc.trim().split("\\s+");
		
		Set<String> tokens = new HashSet<String>();
		
		for(String currentToken : rawTokens) {
			String cleanedToken = currentToken.trim().toLowerCase().replaceAll("[^A-Za-z0-9]+", "");
			
			if(cleanedToken.length() > minTokenSize)
				tokens.add(cleanedToken);
		}
		
		return tokens;
	}
}
