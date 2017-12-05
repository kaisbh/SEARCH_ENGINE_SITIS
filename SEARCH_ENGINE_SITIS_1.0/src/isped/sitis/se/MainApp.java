
package isped.sitis.se;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import isped.sitis.se.controller.PersonEditDialogController;
import isped.sitis.se.controller.PersonOverviewController;
import isped.sitis.se.controller.SearchController;
import isped.sitis.se.Parametre;
import isped.sitis.se.model.*;
import isped.sitis.se.util.FileUtil;

public class MainApp extends Application {
	public final String CORPUS_DIRECTORY = Parametre.CORPUS_DIR;
	private Stage primaryStage;
	private BorderPane rootLayout;
	private ArrayList<File> queue = new ArrayList<File>();
	/**
	 * The data as an observable list of Persons.
	 */
	private ObservableList<Person> personData = FXCollections.observableArrayList();
	private ObservableList<isped.sitis.se.model.File> fileData = FXCollections.observableArrayList();

	/**
	 * Constructor
	 */
	public MainApp() {
		// Add some sample data
		personData.add(new Person("Hans", "Muster"));
		personData.add(new Person("Ruth", "Mueller"));
		personData.add(new Person("Heinz", "Kurz"));
		personData.add(new Person("Cornelia", "Meier"));
		personData.add(new Person("Werner", "Meyer"));
		personData.add(new Person("Lydia", "Kunz"));
		personData.add(new Person("Anna", "Best"));
		personData.add(new Person("Stefan", "Meier"));
		personData.add(new Person("Martin", "Mueller"));
		
		FileUtil fileUtil = new FileUtil();
		queue = fileUtil.addFiles(new File(CORPUS_DIRECTORY));
		Iterator<File> iter = queue.iterator();
		Integer i = 0;
		while (iter.hasNext()) {
			i++;
			File f = iter.next();
			System.out.println( i.toString() +" "+f.getPath().toLowerCase());
			fileData.add(new isped.sitis.se.model.File(i.toString(), f.getPath(), "", ""));
		}
		
	}

	/**
	 * Returns the data as an observable list of Persons.
	 * 
	 * @return
	 */
	public ObservableList<Person> getPersonData() {
		return personData;
	}
	
	public ObservableList<isped.sitis.se.model.File> getFileData() {
		return fileData;
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Search Engine SITIS");
		this.primaryStage.getIcons().add(new Image("file:Logo.png"));

		//addFiles(File file)
		initRootLayout();
	
		//showPersonOverview();
		showSearchOverview();
	}
	
	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void showSearchOverview() {
		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/SearchOverview.fxml"));
			AnchorPane searchOverview = (AnchorPane) loader.load();

			// Set person overview into the center of root layout.
			rootLayout.setCenter(searchOverview);

			// Give the controller access to the main app.
			SearchController controller = loader.getController();
			controller.setMainApp(this);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Shows the person overview inside the root layout.
	 */
	public void showPersonOverview() {
		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/PersonOverview.fxml"));
			AnchorPane personOverview = (AnchorPane) loader.load();

			// Set person overview into the center of root layout.
			rootLayout.setCenter(personOverview);

			// Give the controller access to the main app.
			PersonOverviewController controller = loader.getController();
			controller.setMainApp(this);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Returns the main stage.
	 * 
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Opens a dialog to edit details for the specified person. If the user clicks
	 * OK, the changes are saved into the provided person object and true is
	 * returned.
	 *
	 * @param person
	 *            the person object to be edited
	 * @return true if the user clicked OK, false otherwise.
	 */
	public boolean showPersonEditDialog(Person person) {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();

			loader.setLocation(MainApp.class.getResource("view/PersonEdit.fxml"));

			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();

			dialogStage.setTitle("Edit Person");

			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			// Set the person into the controller.
			PersonEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setPerson(person);

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

			return controller.isOkClicked();

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}