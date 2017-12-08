package isped.sitis.jena.example;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefIterator;
import org.apache.lucene.util.MapOfSets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.LongToIntFunction;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import isped.sitis.se.IndexConceptAnalyser;
import isped.sitis.se.Parametre;

import org.apache.lucene.search.similarities.*;
import org.apache.lucene.search.spans.SpanWeight.Postings;
import org.apache.lucene.codecs.lucene50.*;

public class test extends Parametre {
	static IndexReader reader;
	static IndexSearcher searcher;

	public static void main(String[] args) throws Exception {
		reader = DirectoryReader.open(FSDirectory.open(Paths.get(INDEX_DIR)));
		searcher = new IndexSearcher(reader);
		IndexConceptAnalyser analyser = new IndexConceptAnalyser(INDEX_DIR);

		// Document doc = reader.document(1);
		// System.out.println( doc.get("contents"));
		Terms contentsTermVector = reader.getTermVector(5, "contents");
		TermsEnum termsEnum = contentsTermVector.iterator();
		// System.out.println(contentsTermVector.getDocCount());
		// System.out.println(contentsTermVector.getSumTotalTermFreq());

		// DocsEnum docs = termEnum.docs(reader.getLiveDocs());
		// TermsEnum termEnum = contentsTermVector.MultiFields.getTerms(reader,
		// "contents").iterator();
		BytesRef bytesRef;
		// PostingsEnum postingsEnum = MultiFields.getTermPositionsEnum(reader,
		// "contents", bytesRef);
		while ((bytesRef = termsEnum.next()) != null) {
			// int freq = reader.docFreq(new Term("contents", bytesRef));
			// org.apache.lucene.codecs.lucene50.Lucene50PostingsReader postingsEnum;
			// org.apache.lucene.codecs.lucene50.Lucene50LiveDocsFormat docs;
			// org.apache.lucene.codecs.lucene50.Lucene50PostingsReader postingsReader;
			// a = postingsReader.postings(reader, "contents", bytesRef);
			Fields termVectors = reader.getTermVectors(1);

			// = MultiFields.getTermDocsEnum(reader, "contents", bytesRef,PostingsEnum.ALL
			// );
			PostingsEnum termss = MultiFields.getTermDocsEnum(reader, "contents", bytesRef);

			// TermsEnum itr = terms.iterator();
			// long freq = terms.getSumTotalTermFreq();

			// System.out.println(bytesRef.utf8ToString() + " " + termss.docID());
			// bytesRef = termsEnum.next();
			// DocsEnum docEnum;

			// System.out.println(bytesRef.utf8ToString() + " in " + freq + " documents");
		}
		// main map

		TermsEnum termEnum = MultiFields.getTerms(reader, "contents").iterator();
		while (termEnum.next() != null) {
			if (termEnum.term().utf8ToString().equals("lymphom")) {
				PostingsEnum docEnum = MultiFields.getTermDocsEnum(reader, "contents", termEnum.term());
				int doc = docEnum.NO_MORE_DOCS;
				int count = 0;
				while (docEnum.nextDoc() != doc) {
					if (docEnum.docID() == 1) {
						System.out.println(docEnum.docID() + " :" + reader.document(docEnum.docID()).get("path") + ": "
								+ termEnum.term().utf8ToString() + ": " + docEnum.freq());
					}
				}

			}
		}
	}

	/*
	 * Iterator<Concept> itr2 = concepts.iterator(); BytesRef term =
	 * termsEnum.next(); while (term!=null) { DocsEnum docEnum =
	 * MultiFields.getTermDocsEnum(reader,MultiFields.getLiveDocs(reader),
	 * "content",term);
	 * 
	 * System.out.println(term.utf8ToString()); term = termsEnum.next(); }
	 */

}
