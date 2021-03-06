package textClassification;

import java.util.HashSet;
import java.util.Set;


/**
 * This singleton class servers as a way of keeping track of topics users enjoy.
 * It makes uses of a set which is an unordered collection of items without duplicates.
 * @author Mohamed
 *
 */
public class UserTopics {
	
	private static UserTopics single_instance = null;
	
	Set<String> userTopics;
	
	/**
	 * private constructor ensures singleton nature of class
	 */
	private UserTopics() {
		userTopics = new HashSet<String>();
	}
	
	/**
	 * This method servers to provide an instance of the class
	 * and ensure that only one instance exists
	 * @return
	 */
	public static UserTopics getInstance() {
		if(single_instance == null)
			single_instance = new UserTopics();
		
		return single_instance;
	}
	
	/**
	 * This method simply adds terms to the user topics
	 * list. Since it is a set there is no need to monitor for
	 * duplicates
	 * @param term
	 */
	public void addTerm(String term) {
		userTopics.add(term);
	}
	
	/**
	 * This method adds an array of terms to the set,
	 * it simply makes uses of the addTerm method
	 * @param terms
	 */
	public void addTerms(String[] terms) {
		for(String term : terms)
			addTerm(term);
	}
	
	/**
	 * This method simply clears all terms in the set
	 */
	public void clearTerms() {
		userTopics.clear();
	}
	
	/**
	 * This method returns the set of user topics
	 * @return
	 */
	public Set<String> getTerms() {
		return userTopics;
	}
	
	/**
	 * This method simply returns the size of the set 
	 * @return
	 */
	public int getNumOfTerms() {
		return userTopics.size();
	}
	
	/**
     * This method determines what to return when the toString method is invoked
     */
	@Override
    public String toString() {
        return userTopics.toString();
    }
}
