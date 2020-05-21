package ie.gmit.sw;

import java.io.File;
import java.util.Scanner;

import org.encog.engine.network.activation.ActivationReLU;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.buffer.MemoryDataLoader;
import org.encog.ml.data.buffer.codec.CSVDataCODEC;
import org.encog.ml.data.buffer.codec.DataSetCODEC;
import org.encog.ml.data.folded.FoldedDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.cross.CrossValidationKFold;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.csv.CSVFormat;

public class NeuralNetwork {

	/*
	 * *****************************************************************************
	 * ******** NB: READ THE FOLLOWING CAREFULLY AFTER COMPLETING THE TWO LABS ON
	 * ENCOG AND REVIEWING THE LECTURES ON BACKPROPAGATION AND MULTI-LAYER NEURAL
	 * NETWORKS! YOUR SHOULD ALSO RESTRUCTURE THIS CLASS AS IT IS ONLY INTENDED TO
	 * DEMO THE ESSENTIALS TO YOU.
	 * *****************************************************************************
	 * ********
	 * 
	 * The following demonstrates how to configure an Encog Neural Network and train
	 * it using backpropagation from data read from a CSV file. The CSV file should
	 * be structured like a 2D array of doubles with input + output number of
	 * columns. Assuming that the NN has two input neurons and two output neurons,
	 * then the CSV file should be structured like the following:
	 *
	 * -0.385,-0.231,0.0,1.0 -0.538,-0.538,1.0,0.0 -0.63,-0.259,1.0,0.0
	 * -0.091,-0.636,0.0,1.0
	 * 
	 * The each row consists of four columns. The first two columns will map to the
	 * input neurons and the last two columns to the output neurons. In the above
	 * example, rows 1 an 4 train the network with features to identify a category
	 * 2. Rows 2 and 3 contain features relating to category 1.
	 * 
	 * You can normalize the data using the Utils class either before or after
	 * writing to or reading from the CSV file.
	 */
	/*
	 * This code is based on a video provided by Dr John Healy.
	 */
	// Since there are 235 languages make inputs and outputs equal
	static int inputs = 235;
	static int outputs = 235;
	static int epochs;
	private static Language[] lang = Language.values();

	public NeuralNetwork() {
		// Configure the neural network topology.
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(new ActivationSoftMax(), true, inputs)); // You need to figure out activation
																					// function
		// network.addLayer(....); //You need to figure out the number of hidden layers
		// and their neurons
		// network.addLayer(....);
		// for some reason layers are not having an affect, issue with reading file?
		network.addLayer(new BasicLayer(new ActivationReLU(), true, 60));
		network.addLayer(new BasicLayer(new ActivationReLU(), true, 30));
		network.addLayer(new BasicLayer(new ActivationSoftMax(), true, outputs));
		network.getStructure().finalizeStructure();
		network.reset();
		System.out.println("REPORTING NETWORK TOPOLOGY: ");
		for (int i = 0; i < network.getLayerCount(); i++) {
			System.out.println("Activation function for layer " + i + ": " + network.getActivation(i).getLabel());
			System.out.println("layer " + i + " has: " + network.getLayerNeuronCount(i) + " neurons");
		}
		// Read the CSV file "data.csv" into memory. Encog expects your CSV file to have
		// input + output number of columns.
		DataSetCODEC dsc = new CSVDataCODEC(new File("data.csv"), CSVFormat.ENGLISH, true, inputs, outputs, false);
		MemoryDataLoader mdl = new MemoryDataLoader(dsc);
		MLDataSet trainingSet = mdl.external2Memory();

		// Use backpropagation training with alpha=0.1 and momentum=0.2
		Backpropagation trainer = new Backpropagation(network, trainingSet, 0.01, 0.4);
		FoldedDataSet folded = new FoldedDataSet(trainingSet);
		System.out.println(trainer.getTraining());
		// may use backpropagation instead
		MLTrain train = new ResilientPropagation(network, folded);
		CrossValidationKFold cv = new CrossValidationKFold(train, 5);
		// Train the neural network
		// get current time for total time trained - taken from labs
		long start = System.currentTimeMillis();
		int counter = 0;
		double tp, tn, fn;
		tp = tn = fn = 0;
		double correct, error;
		error = correct = 0;
		//train for epochs
		System.out.println("Training.....");
		do {
			cv.iteration();
			counter++;
		} while (counter < epochs);
		// taken from labs & refactored
		// compare y to yd to see if true positive or others
		counter = 0;
		for (MLDataPair pair : trainingSet) {
			MLData output = network.compute(pair.getInput());
			// compare actual to ideal
			int y = (int) Math.round(output.getData(0));
			int yd = (int) pair.getIdeal().getData(0);
			// pair.getInput().getData(0) == pair.getInput().getData(1)
			if (y == yd) {
				correct++;
				System.out.println(
						"TEST " + (int) Math.round(output.getData(0)) + " " + (int) pair.getIdeal().getData(0));
				if (pair.getInput().getData(0) == pair.getInput().getData(1))
					tp++;
			} else {
				error++;
				if (pair.getInput().getData(0) == pair.getInput().getData(1))
					fn++;
				else
					tn++;
			}

			System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1) + ", Y="
					+ (int) Math.round(output.getData(0)) + ", Yd=" + (int) pair.getIdeal().getData(0));
			counter++;
		}
		System.out.println("TRUE POSTIVES: " + tp + " FALSE NEGATIVES " + fn + " TRUE NEGATIVES " + tn);
		System.out.println("\nTOTAL CORRECT: " + correct);
		System.out.println("TRAINING SIZE: " + trainingSet.size());
		System.out.println("TOTAL " + (error + correct));
		System.out.println("TOTAL WRONG " + error);
		// while(cv.getError() > 0.01);
		long end = System.currentTimeMillis();
		//print out stats here:
		System.out.println("Percentage Correct: " + (correct / (correct + error)) * 100);
		System.out.println("\nNetwork trained in: " + epochs + " epochs\n" + " Trained in : " + (end - start) / epochs
				+ " seconds\nOr approx:" + Math.round(((end - start) / epochs) / 60.00) + "minutes"
				+ "\nTotal wrong " + (error / (correct + error)) * 100 +" Total error: " +  (cv.getError() / epochs)+ "\nTEST: " + (100 - cv.getError()) + "%");
		Utilities.saveNeuralNetwork(network, "./neuralnetwork.nn");

	}

	public static void process(String file) throws Exception {
		BasicNetwork nn = null;
		try {
			nn = Utilities.loadNeuralNetwork("neuralnetwork.nn");
		} catch (Exception e) {
			System.out.println(
					"no file could be found for neural network, please try to train neural network before running this command\nerror: "
							+ e.toString());
			Runner r = new Runner();
		}
		Scanner s = new Scanner(System.in);
		// read in file break into ngrams and hash maybe?
		try {
		System.out.print("Enter the number of ngrams for file, try 1 - 5, recommend 5: ");
		int ngrams = s.nextInt();
		System.out.print("Enter Vector size, try 235: ");
		VectorProcessor.vectorSize = s.nextInt();
		System.out.println("FILE NAME: " + file);
		System.out.println("ANALYZING FILE.............");

		VectorProcessor vp = new VectorProcessor();
		double[] testData = vp.testData(file, ngrams);
		
		MLData data = new BasicMLData(testData);
		System.out.println("COMPUTER " + nn.compute(data).size());

		System.out.println("CLASSIFICATION - Language index: " + nn.classify(data) + " file is written in: "
				+ lang[nn.classify(data)]);
		}
		catch(Exception e) {
			System.out.println("NO FILE FOUND!");
			Runner r = new Runner();
		}

	}
}