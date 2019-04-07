package settings;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class Settings {
	public static String RES_LOC = "settings/";
	public static String FILE_TYPE = ".txt";
	public static String CONFIG_FILE = RES_LOC + "config" + FILE_TYPE;
	
	public enum calculation {
		  COSINE,
		  EUCLIDEAN
	}
	
	public calculation method;
	public boolean openInBrowser;
	public int readerFontSize;
	public String readerFont;
	public String readerTheme;
	public String accentColour;
	
	/**
	 * Constructor serves to setup default values
	 */
	public Settings() {
		this.method = calculation.COSINE;
		this.openInBrowser = true;
		this.readerFontSize = 16;
		this.readerFont = "System";
		this.readerTheme = "Lapis Light";
		this.accentColour = "#0093ff";
	}
	
	public String getAccentColour() {
		return accentColour;
	}

	public void setAccentColour(String accentColour) {
		this.accentColour = accentColour;
	}

	public String getReaderTheme() {
		return readerTheme;
	}

	public void setReaderTheme(String readerTheme) {
		this.readerTheme = readerTheme;
	}

	public String getReaderFont() {
		return readerFont;
	}

	public void setReaderFont(String readerFont) {
		this.readerFont = readerFont;
	}

	public calculation getMethod() {
		return method;
	}

	public void setMethod(calculation method) {
		this.method = method;
	}

	public boolean isOpenInBrowser() {
		return openInBrowser;
	}

	public void setOpenInBrowser(boolean openInBrowser) {
		this.openInBrowser = openInBrowser;
	}

	public int getReaderFontSize() {
		return readerFontSize;
	}

	public void setReaderFontSize(int readerFontSize) {
		this.readerFontSize = readerFontSize;
	}
	
	/**
	 * Initialises settings values and writes into JSON
	 * file
	 */
	public static void initSettings() {
		Writer writer = null;
		try {
			System.out.println("Init settings...");
			writer = new FileWriter(CONFIG_FILE);
			Gson gson = new GsonBuilder()
					  .setPrettyPrinting()
					  .create();
			gson.toJson(new Settings(), writer);
			writer.close();
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns a settings object by reading from config JSON file,
	 * if file does not exist it is created and a object with
	 * default values is returned
	 * @return
	 */
	public static Settings getSettings() {
		Gson gson = new Gson();
		Settings setting = new Settings();
		try {
			setting = gson.fromJson(new FileReader(CONFIG_FILE), Settings.class);
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			initSettings();
			e.printStackTrace();
		}
		
		return setting;
	}
	
	/**
	 * Writes a settings object to a JSON file
	 * @param newSettings
	 */
	public static void writeSettingsToFile(Settings newSettings) {
		Writer writer = null;
		try {
			System.out.println("Init settings...");
			writer = new FileWriter(CONFIG_FILE);
			Gson gson = new GsonBuilder()
					  .setPrettyPrinting()
					  .create();
			gson.toJson(newSettings, writer);
			writer.close();
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}
}
