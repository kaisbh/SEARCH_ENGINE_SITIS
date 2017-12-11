package isped.sitis.lucene.example;

import java.io.IOException;

import org.apache.lucene.queryparser.flexible.core.util.StringUtils;

import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.analysis.Tokenizer;

public class test {
	public static void main(String[] args) throws IOException {	
		ClassicSimilarity cs= new ClassicSimilarity();
		//cs.computeWeight(collectionStats, termStats)
		String str = "heart,stroke,pharmacologic,valvular,myocardial,electrocardiogram,cardiovascular";
		Tokenizer t =new Tokenizer() {
			
			@Override
			public boolean incrementToken() throws IOException {
				// TODO Auto-generated method stub
				return false;
			}
		};
		int distance = org.apache.commons.lang3.StringUtils.getLevenshteinDistance("hat", "kat");
		System.out.println(distance);
	}

}
