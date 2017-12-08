package isped.sitis.se.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import isped.sitis.se.DocScored;
import isped.sitis.se.IndexConceptAnalyser;
import isped.sitis.se.IndexConceptSearcher;
import isped.sitis.se.Indexer;
import isped.sitis.se.MainApp;
import isped.sitis.se.Parametre;
import isped.sitis.se.Searcher;
import isped.sitis.se.model.File;

import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.lucene.queryparser.classic.ParseException;

import isped.sitis.se.model.Person;
import isped.sitis.se.util.DateUtil;
import isped.sitis.se.util.FileUtil;

public class SearchController extends Parametre {
	public final String indexLocation = INDEX_DIR;
	public final String corpusLocation = CORPUS_DIR;

	@FXML
	private TableView<isped.sitis.se.model.File> fileTable;
	@FXML
	private TableColumn<isped.sitis.se.model.File, String> numFileColumn;
	@FXML
	private TableColumn<isped.sitis.se.model.File, String> pathFileColumn;
	@FXML
	private TableColumn<isped.sitis.se.model.File, String> scoreFileColumn;
	@FXML
	private TableColumn<isped.sitis.se.model.File, String> conceptFileColumn;
	@FXML
	private TextField searchQuery;

	@FXML
	private RadioButton FR;
	@FXML
	private RadioButton EN;
	@FXML
	private RadioButton Indiff;
	private ToggleGroup group;
	// Reference to the main application.
	private MainApp mainApp;
	private Stage dialogStage;
	private RadioButton selectedRadioButton;

	public SearchController() {
	}

	// @FXML
	// private TableRow<isped.sitis.se.model.File> row = new TableRow<>();
	// TableRow<String> row = new TableRow<String>();

	@FXML
	private void initialize() throws Exception {
		// FR.setText("FR");
		// EN.setText("EN");
		group = new ToggleGroup();
		FR.setToggleGroup(group);
		EN.setToggleGroup(group);
		Indiff.setToggleGroup(group);

		// System.out.println(FR.getText());
		// FR.setText("FR");
		// FR.setSelected(true);
		Indexer.CreateIndex(corpusLocation, "FR");
		Indexer.CreateIndex(corpusLocation, "EN");
		IndexConceptAnalyser analyser = new IndexConceptAnalyser(INDEX_DIR);
		analyser.makeDocList();

		searchQuery.setText("Hodgkin lymphoma");

		numFileColumn.setCellValueFactory(cellData -> cellData.getValue().numFileProperty());
		pathFileColumn.setCellValueFactory(cellData -> cellData.getValue().pathFileProperty());
		scoreFileColumn.setCellValueFactory(cellData -> cellData.getValue().scoreFileProperty());
		conceptFileColumn.setCellValueFactory(cellData -> cellData.getValue().conceptFileProperty());
		fileTable.setRowFactory(tv -> {
			TableRow<isped.sitis.se.model.File> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					isped.sitis.se.model.File rowData = row.getItem();
					// System.out.println(rowData);
					java.io.File file = new java.io.File(rowData.getpathFile());
					Desktop desktop = Desktop.getDesktop();
					if (file.exists())
						try {
							desktop.open(file);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			});
			return row;
		});

	}

	public void UpdateIndex() throws Exception {
		selectedRadioButton = (RadioButton) group.getSelectedToggle();
		if (Indiff.isSelected()) {

		} else {
			// Indexer.CreateIndex(CORPUS_DIR, selectedRadioButton.getText());
		}
	}

	public void ShowResultSearch() throws Exception {
		
		isped.sitis.se.model.File FileResult = null;

		String errorMessage = "";
		if (searchQuery.getText() == null || searchQuery.getText().length() == 0) {
			errorMessage += "Veuillez introduire votre requête\n";
		}
		if (errorMessage.length() == 0) {
			fileTable.getItems().clear();

			ObservableList<isped.sitis.se.model.File> fileData = FXCollections.observableArrayList();
			
			try {
				selectedRadioButton = (RadioButton) group.getSelectedToggle();
				if (Indiff.isSelected()) {
					// String query="cancer lymphoma patient lung" ;
					//IndexConceptSearcher searcher = new IndexConceptSearcher(searchQuery.getText());
					ArrayList<DocScored> resultatConcept = new ArrayList<DocScored>();
					
				
					resultatConcept= IndexConceptSearcher.search(searchQuery.getText());
					Iterator<DocScored> itr = resultatConcept.iterator();
					while (itr.hasNext()) {
						DocScored document = itr.next();
						fileData.add(new isped.sitis.se.model.File(String.valueOf(document.numOrder), document.docPath,
								String.valueOf(document.scoreDocConcept), document.docConcept));
						FileResult = new isped.sitis.se.model.File(String.valueOf(document.numOrder), document.docPath,
								String.valueOf(document.scoreDocConcept), document.docConcept);
						mainApp.getFileData().add(FileResult);
						// System.out.println("Oder:"+doc.numOrder+"; Path :" + doc.docPath + "; Concept
						// :" + doc.docConcept +"; Score Concept:" + doc.scoreDocConcept);
					}
				
				} else {
					ArrayList<String> resultat = new ArrayList<String>();
					resultat = Searcher.Search(searchQuery.getText(), 10, selectedRadioButton.getText());
					Iterator<String> iterator = resultat.iterator();
					while (iterator.hasNext()) {
						// System.out.println(iterator.next());
						String s[] = iterator.next().split(";");
						System.out.println(s[0] + ";" + s[1] + ";" + s[2]);
						fileData.add(new isped.sitis.se.model.File(s[0], s[1], s[2], ""));
						FileResult = new isped.sitis.se.model.File(s[0], s[1], s[2], "");
						mainApp.getFileData().add(FileResult);
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}

		} else {
			// Show the error message.
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(dialogStage);
			alert.setTitle("Invalid Fields");
			alert.setHeaderText("Please correct invalid fields");
			alert.setContentText(errorMessage);
			alert.showAndWait();

		}

	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

		// Add observable list data to the table
		fileTable.setItems(mainApp.getFileData());
	}

	@FXML
	private void openFile() throws IOException {
		File selectedFile = fileTable.getSelectionModel().getSelectedItem();
		if (selectedFile != null) {
			java.io.File file = new java.io.File(selectedFile.getpathFile());
			Desktop desktop = Desktop.getDesktop();
			if (file.exists())
				desktop.open(file);
		} else {
			// Nothing selected.
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("No Selection");
			alert.setHeaderText("No File Selected");
			alert.setContentText("Please select a file in the table.");

			alert.showAndWait();
		}

	}
}
