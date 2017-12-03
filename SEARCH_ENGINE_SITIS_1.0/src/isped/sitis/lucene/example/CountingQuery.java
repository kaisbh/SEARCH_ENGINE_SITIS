package isped.sitis.lucene.example;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;


public class CountingQuery {
	public static String indexLocation = "D:\\Projet_Gayo\\Index" ;
	//public static IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation)));
/*
	public static  IndexSearcher searcher ;
	public static  StandardAnalyzer analyzer = new StandardAnalyzer();
	config = new IndexWriterConfig(analyzer);
	private void processDoc(int docid, String fieldName, Set<String> selector, TermsEnum te, CharArraySet set)
	
			throws IOException {
		Terms terms = searcher.getIndexReader().getTermVector(docid, fieldName);
		if (terms != null) {
			te = terms.iterator();
			BytesRef bytes = te.next();
			while (bytes != null) {
				set.add(bytes);
			}
		} else if (analyzer != null) {
			Document document = searcher.doc(docid, selector);
			IndexableField[] fields = document.getFields(fieldName);
			if (fields == null) {
				return;
			}
			for (IndexableField field : fields) {
				String s = field.stringValue();
				// is this possible
				if (s == null) {
					continue;
				}
				//processFieldEntry(fieldName, s, set);
			}

		} else {
			throw new IllegalArgumentException(
					"The field must have a term vector or the analyzer must" + " not be null.");
		}
	}
*/
}
