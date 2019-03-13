package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import feed.Feed;
import feed.FeedItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import textClassification.TFIDFCalculator;
import textClassification.Tokeniser;
import utilities.RSSParser;
import utilities.Reader;
import utilities.ToolBox;

/**
 * This class serves as the model, it handles all the data and parsing
 * @author Mohamed
 *
 */
public class RSSData {
	
	enum calculation {
		  COSINE,
		  EUCLIDEAN
		}
	
	private static RSSData single_instance = null;
	
	private Reader reader = new Reader();
	private ToolBox toolBox = new ToolBox();
	
	//All feeds
	private ObservableList<Feed> Feeds = FXCollections.observableArrayList();
	private ArrayList<String> feedsURL = new ArrayList<String>();
	
	private String feedURLs;
	
	private List<List<String>> documents = new ArrayList<List<String>>();
	
	//All tokens in all feeds
	private List<String> tokens = new ArrayList<String>();
	private List<FeedItem> feedItems = new ArrayList<FeedItem>();
	
	private boolean feedsParsed = false;
	private boolean feedsTokenised = false;
	private boolean feedTokenMatrixInitialised = false;
	
	private FeedsMatrix feedsMatrix;
	
	
	/**
	 * private constructor ensures singleton nature of class
	 */
	private RSSData() {

	}
	
	/**
	 * Alternate Constructor to pass feeds url
	 * @param url
	 */
	private RSSData(String url) {
		this.feedURLs = url;
	}
	
	/**
	 * This method servers to provide an instance of the class
	 * and ensure that only one instance exists
	 * @return
	 */
	public static RSSData getInstance() {
		if(single_instance == null)
			single_instance = new RSSData();
		
		return single_instance;
	}
	
	/**
	 * This method servers to provide an instance of the class
	 * and ensure that only one instance exists, also servers
	 * as an alternate constructor for singleton nature
	 * of this class
	 * @return
	 */
	public static RSSData getInstance(String url) {
		if(single_instance == null)
			single_instance = new RSSData(url);
		
		return single_instance;
	}
	
	/**
	 * This method uses the RSSParser class to parse a list of RSS feeds from the provided URL
	 * containing a list of RSS url's
	 * @param url
	 */
	public ObservableList<Feed> parseRSSFeeds() {
		
		feedsURL = reader.readFile(feedURLs);
    	for (String URL : feedsURL) {
    		Feeds.add(parseRSSFeed(URL));
    	}
    	
    	tokeniseDocuments();
    	getFeedItems();
    	
    	feedsTokenised = true;
    	feedsParsed = true;
    	
		return Feeds;
	}
	
	/**
	 * This method uses the RSSParser class to parse an RSS feed from the provided URL
	 * @param url
	 * @return
	 */
	private Feed parseRSSFeed(String url) {
		Feed feed;
		RSSParser parser;
		
		parser = new RSSParser(url);
        feed = parser.readFeed();
        
        return feed;
	}
	
	/**
	 * This method servers to set up the list of document tokens
	 * to be used in tfidf calculations
	 */
	public void tokeniseDocuments() {
		Tokeniser tokeniser = new Tokeniser();
		
		for(Feed feed : Feeds) {
			for(FeedItem item : feed.getMessages()) {
				item.setTokens(tokeniser.getTokens(item.getTitle() + item.getDescription()));
				documents.add(item.getTokens());
			}
		}
		
		tokeniseAllTerms();
	}
	
	/**
	 * Creates a list of all tokens from the list of tokens in documents
	 */
	private void tokeniseAllTerms() {
		tokens  = documents.stream().flatMap(x -> x.stream()).collect(Collectors.toList());
	}
	
	public void printTokens() {
		toolBox.printTokens(tokens);
	}
	
	public void printDocuments() {
		toolBox.printDocuments(documents);
	}
	
	/**
	 * Simple method to print all feeds for debugging purposes
	 */
	public void printFeeds() {
		toolBox.printFeeds(Feeds);
	}
	
	public List<List<String>> getAllDocumentTokens() {
		return documents;
	}
	
	public List<FeedItem> getFeedItems() {
		for(Feed feeds : Feeds) {
			for(FeedItem item : feeds.getMessages()) {
				feedItems.add(item);
			}
		}
		return feedItems;
	}
	
	public List<Feed> getFeeds() {
		return Feeds;
	}

	public List<String> getTokens() {
		return tokens;
	}
	
	/**
	 * This method returns a list of items in a specific feed
	 * @param feed
	 * @return
	 */
	public Set<FeedItem> parseRSSFeedItems(Feed feed) {
		return feed.getMessages();
	}
	
	
	
	
	/**
	 * This method initialises the inner matrix class, this should only
	 * be done when all feeds and feeds items have been parsed and tokenised
	 * otherwise matrix size will be incorrect
	 */
	public void initialiseFeedsMatrix() {
		if(feedsParsed && feedsTokenised) {
			feedsMatrix = new FeedsMatrix();
			feedsMatrix.printMatrixData();
			feedsMatrix.reduceMatrix("Thread 1");
			//feedsMatrix.printMatrix();
			feedTokenMatrixInitialised = true;
		}
		else {
			System.err.println("ERROR: Cannot initialise feeds matrix until all feeds have been parsed and tokenised.");
		}
	}
	
	/**
	 * 
	 */
	public void setUpSimilarityMatrix(calculation method) {
		if(feedTokenMatrixInitialised) {
			feedsMatrix.setUpSimilarityMatrix(method);
		} else {
			System.err.println("ERROR: Cannot initialise similarity matrix until feed token matrix has been set up.");
		}
	}
	
	
	private class FeedsMatrix implements Runnable{
		
		public RealMatrix feedTokenMatrix;
		public RealMatrix similarityMatrix;
		
		private Thread t;
		private String threadName;
		private int similarityCalculationMethod = 0; // (0 = cosine) (1 = euclidean)
		
		private calculation method;
	
		int rowSize;		//feeds
		int columnSize;		//tokens
		
		public FeedsMatrix() {
			rowSize = feedItems.size();
			columnSize = tokens.size();
			setUpMatrix();
		}
		
		/**
		 * This method 
		 */
		public void setUpMatrix() {
			System.out.println("Initialising matrix");
			TFIDFCalculator tfidfCalc = new TFIDFCalculator();
			
			double[][] array = new double[rowSize][columnSize];
			
			for(int i = 0;i < rowSize;i++) {
				FeedItem currentFeedItem = feedItems.get(i);
				for(int j = 0;j < columnSize;j++) {
					String currentToken = tokens.get(j);
					double score = tfidfCalc.tf(currentFeedItem.getTokens(), currentToken);
					//double score = tfidfCalc.tfidf(currentFeedItem.getTokens(), documents, currentToken);
					array[i][j] = score;
				}
			}
			
			feedTokenMatrix = MatrixUtils.createRealMatrix(array);
		}
		
		public void reduceMatrix(String threadName) {
			this.threadName = threadName;
			System.out.println("Creating thread " + threadName);
			this.start();
		}
		
		/**
		 * This method calculates the distance or similarity of feed items in order
		 * to populate a similarity matrix. The method used for calculation can be defined.
		 * 
		 * EUCLIDEAN: This simply gets the distance between items, therefore the smaller the number the more similar the items
		 * COSINE: This calculates the cosine similarity of items, this has a range of (0 - 1) 0 means no similarity at all 1 means identical items
		 * @param method
		 */
		public void setUpSimilarityMatrix(calculation method) {
			this.method = method;
			double[][] array = new double[rowSize][rowSize];
			double similarity = 0;
			
			for(int i = 0;i < rowSize;i++) {
				FeedItem feedA = feedItems.get(i);
				for(int j = 0;j < rowSize;j++) {
					FeedItem feedB = feedItems.get(j);
					
					if(method == calculation.EUCLIDEAN)
						similarity = toolBox.euclideanDistance(feedA.getReducedMatrixValue().getRow(0), feedB.getReducedMatrixValue().getRow(0));
					else if(method == calculation.COSINE)
						similarity = toolBox.cosineSimilarity(feedA.getReducedMatrixValue().getRow(0), feedB.getReducedMatrixValue().getRow(0));
					
					System.out.println("Feed A: " + feedA.getTitle() + " Feed B: " + feedB.getTitle());
					System.out.println("Similarity score: " + similarity);
					
					array[i][j] = similarity;
				}
			}
			
			similarityMatrix = MatrixUtils.createRealMatrix(array);
		}
		
		public void start() {
			System.out.println("Starting thread: " + threadName);
			if (t == null) {
				t = new Thread (this, threadName);
				t.start ();
			}
		}

		@Override
		public void run() {
			System.out.println("Performing SVD calulations on matrix");
			SingularValueDecomposition SVD = new SingularValueDecomposition(feedTokenMatrix);
			
			//RealMatrix U = SVD.getU();
			//RealMatrix S = SVD.getS();
			RealMatrix V = SVD.getV();
			
			RealMatrix Vp = V.getSubMatrix(0, feedTokenMatrix.getColumnDimension() - 1, 0, 1);
			
			int i = 0;
			for(FeedItem item : feedItems) {
				RealMatrix result = feedTokenMatrix.getRowMatrix(i).multiply(Vp);
				item.setReducedMatrixValue(result);
				//toolBox.printMatrix(item.getReducedMatrixValue());
				//System.out.println(item.getTitle());
				i++;
			}
			System.out.println("Finishing thread " + t.getName());
			
			setUpSimilarityMatrix(calculation.EUCLIDEAN);		//TODO: consider parallelising this 
		}
		
		/**
		 * 
		 * @param feedItemIndex index of item to find similarities for
		 * @param numOfItems number of similarities to get
		 * @return list of feeds
		 */
/*		public List<FeedItem> getSimilarItems(int feedItemIndex, int numOfItems) {
			List<FeedItem> feedItems = new ArrayList<FeedItem>();
			
			if(this.method == calculation.EUCLIDEAN) {
				
			}
			if(this.method == calculation.COSINE) {
				double[] listOfScore = similarityMatrix.getRow(feedItemIndex);
				

				
				PriorityQueue<Double> PQ;
				PQ = new PriorityQueue<>(listOfScore.length);
				PQ.addAll(Arrays.asList(listOfScore));
				
			}
			
			return feedItems;
		}*/
		
		
		
		
		
		
		public void printMatrixData() {
			toolBox.printMatrixData(feedTokenMatrix);
		}
		
		public void printMatrix() {
			toolBox.printMatrix(feedTokenMatrix);
		}
		
		public void testSVD() {
			double[][] array = {
					{1, 1, 1, 0, 0},
					{3, 3, 3, 0, 0},
					{4, 4, 4, 0, 0},
					{5, 5, 5, 0, 0},
					{0, 2, 0, 4, 4},
					{0, 0, 0, 5, 5},
					{0, 1, 0, 2, 2}
			};
			
			RealMatrix A = MatrixUtils.createRealMatrix(array);
			SingularValueDecomposition svd = new SingularValueDecomposition(A);
			RealMatrix U = svd.getU();
			RealMatrix S = svd.getS();
			RealMatrix V = svd.getV();
			
			
			RealMatrix Up = U.getSubMatrix(0, A.getRowDimension() - 1, 0, 1);
			RealMatrix Sp = S.getSubMatrix(0, 1, 0, 1);
			RealMatrix Vp = V.getSubMatrix(0, A.getColumnDimension() - 1, 0, 1);
			
			RealMatrix query = A.getRowMatrix(4);
			RealMatrix query2 = A.getRowMatrix(1);

			
			RealMatrix vector = query.multiply(Vp);
			RealMatrix vector2 = query2.multiply(Vp);
			
			System.out.println("A");
			toolBox.printMatrix(A);
			
			System.out.println("Up");
			toolBox.printMatrix(Up);
			
			System.out.println("Sp");
			toolBox.printMatrix(Sp);
			
			System.out.println("Vp");
			toolBox.printMatrix(Vp);
			
			System.out.println("vector");
			toolBox.printMatrix(vector);
			
			System.out.println("vector2");
			toolBox.printMatrix(vector2);

		}
		
	}
}
