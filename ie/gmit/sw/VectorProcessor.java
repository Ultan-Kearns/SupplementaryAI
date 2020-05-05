package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

public class VectorProcessor {
 private double[] vector = new double[100];
 private DecimalFormat df = new DecimalFormat("###.###");
 private int n = 4;
 private Language[] langs;
 
 public void go() throws Exception{
	 try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("./"))))){
		 String line = null;
		 while((br.readLine()) != null) {
			 process(line);
		 }
	 }catch(Exception e) {
		 
	 }
 }
public void process(String line) throws Exception{
	String [] record = line.split("@");
	if(record.length > 2) return; // get rid of bad lines
	String text = record[0].toLowerCase();
	String language = record[1];
	//set vector
	for(int i = 0; i < vector.length; i++) {
		vector[i] = 0;
		/*
		 * Loop over text
		 * for each n gram
		 * compute the index as an n gram.hashcode % vector.length
		 * vector[i] = value of current + 1 
		 */
		Utilities.normalize(vector, -1, 1);
		//write out line to file
	}
}
}
