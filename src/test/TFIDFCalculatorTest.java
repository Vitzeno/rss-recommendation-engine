package test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import textClassification.TFIDFCalculator;

class TFIDFCalculatorTest {
	
	private static final double DELTA = 0.01;

	@Test
	void tfTest() {
		List<String> document = new ArrayList<>(Arrays.asList("the", "cat", "jumped", "over", "the", "wall", "with", "an", "apple", "in", "his", "paws"));
		TFIDFCalculator tfidfCalc = new TFIDFCalculator();
		
		assertEquals(0.09, tfidfCalc.tf(document, "cat"), DELTA, "TF Test single");
		assertEquals(0.09, tfidfCalc.tf(document, "jumped"), DELTA, "TF Test single");
		assertEquals(0.16, tfidfCalc.tf(document, "the"), DELTA, "TF Test duplicates");
		assertEquals(0, tfidfCalc.tf(document, "car"), DELTA, "TF Test empty");
		assertEquals(0, tfidfCalc.tf(document, ""), DELTA, "TF Test space");
	}
	
	@Test
	void idfTest() {
		TFIDFCalculator tfidfCalc = new TFIDFCalculator();
		tfidfCalc.performIDF = true;
		List<String> document = new ArrayList<>(Arrays.asList("the", "cat", "jumped", "over", "the", "wall", "with", "an", "apple", "in", "his", "paws"));
		List<String> document2 = new ArrayList<>(Arrays.asList("the", "man", "was", "upset", "over", "something", "with", "his", "apple", "watch"));
		List<String> document3 = new ArrayList<>(Arrays.asList("the", "dog", "jumped", "over", "the", "cat", "who", "had", "an", "apple", "in", "his", "paws"));
		List<String> document4 = new ArrayList<>(Arrays.asList("the", "dog", "jumped", "over", "the", "cat", "who", "had", "an", "apple", "in", "his", "paws"));
		List<List<String>> documents = new ArrayList<List<String>>();
		documents.add(document);
		documents.add(document2);
		documents.add(document3);
		documents.add(document4);
		
		assertEquals(0.124, tfidfCalc.idf(documents, "cat"), DELTA);
		assertEquals(0.602, tfidfCalc.idf(documents, "upset"), DELTA);
		assertEquals(0.0, tfidfCalc.idf(documents, "the"), DELTA);
		assertEquals(0.602, tfidfCalc.idf(documents, "was"), DELTA);
	}
	
	@Test
	void tfidfTest() {
		TFIDFCalculator tfidfCalc = new TFIDFCalculator();
		List<String> document = new ArrayList<>(Arrays.asList("the", "cat", "jumped", "over", "the", "wall", "with", "an", "apple", "in", "his", "paws"));
		List<String> document2 = new ArrayList<>(Arrays.asList("the", "man", "was", "upset", "over", "something", "with", "his", "apple", "watch"));
		List<String> document3 = new ArrayList<>(Arrays.asList("the", "dog", "jumped", "over", "the", "cat", "who", "had", "an", "apple", "in", "his", "paws"));
		List<String> document4 = new ArrayList<>(Arrays.asList("the", "dog", "jumped", "over", "the", "cat", "who", "had", "an", "apple", "in", "his", "paws"));
		List<List<String>> documents = new ArrayList<List<String>>();
		documents.add(document);
		documents.add(document2);
		documents.add(document3);
		documents.add(document4);
		
		assertEquals(0.0, tfidfCalc.tfidf(document, documents, "the"), DELTA);
		assertEquals(0.011, tfidfCalc.tfidf(document, documents, "cat"), DELTA);
	}
	
	

}
