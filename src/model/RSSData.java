package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import database.DatabaseHandler;
import feed.Feed;
import feed.FeedItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import textClassification.TFIDFCalculator;
import textClassification.Tokeniser;
import textClassification.UserTopics;
import utilities.RSSParser;
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
	
	private ToolBox toolBox = new ToolBox();
	
	//All feeds
	private ObservableList<Feed> Feeds = FXCollections.observableArrayList();
	private ArrayList<String> feedsURL = new ArrayList<String>();
	
	
	//private List<List<String>> documents = new ArrayList<List<String>>();
	private Set<Set<String>> documents = new HashSet<Set<String>>(); 
	
	//All tokens in all feeds
	private Set<String> tokens = new HashSet<String>();
	private List<FeedItem> feedItems = new ArrayList<FeedItem>();
	
	private boolean feedsParsed = false;
	private boolean feedsTokenised = false;
	private boolean feedTokenMatrixInitialised = false;
	private boolean similarityMatrixInitialised = false;
	
	private int numOfSimilarRecommendations = 5;
	
	
	
	private FeedsMatrix feedsMatrix;
	
	
	/**
	 * private constructor ensures singleton nature of class
	 */
	private RSSData() {

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
	 * This method uses the RSSParser class to parse a list of RSS feeds from the provided URL
	 * containing a list of RSS url's
	 * @param url
	 */
	public ObservableList<Feed> parseRSSFeeds() {
		DatabaseHandler DBHandler = new DatabaseHandler();
		feedsURL = DBHandler.selectAllFromFeedsTable();
		
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
	 * Creates a set of all tokens from the set of tokens in documents set
	 */
	private void tokeniseAllTerms() {
		tokens = new HashSet<String>(documents.stream().flatMap(x -> x.stream()).collect(Collectors.toList()));
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
	
	public Set<Set<String>> getAllDocumentTokens() {
		return documents;
	}
	
	public List<FeedItem> getFeedItems() {
		feedItems.clear();
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

	public Set<String> getTokens() {
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
			feedTokenMatrixInitialised = true;
		}
		else {
			System.err.println("ERROR: Cannot initialise feeds matrix until all feeds have been parsed and tokenised.");
		}
	}
	
	public Feed setTFIDFScores() {
		Feed recFeed = new Feed();
		
		recFeed.addToFeed(feedsMatrix.setTFIDFScoresPerFeed());
		
		return recFeed;
	}
	
	public Thread getThread() {
		return feedsMatrix.t;
	}
	
	
	private class FeedsMatrix implements Runnable{
		
		public RealMatrix feedTokenMatrix;
		public RealMatrix similarityMatrix;
		
		public Thread t;
		private String threadName;
		
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
			System.out.println("Initialising matrix...");
			TFIDFCalculator tfidfCalc = new TFIDFCalculator();
			
			double[][] array = new double[rowSize][columnSize];
			int n = 0;
			
			for(int i = 0;i < rowSize;i++) {
				FeedItem currentFeedItem = feedItems.get(i);
				Iterator<String> itr = tokens.iterator();	//need to reset iterator so must be within outer loop
				for(int j = 0;j < columnSize;j++) {
					String currentToken = "err" + n++;
					
					if(itr.hasNext()) {
						currentToken = itr.next();
					}
					//System.out.println(currentToken);

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
			System.out.println("Creating similarity matrix...");
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
					
					//System.out.println("Feed A: " + feedA.getTitle() + " Feed B: " + feedB.getTitle());
					//System.out.println("Similarity score: " + similarity);
					
					array[i][j] = similarity;
				}
			}
			
			similarityMatrix = MatrixUtils.createRealMatrix(array);
			similarityMatrixInitialised = true;
		}
		
		public void start() {
			System.out.println("Starting thread: " + threadName);
			if (t == null) {
				t = new Thread (this, threadName);
				t.start ();
				/*
				try {
					t.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				*/
			}
		}
		
		@Override
		public void run() {
			System.out.println("Performing SVD calulations on matrix...");
			SingularValueDecomposition SVD = new SingularValueDecomposition(feedTokenMatrix);
			
			//RealMatrix U = SVD.getU();
			RealMatrix S = SVD.getS();
			RealMatrix V = SVD.getV();
			
			int singularValuesToDrop = S.getColumnDimension() - numOfSingularValuesToRetain(S);
			//System.out.println("Number of values to drop " + singularValuesToDrop);
			
			//Multiple by v to map queries into concept space
			RealMatrix Vp = V.getSubMatrix(0, feedTokenMatrix.getColumnDimension() - 1, 0, singularValuesToDrop - 1);

			AtomicInteger index = new AtomicInteger();
			feedItems.parallelStream().forEachOrdered(item -> {
				RealMatrix result = feedTokenMatrix.getRowMatrix(index.getAndIncrement()).multiply(Vp);
				item.setReducedMatrixValue(result);
			});
			
			
			
			
			setUpSimilarityMatrix(calculation.COSINE);		//TODO: consider parallelising this 
			//getSimilarItems(11, 5);
			


			
			System.out.println("Finishing thread " + t.getName());
		}
		
		/**
		 * 
		 * @param feedItemIndex index of item to find similarities for
		 * @param numOfItems number of similarities to get
		 * @return list of feeds
		 */
		public List<FeedItem> getSimilarItems(int feedItemIndex, int numOfItems) {
			List<FeedItem> similarItems = new ArrayList<FeedItem>();
			
			List<Double> originalListOfScores = Arrays.stream(similarityMatrix.getRow(feedItemIndex)).boxed().collect(Collectors.toList());
			List<Double> listOfScores = Arrays.stream(similarityMatrix.getRow(feedItemIndex)).boxed().collect(Collectors.toList());
			
			if(this.method == calculation.EUCLIDEAN) {
				//listOfScores = listOfScores.parallelStream().sorted().limit(numOfItems).collect(Collectors.toList());
				listOfScores = listOfScores.stream().sorted().limit(numOfItems).collect(Collectors.toList());	
			}
			if(this.method == calculation.COSINE) {
				//listOfScores = listOfScores.parallelStream().sorted(Comparator.reverseOrder()).limit(numOfItems).collect(Collectors.toList());
				listOfScores = listOfScores.stream().sorted(Comparator.reverseOrder()).limit(numOfItems).collect(Collectors.toList());
			}
			
			
			for(int i = 0;i < listOfScores.size();i++) {
				int index = toolBox.getIndex(originalListOfScores, listOfScores.get(i));
				
				//System.out.println("score " + listOfScores.get(i) + " Index " + index + " item title " + feedItems.get(i));
				//System.out.println(originalListOfScores.get(index) + " " + index);
				//feedItems.get(index).appendDescription(" | Similar to: " + feedItems.get(feedItemIndex).getTitle());
				similarItems.add(feedItems.get(index));
			}
			
			System.out.println();
			System.out.println("Item provided " + feedItems.get(feedItemIndex).getTitle() + " | score: " + feedItems.get(feedItemIndex).getScore());
			System.out.println();
			for(int i = 0;i < similarItems.size();i++) {
				System.out.println(similarItems.get(i).getTitle());
				//similarItems.get(i).setExraDecription("Recommended based on topics you enjoy");
			}
			
			//printTokens();
			
			return similarItems;
		}
		
		/**
		 * The sum of squares of the retained singular values should be at least 90%
		 * of the sum of square of all the singular values
		 * @param S
		 * @return
		 */
		public int numOfSingularValuesToRetain(RealMatrix S) {
			System.out.println("Calculating optimal number of singular values to drop...");
			int singularValuesToRetain = rowSize / 2;
			double percentage = 0;
			double originalValues = toolBox.getSumOfSquares(S);
				
			while(percentage < 90) {
				RealMatrix Sp = S.getSubMatrix(0, singularValuesToRetain, 0, S.getColumnDimension() - 1);
				double newValues = toolBox.getSumOfSquares(Sp);
				percentage = (newValues / originalValues) * 100;
				singularValuesToRetain++;
			}
			
			return singularValuesToRetain;
		}

		
		/**
		 * This method utilises the tfidf class to
		 * generate a numerical score for each feed based on user interests
		 * as defined in the userTopics class.
		 * @param feed
		 */
		public Feed setTFIDFScoresPerFeed() {
			TFIDFCalculator tfidfCalc = new TFIDFCalculator();
			UserTopics topics = UserTopics.getInstance();
			Feed recFeed = new Feed();
				
			int size = feedItems.size();
			for(int i = 0;i < size;i++) {
				for(String term : topics.getTerms()) {
					double tfidfScore = tfidfCalc.tfidf(feedItems.get(i).getTokens(), documents, term);					
					
					if(tfidfScore > 0) {
						feedItems.get(i).setScore(tfidfScore);
						feedItems.get(i).setExraDecription("Recommended Score: " + (int) ((feedItems.get(i).getScore() + tfidfScore) * 100));
						
						recFeed.addToFeed(getSimilarItems(i, numOfSimilarRecommendations));
						recFeed.addToFeed(feedItems.get(i));
					}	
				}
			}
			
			return recFeed;
		}
		
		
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
