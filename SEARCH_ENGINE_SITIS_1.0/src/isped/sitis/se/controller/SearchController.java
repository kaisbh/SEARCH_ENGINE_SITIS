package isped.sitis.se.controller;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import isped.sitis.se.MainApp;
import isped.sitis.se.model.Person;
import isped.sitis.se.util.DateUtil;
public class SearchController {
	private MainApp mainApp;
	public SearchController() {
    }
	public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        //personTable.setItems(mainApp.getPersonData());
    }
}
