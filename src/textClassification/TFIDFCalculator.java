package textClassification;

import java.util.Set;

/**
 * This class performs term frequency and inverse domain frequency calculations.
 * It will be essential in generating recommendations.
 * @author Mohamed
 *
 */
public class TFIDFCalculator {
	
	private boolean performIDF;
	
	/**
	 * The term frequency (tf) is the number of times a terms appears in a 
	 * document relative to its size.
	 * 
	 * tf = (Number of times term t appears document) / (Number of terms in the document)
	 * @return
	 */
	public double tf(Set<String> document, String term) {
		double termCount = 0;
		double result = 0;
		performIDF = false;
		
		for(String word : document) {
			if(term.equalsIgnoreCase(word))
				termCount++;
		}
		
		//normalise result
		result = termCount / document.size();
		
		if(result > 0)
			performIDF = true;
		
		return result;
	}
	
	/**
	 * The inverse domain frequency (idf) measure how important a term is. The term frequency
	 * considers all terms equally, however common terms such as "the" are not important.
	 * Therefore common terms need to be weighted less than rarer ones which may be more
	 * important.
	 * 
	 * idf = log((Number of documents) / (Number of documents with term t))
	 * @return
	 */
	private double idf(Set<Set<String>> documents, String term) {
		double termCount = 0;
		
		if(!performIDF)
			return 0;
	
		for(Set<String> document : documents) {
			for(String word : document) {
				if(term.equalsIgnoreCase(word)) {
					termCount++;
					break;
				}
			}
		}
		
		return Math.log(documents.size() / termCount);	
	}
	
	/**
	 * This is composed of both the term frequency (tf) and inverse document  frequency (idf).
	 * term frequency inverse document  frequency (tfidf) is large when term appears many times
	 * in small number of documents, but small when term appears in all documents.
	 * 
	 * tfidf = tf * idf
	 * @return
	 */
	public double tfidf(Set<String> document, Set<Set<String>> documents, String term) {
		return tf(document, term) * idf(documents, term);	
	}
}
