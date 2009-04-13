package acceptance;

import java.io.*;
import java.util.zip.Adler32;

public final class CheckSum {

  public static long adler32(String file) throws IOException {

      Adler32 adler = new Adler32();
      FileInputStream fis = new FileInputStream(file);  
 
	  byte[] buffer = new byte[1024];

	  while(fis.read(buffer)>= 0){
         adler.update(buffer);
      }
	  fis.close();
	  
	  return adler.getValue();
   }
}