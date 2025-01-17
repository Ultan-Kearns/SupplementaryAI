package ie.gmit.sw;

import java.util.Scanner;

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
		int response;
		while (true) {
			System.out.println(
					"1. Show Languages\n2. Train\n3. To classify a file\n4. To report topology of trained Neural Network\n5. To exit\n");
			System.out.print("Option: ");
			response = s.nextInt();
			if (response == 1) {
				for (int i = 0; i < langs.length; i++) {
					System.out.println(i + "-->" + langs[i]);
				}
			} else if (response == 2) {
				try {
					System.out.print("\nPlease enter the directory of the traiining data file: ");
					String fileDir = s.next();
					System.out.print("\nTry 1 - 5\nPlease enter the Number of ngrams: ");
					int ngrams = s.nextInt();
					System.out.print("\nTry 235 - 500, must be > 235\nPlease enter the vector size: ");
					VectorProcessor.vectorSize = s.nextInt();
				System.out.print("\nTry 50\nPlease enter the number of epochs: ");
				NeuralNetwork.epochs = s.nextInt();
				System.out.println("Neural network will be trained with File: " + fileDir
						+ "  and number of ngrams will equal: " + ngrams);
				VectorProcessor vp = new VectorProcessor();
				// set up CSV file
				vp.go(fileDir, ngrams);
				// trains network
				NeuralNetwork nn = new NeuralNetwork();
				}
				catch(Exception e) {
					System.out.println(e.toString());
					Runner r = new Runner();
				}
			}
			if (response == 3) {
				System.out.print("Enter File Directory: ");
				String file = s.next();
 				NeuralNetwork.process(file);
 				s.nextLine();

			} else if (response == 4) {
				// loads in the neural network and reports topology or displays error msg if
				// file doesn't exist...
				try {
					BasicNetwork nn = Utilities.loadNeuralNetwork("neuralnetwork.nn");
					System.out.println("Number of Layers: " + nn.getLayerCount());
					for (int i = 0; i < nn.getLayerCount(); i++) {
						System.out
								.println("Activation function for layer " + i + ": " + nn.getActivation(i).getLabel());
						System.out.println("layer " + i + " has: " + nn.getLayerNeuronCount(i) + " neurons");
					}
					System.out.println("\n");
				} catch (Exception e) {
					Runner r = new Runner();

					System.out.println(
							"Train network before running this command. Also trained network must named as neuralnetwork and have file extension .nn");
				}
			} else if (response == 5) {
				s.close();
				break;
			} else {
				System.out.println("\nInvalid Command");
			}
		}
		System.out.println("\nHasta Luego!\n");
	}
}