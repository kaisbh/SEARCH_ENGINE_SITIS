package isped.sitis.lucene.example;

import java.io.IOException;

import org.apache.lucene.queryparser.flexible.core.util.StringUtils;



public class test {
	public static void main(String[] args) throws IOException {	
		int distance = org.apache.commons.lang3.StringUtils.getLevenshteinDistance("cattyyy", "hat");
		System.out.println(distance);
	}

}
