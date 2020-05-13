package ie.gmit.sw;

import java.util.Scanner;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.neural.networks.BasicNetwork;

public class Runner {
	public static void main(String[] args) throws Exception {
		Language[] langs = Language.values(); // Only call this once...

		/*
		 * Each of the languages in the enum Language can be represented as a number
		 * between 0 and 234. You can map the output of the neural network and the
		 * training data label to / from the language using the following. Eg. index 0
		 * maps to Achinese, i.e. langs[0].
		 */
		Scanner s = new Scanner(System.in);
		while (true) {
			System.out.println(
					"1. Show Languages\n2. Train\n3. To classify a file\n4. To report topology of trained Neural Network\n5. To exit\n");
			System.out.print("Option: ");
			int response = s.nextInt();
			if (response == 1) {
				for (int i = 0; i < langs.length; i++) {
					System.out.println(i + "-->" + langs[i]);
				}
			} else if (response == 2) {
				System.out.print("\nPlease enter the directory of the file: ");
				String fileDir = s.next();
				System.out.print("\nTry 10\nPlease enter the Number of ngrams: ");
				int ngrams = s.nextInt();
				System.out.print("\nTry 500\nPlease enter the vector size: ");
				VectorProcessor.vectorSize = s.nextInt();
				System.out.print("\nTry 1000\nPlease enter the number of epochs: ");
				NeuralNetwork.epochs = s.nextInt();
				System.out.println("Neural network will be trained with File: " + fileDir
						+ "  and number of ngrams will equal: " + ngrams);
				VectorProcessor vp = new VectorProcessor();
				// set up CSV file
				vp.go(fileDir, ngrams);
				// trains network
				NeuralNetwork nn = new NeuralNetwork();
			}
			if (response == 3) {
				System.out.println("Enter File Directory: ");
				String file = s.nextLine();
				// read in file to string
				NeuralNetwork.process(file);

			} else if (response == 4) {
				try {
				BasicNetwork nn = Utilities.loadNeuralNetwork("neuralnetwork.nn");
				System.out.println("Number of Layers: " + nn.getLayerCount());
				for (int i = 0; i < nn.getLayerCount(); i++) {
					System.out.println("Activation function for layer " + i + ": " + nn.getActivation(i).getLabel());
					System.out.println("layer " + i + " has: " + nn.getLayerNeuronCount(i) + " neurons");
				}
				}
				catch(Exception e) {
					System.out.println("Train network before running this command. Also trained network must named as neuralnetwork and have file extension .nn");
				}
			} else if (response == 5) {
				break;
			} else {
				System.out.println("\nInvalid Command");
			}
		}
		System.out.println("\nHasta Luego!\n");
	}
}