package utilities;

import Jama.Matrix;

/**
 * This class contains various helper methods used in multiple
 * parts of the code base.
 * @author Mohamed
 *
 */
public class ToolBox {
	
	private static ToolBox single_instance = null;
	
	/**
	 * private constructor ensures singleton nature of class
	 */
	private ToolBox() {
		
	}
	
	/**
	 * This method servers to provide an instance of the class
	 * and ensure that only one instance exists
	 * @return
	 */
	public static ToolBox getInstance() {
		if(single_instance == null)
			single_instance = new ToolBox();
		
		return single_instance;
	}

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
}
