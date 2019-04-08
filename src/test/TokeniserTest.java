package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import textClassification.Tokeniser;

class TokeniserTest {

	@Test
	void getTokensTest() {
		Tokeniser tokeniser = new Tokeniser();
		
		assertEquals(new ArrayList<>(Arrays.asList("the", "cat", "jumper", "over", "the", "wall")), tokeniser.getTokens("The cat jumper over the wall"));
		assertEquals(new ArrayList<>(Arrays.asList("he", "was", "very", "angry", "today")), tokeniser.getTokens("He was very angry today"));
		assertEquals(new ArrayList<>(Arrays.asList("hello", "world", "goodbye", "array")), tokeniser.getTokens("Hello World, Goodbye array"));
	}

}
