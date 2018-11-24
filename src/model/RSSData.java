package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RSSData {
	
	private final ObservableList<String> RSSList = FXCollections.observableArrayList();
	
	public void addToList(String item) {
		RSSList.add(item);
	}
	
	public ObservableList<String> getRSSList() {
        return RSSList ;
    }

}
