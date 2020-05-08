package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;

public class VectorProcessor {
	private double[] vector = new double[100];
	private DecimalFormat df = new DecimalFormat("###.###");
	private Language[] langs;
	File output = new File("./data.csv");
	BufferedWriter writer;

	public void go(String fileDir, int ngrams) throws Exception {
		File input = new File(fileDir);

		try {

			System.out.println("CREATING DATA.......");
			output.createNewFile();
			BufferedReader br = new BufferedReader(new FileReader(input));
			String line = null;
			// everytime process is called new file is created
			writer = new BufferedWriter(new FileWriter(output,false));
			while ((line = br.readLine()) != null) {
				process(line, ngrams);
			}
			br.close();

			System.out.println("DATA FILE CREATED!");
			writer.close();
		} catch (Exception e) {
			System.out.println(fileDir.toString());
			System.out.println("\nFailed to open file, does it actually exist?");
			System.out.println(e.toString());
			// bail out and return -1 indicating an error has occured
			System.exit(-1);
		}
	}

	public void process(String line, int ngrams) throws Exception {
		String[] record = line.split("@");
		if (record.length > 2)
			return; // get rid of bad lines
		String text = record[0].toLowerCase();
		String language = record[1];	
		// break line into ngrams
		// set vector
		for (int i = 0; i < vector.length; i++) {
			vector[i] = 0;
			/*
			 * Loop over text for each n gram compute the index as an n gram.hashcode %
			 * vector.length vector[i] = value of current + 1
			 */
		}
		//if ngrams = 4 should only have 4 nums of columns 
		int counter = 0;
		for (int i = ngrams; i < text.length() - ngrams; i += ngrams) {
			int hashcode = text.substring(i, ngrams + i).hashCode();
			vector[i % vector.length] = hashcode;
			// write out line to file
			Utilities.normalize(vector, -1, 1);
			writer.append(df.format(vector[i % vector.length]));
			writer.append(',');
			counter++;
			if(counter == ngrams) {
				writer.append('\n');
				counter = 0;
			}
		}

		//writer.append('\n');
	}
}
