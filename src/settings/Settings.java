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
	
	enum calculation {
		  COSINE,
		  EUCLIDEAN
	}
	
	public calculation method;
	public boolean openInBrowser;
	public int readerFontSize;
	
	public Settings() {
		this.method = calculation.COSINE;
		this.openInBrowser = true;
		this.readerFontSize = 25;
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
