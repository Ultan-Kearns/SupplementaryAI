package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import jdk.nashorn.internal.runtime.regexp.joni.Regex;

public class VectorProcessor {
 private double[] vector = new double[100];
 private DecimalFormat df = new DecimalFormat("###.###");
 private Language[] langs;
 
 public void go(String fileDir,int ngrams) throws Exception{
	 File file = new File(fileDir);
 
	 try {
		 BufferedReader br = new BufferedReader(new FileReader(file));
		 String line = null;
		 while((line = br.readLine()) != null) {
			process(line,ngrams); 
		 }
		 br.close();
	 }
	 catch(Exception e) {
		 System.out.println(fileDir.toString());
		 System.out.println("\nFailed to open file, does it actually exist?");
		 System.out.println(e.toString());
		 //bail out and return -1 indicating an error has occured
		 System.exit(-1);
	 }
 }
public void process(String line,int ngrams) throws Exception{
	String [] record = line.split("@");
	if(record.length > 2) return; // get rid of bad lines
	String text = record[0].toLowerCase();
	String language = record[1];
	//break line into ngrams
	StringBuffer ngramString = new StringBuffer(text);
	for(int i = ngrams; i < text.length(); i+= ngrams) {
		ngramString.insert(i, " ");
		i++;
	}
	File file = new File("data.csv");
	FileWriter fw = new FileWriter(file);
	file.createNewFile();
	//set vector
	for(int i = 0; i < vector.length; i++) {
		vector[i] = 0;
		/*
		 * Loop over text
		 * for each n gram
		 * compute the index as an n gram.hashcode % vector.length
		 * vector[i] = value of current + 1 
		 */
	}
	for(int i = 0; i < 100; i++) {
		int index = line.hashCode() % vector.length; 
		vector[i] = index;
		Utilities.normalize(vector, -1, 1);
		//write out line to file
		fw.write(index);
	}
	fw.close();
}
}
