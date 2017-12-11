package isped.sitis.se;

import java.awt.TextField;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.pdfbox.io.RandomAccessRead;

public class VocabIndexer extends Parametre {
	public static void main(String[] args) throws ParseException {
		IndexWriter writer =  indexVocab(VOCAB_FILE);
		StandardAnalyzer analyzerStd = new StandardAnalyzer();
		
		
		//Create RAMDirectory instance
		RAMDirectory ramDir = new RAMDirectory();
		 
		//Builds an analyzer with the default stop words
		Analyzer analyzer = new StandardAnalyzer();
		 
		IndexReader reader = null;
		try
		{
		    //Create Reader
		    reader = DirectoryReader.open(ramDir);
		     
		    //Create index searcher
		    IndexSearcher searcher = new IndexSearcher(reader);
		     
		    //Build query
		    Query query = new QueryParser("contents", analyzerStd).parse("cancer");
		 
		    //Search the index
		    TopDocs foundDocs = searcher.search(query, 10);
		     
		    // Total found documents
		    System.out.println("Total Results :: " + foundDocs.totalHits);
		 
		    //Let's print found doc names and their content along with score
		    for (ScoreDoc sd : foundDocs.scoreDocs)
		    {
		        Document d = searcher.doc(sd.doc);
		        System.out.println("Document Concept : " + d.get("concept")
		                    + "  :: Content : " + d.get("content")
		                    + "  :: Score : " + sd.score);
		    }
		    //don't forget to close the reader
		    reader.close();
		}
		catch (IOException | ParseException e)
		{
		    //Any error goes here
		    e.printStackTrace();
		}
	}


	static IndexWriter indexVocab(String VOCAB_FILE) {
		RAMDirectory ramDir = new RAMDirectory();
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriter writer = null;
		try {
			// IndexWriter Configuration
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			iwc.setOpenMode(OpenMode.CREATE);

			// IndexWriter writes new index files to the directory
			writer = new IndexWriter(ramDir, iwc);

			// Create some docs with name and content
			BufferedReader br;
			String line;
			String[] splittedLigne = null;
			String[] splittedVocabTerms = null;
			try {
				br = new BufferedReader(new FileReader(new File(VOCAB_FILE)));
				line = br.readLine();
				while ((line = br.readLine()) != null) {
					splittedLigne = line.toLowerCase().split(":");
					String concept = splittedLigne[0];
					// if (splittedLigne[0].toLowerCase().equals(concept.toLowerCase())) {
					splittedVocabTerms = splittedLigne[1].split(",");
					for (int i = 0; i < splittedVocabTerms.length; i++) {
						Document doc = new Document();
						FieldType ft = new FieldType();
						ft.setStored(true);
						Field  fld1 = new Field("content", splittedVocabTerms[i], ft);
						doc.add(fld1);
						Field fld2 = new Field("concept", concept, ft);
						doc.add(fld2);
						writer.addDocument(doc);
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// don't forget to close the writer
			writer.close();
		} catch (IOException e) {
			// Any error goes here
			e.printStackTrace();
		}
		
		return writer;

	}

	
}