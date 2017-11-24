package MyPackage;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import java.io.*;
import java.util.ArrayList;

public class MyIndexer {
	private static StandardAnalyzer analyzer = new StandardAnalyzer();
	// getStopWord("D:\\RIIFT\\MyCorpus\\1.txt");
	private static CharArraySet CharArraySetSWFR = new CharArraySet(
			getStopWord("D:\\Projet_Gayo\\RIIFT\\stop-words-collection-2011.11.21\\stop-words\\stop-words-french.txt"),
			true);
	private static FrenchAnalyzer analyzerFR = new FrenchAnalyzer(CharArraySetSWFR);
	private static CharArraySet CharArraySetSWEN = new CharArraySet(
			getStopWord(
					"D:\\Projet_Gayo\\RIIFT\\stop-words-collection-2011.11.21\\stop-words\\stop-words-english1.txt"),
			true);
	private static EnglishAnalyzer analyzerEN = new EnglishAnalyzer(CharArraySetSWEN);

	private IndexWriter writer;
	private ArrayList<File> queue = new ArrayList<File>();

	public void indexFileOrDirectory(String fileName) throws IOException {
		addFiles(new File(fileName));
		int originalNumDocs = writer.numDocs();
		Integer i = 0;
		for (File f : queue) {
			FileReader fr = null;
			try {
				Document doc = new Document();
				fr = new FileReader(f);
				doc.add(new TextField("contents", fr));
				doc.add(new StringField("path", f.getPath(), Field.Store.YES));
				doc.add(new StringField("filename", f.getName(), Field.Store.YES));
				doc.add(new StringField("id", i.toString(), Field.Store.YES));
				writer.addDocument(doc);
				System.out.println("Ajout�: " + f);
				i++;
			} catch (Exception e) {
				System.out.println("Impossible d'ajouter: " + f);
			} finally {
				fr.close();
			}
		}
		int newNumDocs = writer.numDocs();
		System.out.println("");
		System.out.println("############################");
		System.out.println((newNumDocs - originalNumDocs) + " documents ajout�s.");
		System.out.println("###########################");
		queue.clear();
	}

	private void addFiles(File file) {

		if (!file.exists()) {
			System.out.println(file + " n'existe ps.");
		}
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				addFiles(f);
			}
		} else {
			String filename = file.getName().toLowerCase();
			if (filename.endsWith(".htm") || filename.endsWith(".html") || filename.endsWith(".xml")
					|| filename.endsWith(".txt")) {
				queue.add(file);
			} else {
				System.out.println("Ignor� " + filename);
			}
		}
	}

	//
	public void closeIndex() throws IOException {
		writer.close();
	}

	public static ArrayList<String> getStopWord(String FileName) {
		ArrayList<String> stop_words = new ArrayList();
		try {
			BufferedReader is = new BufferedReader(new FileReader(FileName));
			String inputLine;
			while ((inputLine = is.readLine()) != null) {
				// stop_words.add(inputLine);
				// System.out.println(inputLine);
			}
		} catch (IOException io) {

		}
		return stop_words;
	}

	MyIndexer(String indexDir) throws IOException {
		FSDirectory dir = FSDirectory.open(new File(indexDir).toPath());
		// IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriterConfig config = new IndexWriterConfig(analyzerFR);
		writer = new IndexWriter(dir, config);

	}

	public static void main(String[] args) throws IOException {
		/*
		 * //System.out.println("Entrez le chemin o�  l'index doit �tre cr��:"); String
		 * indexLocation = null;
		 * 
		 * BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		 * System.out.println("OK!"); String s = br.readLine(); MyIndexer indexer =
		 * null;
		 * 
		 * try { indexLocation = s; indexer = new MyIndexer(s); } catch (Exception ex) {
		 * System.out.println("Ne peut cr�er l'index..." + ex.getMessage());
		 * System.exit(-1); }
		 * 
		 * while (!s.equalsIgnoreCase("q")) { try { System.out.println(
		 * "Donner le chemin du corpus:");
		 * System.out.println("[Types de fichiers accept�s: .xml, .html, .html, .txt]");
		 * s = br.readLine(); if (s.equalsIgnoreCase("q")) { break; }
		 * 
		 * // try to add file into the index indexer.indexFileOrDirectory(s); } catch
		 * (Exception e) { System.out.println("Erreur pour indexer " + s + " : " +
		 * e.getMessage()); } }
		 */
		System.out.println("Donner le chemin du corpus:");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = br.readLine();
		MyIndexer indexer = null;
		System.out.println(s);
		indexer = new MyIndexer(s);
		try {
			//indexLocation = s;
			indexer = new MyIndexer(s);
		} catch (Exception ex) {
			System.out.println("Ne peut cr�er l'index..." + ex.getMessage());
			System.exit(-1);
		}
		indexer.closeIndex();

	}
}
