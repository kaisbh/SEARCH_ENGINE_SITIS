package isped.sitis.se;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import isped.sitis.se.util.*;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


public class Indexer extends Parametre{

	private static IndexWriter writer;
	private static ArrayList<File> queue = new ArrayList<File>();

	// Constructeur
	Indexer(String indexDir, String analyzerLang) throws IOException {

		CharArraySet CharArraySetSW;
		IndexWriterConfig config;
		FSDirectory dir = FSDirectory.open(Paths.get(indexDir));
		switch (analyzerLang) {
		// Choix de l'Analyse du corpus
		case "FR":
			CharArraySetSW = new CharArraySet(getStopWord("./resources/StopWord/stop-words-french.txt"), true);
			FrenchAnalyzer analyzerFR = new FrenchAnalyzer(CharArraySetSW);
			config = new IndexWriterConfig(analyzerFR);
			break;
		case "EN":
			CharArraySetSW = new CharArraySet(getStopWord("./resources/StopWord/stop-words-english1.txt"), true);
			// EnglishAnalyzer analyzerEN = new
			// EnglishAnalyzer(EnglishAnalyzer.getDefaultStopSet());
			// EnglishAnalyzer analyzerEN = new EnglishAnalyzer(CharArraySetSW);
			EnglishAnalyzer analyzerEN = new EnglishAnalyzer();
			config = new IndexWriterConfig(analyzerEN);
			break;
		default:
			StandardAnalyzer analyzerStd = new StandardAnalyzer();
			config = new IndexWriterConfig(analyzerStd);
			break;
		}

		writer = new IndexWriter(dir, config);

	}

	public static void CreateIndex(String indexLocation, String corpusLocation, String analyzerLang) {
		Indexer indexer = null;
		FileUtil.deleteFiles(indexLocation);
		try {
			indexer = new Indexer(indexLocation, analyzerLang);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			indexer.indexFileOrDirectory(corpusLocation);
		} catch (Exception e) {
			System.out.println("Error indexing " + corpusLocation + " : " + e.getMessage());
		}
		try {
			indexer.closeIndex();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Chemain de l'index");
		// String indexLocation = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s1 = br.readLine();
		//System.out.println(
		//		"Enter the full path to add into the index (q=quit): (e.g. /home/ron/mydir or c:\\Users\\ron\\mydir)");
		//System.out.println("[Acceptable file types: .xml, .html, .html, .txt]");
		String s2 = null;
		s2 = br.readLine();

		CreateIndex(INDEX_DIR, CORPUS_DIR, "EN");

	}

	public static ArrayList<String> getStopWord(String FileName) {
		ArrayList<String> stop_words = new ArrayList<String>();
		try {
			BufferedReader is = new BufferedReader(new FileReader(FileName));
			String inputLine;
			while ((inputLine = is.readLine()) != null) {
				 stop_words.add(inputLine);
				// System.out.println(inputLine);
			}
		} catch (IOException io) {

		}
		return stop_words;
	}

	/**
	 * Indexes a file or directory
	 * 
	 * @param fileName
	 *            the name of a text file or a folder we wish to add to the index
	 * @throws java.io.IOException
	 *             when exception
	 */

	public void indexFileOrDirectory(String fileName) throws IOException {
		// ===================================================
		// gets the list of files in a folder (if user has submitted
		// the name of a folder) or gets a single file name (is user
		// has submitted only the file name)
		// ===================================================
		addFiles(new File(fileName));

		int originalNumDocs = writer.numDocs();
		for (File f : queue) {
			// System.out.println(f.getName());
			Document doc = new Document();
			if (f.getName().toLowerCase().endsWith(".pdf")) {
				String contents = "";
				PDDocument docPdf = null;
				try {
					docPdf = PDDocument.load(f);
					PDFTextStripper stripper = new PDFTextStripper();

					stripper.setLineSeparator("\n");
					//stripper.setStartPage(1);
					//stripper.setEndPage(5);// this mean that it will index the first 5 pages only
					contents = stripper.getText(docPdf);
					//System.out.println(contents);
					
					
					FieldType ft = new FieldType();
					ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
					ft.setStoreTermVectors(true);
					ft.setStoreTermVectorOffsets(true);
					ft.setStoreTermVectorPayloads(true);
					ft.setStoreTermVectorPositions(true);
					ft.setTokenized(true);
					ft.setStored(true);
					ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
					ft.setStoreTermVectors(true);
					Field fldContents = new Field("contents", contents, ft);
					doc.add(fldContents);
					Field fldPath = new Field("path", f.getPath(), ft);
					doc.add(fldPath);
					Field fldFilename = new Field("filename", f.getName(), ft);
					doc.add(fldFilename);

					writer.addDocument(doc);
					System.out.println("Added: " + f.toPath());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				FileReader fr = null;
				try {
					//Document doc = new Document();

					// ===================================================
					// add contents of file
					// ===================================================
					fr = new FileReader(f);
					BufferedReader is = new BufferedReader(fr);
					String inputLine;
					String txt = "";
					while ((inputLine = is.readLine()) != null) {
						txt = txt + inputLine;

					}

					// tf.fieldType().setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
					// doc.add(new TextField("contents", fr));
					// doc.add(new TextField("contents", new String(Files.readAllBytes(f.toPath())),
					// Field.Store.YES));
					// doc.add(new StringField("path", f.getPath(), Field.Store.YES));
					// doc.add(new StringField("filename", f.getName(), Field.Store.YES));
					FieldType ft = new FieldType();
					ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
					ft.setStoreTermVectors(true);
					ft.setStoreTermVectorOffsets(true);
					ft.setStoreTermVectorPayloads(true);
					ft.setStoreTermVectorPositions(true);
					ft.setTokenized(true);
					ft.setStored(true);
					ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
					ft.setStoreTermVectors(true);
					Field fldContents = new Field("contents", new String(Files.readAllBytes(f.toPath())), ft);
					doc.add(fldContents);
					Field fldPath = new Field("path", f.getPath(), ft);
					doc.add(fldPath);
					Field fldFilename = new Field("filename", f.getName(), ft);
					doc.add(fldFilename);

					writer.addDocument(doc);
					System.out.println("Added: " + f.toPath());
				} catch (Exception e) {
					System.out.println("Could not add: " + f.toPath());
				} finally {
					fr.close();
				}
			}

			/*
			*/
		}

		int newNumDocs = writer.numDocs();
		System.out.println("");
		System.out.println("************************");
		System.out.println((newNumDocs - originalNumDocs) + " documents added.");
		System.out.println("************************");

		queue.clear();
	}

	public static void addFiles(File file) {

		if (!file.exists()) {
			System.out.println(file + " does not exist.");
		}
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				addFiles(f);
			}
		} else {
			String filename = file.getName().toLowerCase();
			// ===================================================
			// Only index text files
			// ===================================================
			if (filename.endsWith(".htm") || filename.endsWith(".html") || filename.endsWith(".xml")
					|| filename.endsWith(".txt") || filename.endsWith(".pdf")) {
				queue.add(file);
			} else {
				System.out.println("Skipped " + filename);
			}
		}
	}

	/**
	 * Close the index.
	 * 
	 * @throws java.io.IOException
	 *             when exception closing
	 */
	public void closeIndex() throws IOException {
		writer.close();
	}

}
