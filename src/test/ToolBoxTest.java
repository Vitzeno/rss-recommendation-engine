package test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.linear.MatrixUtils;
import org.junit.jupiter.api.Test;
import javafx.scene.paint.Color;
import utilities.ToolBox;

class ToolBoxTest {
	
	private static final double DELTA = 0.01;
	
	@Test
	void cleanStringTest() {
		assertEquals("test", ToolBox.cleanString(" test"), "Remove space from front");
		assertEquals("test", ToolBox.cleanString("te st"), "Remove space from middle");
		assertEquals("test", ToolBox.cleanString("test "), "Remove space from end");
	}
	
	@Test
	void cleanColourTest() {
		assertEquals("#4286f4", ToolBox.cleanColour("0x4286f4"), "Remove 0x and replace with #");
		assertEquals("#73b79b", ToolBox.cleanColour("0x73b79b"), "Remove 0x and replace with #");
		assertEquals("#30a072", ToolBox.cleanColour("0x30a072"), "Remove 0x and replace with #");
		assertEquals("#9be8c8", ToolBox.cleanColour("0x9be8c8"), "Remove 0x and replace with #");
	}
	
	@Test
	void cosineSimilarityTest() {
		assertEquals(0.9647638212377322, ToolBox.cosineSimilarity(new double[] {3,1}, new double[] {3,2}), DELTA ,"Test 2D vector");
		assertEquals(0.807391122257898, ToolBox.cosineSimilarity(new double[] {4,1}, new double[] {5,6}), DELTA ,"Test 2D vector");
		assertEquals(0.4408292859799824, ToolBox.cosineSimilarity(new double[] {62, 34, 6}, new double[] {23, 4, 56}), DELTA ,"Test 3D vector");
		assertEquals(0.9269770925770784, ToolBox.cosineSimilarity(new double[] {46, 3, 65}, new double[] {54, 46, 89}), DELTA ,"Test 3D vector");
		assertNotEquals(0.9269770925770784, ToolBox.cosineSimilarity(new double[] {46, 65}, new double[] {54, 46, 89}), DELTA );
	}
	
	@Test
	void euclideanDistance() {
		assertEquals(1, ToolBox.euclideanDistance(new double[] {3,1}, new double[] {3,2}), DELTA ,"Test 2D vector");
		assertEquals(5.0990195135927845, ToolBox.euclideanDistance(new double[] {4,1}, new double[] {5,6}), DELTA ,"Test 2D vector");
		assertEquals(70.14983962918234, ToolBox.euclideanDistance(new double[] {62, 34, 6}, new double[] {23, 4, 56}), DELTA ,"Test 3D vector");
		assertEquals(49.889878733065686, ToolBox.euclideanDistance(new double[] {46, 3, 65}, new double[] {54, 46, 89}), DELTA ,"Test 3D vector");
		assertNotEquals(49.889878733065686, ToolBox.euclideanDistance(new double[] {46, 65}, new double[] {54, 46, 89}), DELTA);
	}
	
	@Test 
	void getIndexTest() {
		List<Double> testList = new ArrayList<>(Arrays.asList(1.56, 2.62, 3.77, 4.44, 5.04, 6.15));
		assertEquals(0,ToolBox.getIndex(testList, 1.56), DELTA, "Get index of values given array");
		assertEquals(1,ToolBox.getIndex(testList, 2.62), DELTA, "Get index of values given array");
		assertEquals(2,ToolBox.getIndex(testList, 3.77), DELTA, "Get index of values given array");
		assertEquals(3,ToolBox.getIndex(testList, 4.44), DELTA, "Get index of values given array");
		assertEquals(4,ToolBox.getIndex(testList, 5.04), DELTA, "Get index of values given array");
		assertEquals(5,ToolBox.getIndex(testList, 6.15), DELTA, "Get index of values given array");
	}
	
	@Test
	void intToStringTest() {
		assertEquals(3, ToolBox.intToString("3"), DELTA, "Basic string");
		assertEquals(-5, ToolBox.intToString("-5"), DELTA, "Negative string");
		assertEquals(0, ToolBox.intToString("0"), DELTA, "Zero string");
		assertNotEquals(5, ToolBox.intToString(" 5"), DELTA);
	}
	
	@Test
	void getSumOfSquaresTest() {
		double array1[][] = { 
				{2, 0, 0, 0, 0}, 
				{0, 3, 0, 0, 0},
				{0, 0, 4, 0, 0},
				{0, 0, 0, 8, 0},
				{0, 0, 0, 0, 9}
		};
		double array2[][] = { 
				{45, 0, 0, 0, 0}, 
				{0, 56, 0, 0, 0},
				{0, 0, 78, 0, 0},
				{0, 0, 0, 12, 0},
				{0, 0, 0, 0, 8}
		};
		double array3[][] = { 
				{0, 0, 0, 0, 0}, 
				{0, 3.4, 0, 0, 0},
				{0, 0, 6.8, 0, 0},
				{0, 0, 0, 4.89, 0},
				{0, 0, 0, 0, 9.99}
		};
		
		assertEquals(174.0, ToolBox.getSumOfSquares(MatrixUtils.createRealMatrix(array1)), DELTA, "Simple matrix");
		assertEquals(11453.0, ToolBox.getSumOfSquares(MatrixUtils.createRealMatrix(array2)), DELTA, "Matrix of large numbers");
		assertEquals(181.5122, ToolBox.getSumOfSquares(MatrixUtils.createRealMatrix(array3)), DELTA, "Matrix of doubles");
	}
	
	@Test
	void getCoverageTest() {
		assertEquals(60, ToolBox.getCoverage(75, 45), DELTA, "");
		assertEquals(56, ToolBox.getCoverage(100, 56), DELTA);
		assertEquals(36.923, ToolBox.getCoverage(65, 24), DELTA);
	}
	
	@Test
	void ligthenColourRest() {
		Color toLighten = (Color.web("#4286f4"));
		Color lighter = toLighten.brighter();
		assertEquals(lighter, ToolBox.lightenColour(toLighten));
	}
	
	@Test
	void darkenColourRest() {
		Color toDarken = (Color.web("#4286f4"));
		Color lighter = toDarken.darker();
		assertEquals(lighter, ToolBox.darkenColour(toDarken));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
