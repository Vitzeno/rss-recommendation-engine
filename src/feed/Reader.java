package feed;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class servers as a reader/writer which adds and removes feeds from the users list of saved feeds
 * @author Mohamed
 *
 */
public class Reader {
	
	private String RES_LOC = "res/feeds/";
	private String FILE_TYPE = ".txt";
	
	/**
	 * This method reads a file line by line and returns the contents as an ArrayList
	 * @param fileName
	 * @return
	 */
	public ArrayList<String> readFile(String fileName) {
		ArrayList<String> feeds = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(new File(RES_LOC + fileName + FILE_TYPE)))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       feeds.add(line);
		    }
		} catch (FileNotFoundException e) {
			System.err.println("File not found, make sure correct file name is entered");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO Error");
			e.printStackTrace();
		}
		
		System.out.println(feeds);
		return feeds;
	}
	
	/**
	 * This method appends to the end of the file a single new line of text
	 * @param fileName
	 * @param output
	 */
	public void appendFile(String fileName, String output) { 
		File file = new File(RES_LOC + fileName + FILE_TYPE);
		FileWriter fr;
		try {
			fr = new FileWriter(file, true);
			BufferedWriter br = new BufferedWriter(fr);
			br.write(output + '\n');
			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println("IO Error");
			e.printStackTrace();
		}
	}

}
