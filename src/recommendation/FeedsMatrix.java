package recommendation;

import java.util.ArrayList;
import java.util.List;
import Jama.Matrix;
import feed.FeedItem;
import model.RSSData;
import utilities.ToolBox;

public class FeedsMatrix {
	
	private RSSData RSSDataModel = RSSData.getInstance();
	private List<FeedItem> feeds = new ArrayList<FeedItem>();
	private List<String> tokens = new ArrayList<String>();
	private ToolBox toolBox = new ToolBox();
	
	private Matrix matrix;
	


	public FeedsMatrix() {
		feeds = RSSDataModel.getFeedItems();
		tokens = RSSDataModel.getTokens();
		setUpMatrix();
	}
	
	private void setUpMatrix() {
		double[][] array = new double[feeds.size()][tokens.size()];
		matrix = new Matrix(array);
	}
	
	public void printMatrixData() {
		toolBox.printMatrixData(matrix);
	}
	
	public Matrix getMatrix() {
		return matrix;
	}

	public void setMatrix(Matrix matrix) {
		this.matrix = matrix;
	}
	
	/*
	double[][] array = {
			{1, 1, 1, 0, 0},
			{3, 3, 3, 0, 0},
			{4, 4, 4, 0, 0},
			{5, 5, 5, 0, 0},
			{0, 2, 0, 4, 4},
			{0, 0, 0, 5, 5},
			{0, 1, 0, 2, 2}
	};
	
	Matrix A = new Matrix(array);
	SingularValueDecomposition svd = A.svd();
	Matrix U = svd.getU();
	Matrix S = svd.getS();
	Matrix V = svd.getV();
	
	/**
	 * Get a submatrix.
		Parameters:
		i0 - Initial row index
		i1 - Final row index
		c - Array of column indices.
	 */
	/*
	Matrix Up = U.getMatrix(0, U.getRowDimension() - 1,  new int[] {0, 1});
	Matrix Sp = S.getMatrix(0, S.getRowDimension() - 1,  new int[] {0, 1});
	Matrix Vp = V.getMatrix(0, V.getRowDimension() - 1,  new int[] {0, 1});
	
	Matrix query = A.getMatrix(new int[] {4}, 0, A.getColumnDimension() - 1);
	Matrix query2 = A.getMatrix(new int[] {1}, 0, A.getColumnDimension() - 1);

	
	Matrix vector = query.times(Vp);
	Matrix vector2 = query2.times(Vp);
	
	for(int i = 0;i < vector.getRowDimension();i++) {
		for(int j = 0;j < vector.getColumnDimension();j++) {
			System.out.print(vector.get(i, j) + " ");
		}
		System.out.println();
	}
	
	for(int i = 0;i < vector2.getRowDimension();i++) {
		for(int j = 0;j < vector2.getColumnDimension();j++) {
			System.out.print(vector2.get(i, j) + " ");
		}
		System.out.println();
	}
	*/

}
