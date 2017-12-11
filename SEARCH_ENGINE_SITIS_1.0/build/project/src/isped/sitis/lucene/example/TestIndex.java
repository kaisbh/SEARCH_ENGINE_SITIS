package isped.sitis.lucene.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestIndex {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 String indexLocation = null;
		    BufferedReader br = new BufferedReader(
		            new InputStreamReader(System.in));
		    String s = null;
		    System.out.println("Enter the path where the index will be created: (e.g. /tmp/index or c:\\temp\\index)");

			try {
				s = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		    Indexer indexer = null;
		    try {
		      indexLocation = s;
		      indexer = new Indexer(s);
		      
		      indexer.indexFileOrDirectory(s);
		    } catch (Exception ex) {
		      System.out.println("Cannot create index..." + ex.getMessage());
		      System.exit(-1);
		    }
	}

}
