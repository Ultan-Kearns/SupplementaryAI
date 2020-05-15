package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.List;

/*
 * TODO
 * 1. write out classification for each row,
 * 2. Test neural network
 */
public class VectorProcessor {
	static int vectorSize;
	private double[] vector = new double[vectorSize];
	private DecimalFormat df = new DecimalFormat("###.###");
	private Language[] lang = Language.values();
	int counter = 0;
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
			writer = new BufferedWriter(new FileWriter(output, false));
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

	// should have 235 + 235 coluns 470 total only getting 358
	public void process(String line, int ngrams) throws Exception {
		String[] record = line.split("@");
		if (record.length > 2)
			return; // get rid of bad lines

		String text = record[0].toLowerCase();
		String language = record[1];
		// break line into ngrams
		// set vector
		for (int i = 0; i < NeuralNetwork.inputs; i++) {
			vector[i] = 0;
		}
		// issue with NGRAMS, since the number is smaller not sure about hashcode
		// honestly very hard to figure this thing out
		// should only be 235 in here
		if(text.length() < 235) {
			StringBuffer s = new StringBuffer(text.length());
			for(int c = text.length(); c < NeuralNetwork.inputs; c++) {
				s.append("0");
			}
			text += s.toString();
			System.out.println("TTTTTTTTTT" + text.length());
		}
		//issue here? issue with last line of file maybe it's > 235
		for (int i = ngrams; i < vector.length - ngrams; i += ngrams) {
			System.out.println(i);
	 
			int hashcode = text.substring(i, ngrams + i).hashCode();
			int index = hashcode % vector.length;
			// think this maybe wrong
			vector[Math.abs(index)] = index + 1;
			// write out line to file
			Utilities.normalize(vector, -1, 1);
			writer.append(df.format(vector[Math.abs(index)]));
			writer.append(",");
			System.out.println("TEST of vector : " + i);
			System.out.println("INDEX: " + index);
 

		}
		if(vector.length >= NeuralNetwork.inputs) {
			System.out.println("VECTOR LENGTH " + vector.length);
		}
		// use a counter to determine language of file so you can label it
		if (!language.equalsIgnoreCase(lang[counter].toString()) && counter < 234) {
			counter++;
			// write out language label - this will fill in 235 values - don't know if it's
			// working
			for (int j = 0; j <= lang.length - 1; j++) {
				System.out.println("LANGUAGE " + j);
				if (lang[counter - 1].equals(lang[j])) {
					writer.append('1');
				} else {
					writer.append('0');
				}
				if (j == lang.length - 1) {
					writer.append("\n");
					break;
				}
				writer.append(',');
			}
		}
	}

	// each row should have vector length + #labels --- labels = number of elements
	// in each row
	// size of vector + 235
}
