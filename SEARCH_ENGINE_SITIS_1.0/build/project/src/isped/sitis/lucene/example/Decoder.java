package isped.sitis.lucene.example;

import org.apache.lucene.document.Field;


public interface Decoder {
  
  public String decodeTerm(String fieldName, Object value) throws Exception;
  public String decodeStored(String fieldName, Field value) throws Exception;
}
