package utilities;

import java.util.List;
import Jama.Matrix;
import feed.Feed;
import feed.FeedItem;

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
	public void printMatrix(Matrix m) {
		for(int i = 0;i < m.getRowDimension();i++) {
			for(int j = 0;j < m.getColumnDimension();j++) {
				System.out.print(m.get(i, j) + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void printMatrixData(Matrix m) {
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
}
