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

	public void process(String line, int ngrams) throws Exception {
	 
		String[] record = line.split("@");
		if (record.length > 2)
			return; // get rid of bad lines

		String text = record[0].toLowerCase();
		String language = record[1];
		if(!language.equalsIgnoreCase(lang[counter].toString()) && counter < 234) { counter++; }
		// break line into ngrams
		// set vector
		for (int i = 0; i < vector.length; i++) {
			vector[i] = 0;
			/*
			 * Loop over text for each n gram compute the index as an n gram.hashcode %
			 * vector.length vector[i] = value of current + 1
			 */
		}

		// need to set language as label of training data

		for (int i = ngrams; i < text.length() - ngrams; i += ngrams) {
			// need input + output number of columns, no clue how to do this and follow it up with the language label
			int hashcode = text.substring(i, ngrams + i).hashCode();
			int index = hashcode % vector.length;
			// think this maybe wrong
			vector[Math.abs(index)] = index + 1;
			// write out line to file
			Utilities.normalize(vector, -1, 1);
			writer.append(df.format(vector[Math.abs(index)]));
			writer.append(",");
		}
		// write out language label
		for (int j = 0; j < lang.length - 1; j++) { 
			if (lang[counter].equals(lang[j])) {
				writer.append('1');
			} else {
				writer.append('0');
			}
			writer.append(',');
		}
	}
	// each row should have vector length + #labels --- labels = number of elements
	// in each row
	// size of vector + 235
}
