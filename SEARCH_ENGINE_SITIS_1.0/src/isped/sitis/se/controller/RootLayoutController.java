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
import isped.sitis.se.ConceptAnalyser;
import isped.sitis.se.ConceptSearcher;
import isped.sitis.se.Indexer;
import isped.sitis.se.MainApp;
import isped.sitis.se.Parametre;
import isped.sitis.se.Searcher;
import isped.sitis.se.model.DocScored;
import isped.sitis.se.model.File;

import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.lucene.queryparser.classic.ParseException;



import isped.sitis.se.util.FileUtil;

public class RootLayoutController extends Parametre {
	public final String indexLocation = INDEX_DIR;
	public final String corpusLocation = CORPUS_DIR;

	public RootLayoutController() {
	}

	public void UpdateIndexFr() throws Exception {
		Indexer.CreateIndex(corpusLocation, "FR");

	}

	public void UpdateIndexEn() throws Exception {
		Indexer.CreateIndex(corpusLocation, "EN");
	}

	public void UpdateIndexStd() throws Exception {
		Indexer.CreateIndex(corpusLocation, "");
	}

	public void UpdateIndexConcept() throws Exception {
		ConceptAnalyser analyser = new ConceptAnalyser(INDEX_DIR);
		analyser.makeDocList();
	}

	public void UpdateIndexall() throws Exception {
		Indexer.CreateIndex(corpusLocation, "FR");
		Indexer.CreateIndex(corpusLocation, "EN");
		Indexer.CreateIndex(corpusLocation, "");
		ConceptAnalyser analyser = new ConceptAnalyser(INDEX_DIR);
		analyser.makeDocList();
	}
	
	public void OpenVovab() throws Exception {
		java.io.File file = new java.io.File(VOCAB_FILE);
		Desktop desktop = Desktop.getDesktop();
		if (file.exists())
			desktop.open(file);
	}
	public void OpenAnalysedDoc() throws Exception {
		java.io.File file = new java.io.File(CONCEPT_INDEX_FILE);
		Desktop desktop = Desktop.getDesktop();
		if (file.exists())
			desktop.open(file);
	}
	
	
}