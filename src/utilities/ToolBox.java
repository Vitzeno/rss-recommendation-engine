package utilities;

import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.math3.linear.RealMatrix;
import feed.Feed;
import feed.FeedItem;
import javafx.collections.ObservableList;

/**
 * This class contains various helper methods used in multiple
 * parts of the code base.
 * @author Mohamed
 *
 */
public class ToolBox {

	/**
	 * This method simply prints a matrix to be displayed
	 * for ease of use and visualisation purposes
	 * @param m
	 */
	public void printMatrix(RealMatrix m) {
		for(int i = 0;i < m.getRowDimension();i++) {
			for(int j = 0;j < m.getColumnDimension();j++) {
				System.out.print(m.getEntry(i, j) + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void printMatrixData(RealMatrix m) {
		System.out.println("Matrix row dimension " + m.getRowDimension());
		System.out.println("Matrix col dimension " + m.getColumnDimension());
	}
	
	public void printTokens(List<String> tokens) {
		System.out.println(tokens);
		System.out.println("Feed size " + tokens.size());
	}
	
	public void printDocuments(List<List<String>> documents) {
		System.out.println(documents);
		System.out.println("Feed size " + documents.size());
	}
	
	public void printFeeds(List<Feed> feeds) {
		for(Feed feed : feeds) {
			for(FeedItem items : feed.getMessages())
				System.out.println(items);
		}
	}
	
	/**
	 * Cosine similarity returns a range between -1 and 1 ranging from opposites to 
	 * exactly the same. If the method returns any other value then there has been
	 * an error, in this case method will return a value of 2.
	 * 
	 * In the case of this application all values are positive therefore the range
	 * will only be between 0 and 1
	 * 
	 * Arrays must be of the same length.
	 * @param A
	 * @param B
	 * @return
	 */
	public double cosineSimilarity(double[] A, double[] B) {
		double dotProduct = 0;
		double A_Magnitude = 0;
		double B_Magnitude = 0;
		
		if(A.length != B.length) {
			System.err.println("Array sizes must be the same.");
			return 2;
		}
		
		for(int i = 0;i < A.length;i++) {
			dotProduct += A[i] * B[i];			//A.B
			A_Magnitude += Math.pow(A[i], 2);	//A^2
			B_Magnitude += Math.pow(B[i], 2);	//B^2
		}

		if(A_Magnitude == 0 || B_Magnitude == 0) 
			return 2;
	
		return dotProduct / (Math.sqrt(A_Magnitude) * Math.sqrt(B_Magnitude));
	}
	
	/**
	 * This method calculates the euclidean distance between two points
	 * represented as two arrays. The smaller the number the more similar 
	 * items are.
	 * 
	 * Arrays must be of the same length.
	 * @param A
	 * @param B
	 * @return
	 */
	public double euclideanDistance(double[] A, double[] B) {
		double distance = 0;
		
		if(A.length != B.length) {
			System.err.println("Array sizes must be the same.");
			return 2;
		}
		
		for(int i = 0;i < A.length;i++) 
			distance += Math.pow((A[i] - B[i]), 2);
		
		return Math.sqrt(distance);
	}
	
	public int getIndex(final List<Double> list, final double score) {
		return IntStream.range(0, list.size()).filter(i -> score == list.get(i)).findFirst().orElse(0);
	}
}
