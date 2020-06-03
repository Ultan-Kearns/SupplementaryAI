package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;

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
				process(line, ngrams, writer);
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
	public void process(String line, int ngrams, BufferedWriter bw) throws Exception {
		String[] record = line.split("@");
		if(counter == 235)
			return;
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
 	
		// issue here? issue with last line of file maybe it's > 235 - ngrams from
		// length maybe?
		for (int i = 0; i < vector.length * ngrams; i += ngrams) {
 			try {
				int hashcode = text.substring(i, ngrams + i).hashCode();
				int index = hashcode % vector.length;
				// think this maybe wrong
				vector[Math.abs(index)] = index + 1;
				// write out line to file
				Utilities.normalize(vector, 0, 1);
				bw.append(df.format(vector[Math.abs(index)]));
				bw.append(",");
			} catch (Exception e) {
				//in case text < vector.length
				bw.append('0');
				bw.append(',');
			}

		}
 
		// use a counter to determine language of file so you can label it - issue may
		// be with counter
		if (counter < 235) {
			// write out language label - this will fill in 235 values - don't know if it's
			// working
			for (int j = 0; j < lang.length; j++) {
 				if (lang[counter].equals(lang[j])) {
					bw.append('1');
				} else {
					bw.append('0');
				}
				if (j == lang.length - 1) {
					bw.append("\n");
					break;
				}
				bw.append(',');
			}
			counter++;
		}
	}

	public double[] testData(String input, int ngrams) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(input));
		String line = null;
		// can't use this method
		while ((line = br.readLine()) != null) {
			processTestFile(line, ngrams);
		}
		br.close();
		return vector;
	}

	public void processTestFile(String line, int ngrams) {
		String text = line.toLowerCase();
		// break line into ngrams
		// set vector
		for (int i = 0; i < NeuralNetwork.inputs; i++) {
			vector[i] = 0;
		}

		for (int i = 0; i < vector.length * ngrams; i += ngrams) {
 			try {
				int hashcode = text.substring(i, ngrams + i).hashCode();
				int index = hashcode % vector.length;
				// think this maybe wrong
				vector[Math.abs(index)] = index + 1;
				// write out line to file
				Utilities.normalize(vector, 0, 1);
			} catch (Exception e) {
				
			}

		}
	}
}