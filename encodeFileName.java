package quickstart;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URLEncoder;

public class encodeFileName {

	public static void main(String[] args) {
		
		File docid = new File("C:/Users/minaxi92/workspace/quickstart/output/downloads/result/docid.csv");
		
		try{
			  // Open the file that is the first 
			  // command line parameter
			  FileInputStream fstream = new FileInputStream("C:/Users/12135/workspace/quickstart/output/downloads/result/files.txt");
			  // Get the object of DataInputStream
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine;
			  FileWriter id = new FileWriter(docid, true);
			  //Read File Line By Line
			  while ((strLine = br.readLine()) != null)   {
			  // Print the content on the console
				  String hashedName = URLEncoder.encode(strLine, "UTF-8");
				    id.append(strLine);
				    id.append(',');
				    id.append(hashedName);
				    id.append('\n');
			  }
			  //Close the input stream
			  in.close();
			  id.flush();
			    id.close();
			    }catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
			  }

	}

}