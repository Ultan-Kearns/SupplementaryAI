package ie.gmit.sw;

import java.io.File;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
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
	 * *************************************************************************************
	 * NB: READ THE FOLLOWING CAREFULLY AFTER COMPLETING THE TWO LABS ON ENCOG AND REVIEWING
	 * THE LECTURES ON BACKPROPAGATION AND MULTI-LAYER NEURAL NETWORKS! YOUR SHOULD ALSO 
	 * RESTRUCTURE THIS CLASS AS IT IS ONLY INTENDED TO DEMO THE ESSENTIALS TO YOU. 
	 * *************************************************************************************
	 * 
	 * The following demonstrates how to configure an Encog Neural Network and train
	 * it using backpropagation from data read from a CSV file. The CSV file should
	 * be structured like a 2D array of doubles with input + output number of columns.
	 * Assuming that the NN has two input neurons and two output neurons, then the CSV file
	 * should be structured like the following:
	 *
	 *			-0.385,-0.231,0.0,1.0
	 *			-0.538,-0.538,1.0,0.0
	 *			-0.63,-0.259,1.0,0.0
	 *			-0.091,-0.636,0.0,1.0
	 * 
	 * The each row consists of four columns. The first two columns will map to the input
	 * neurons and the last two columns to the output neurons. In the above example, rows 
	 * 1 an 4 train the network with features to identify a category 2. Rows 2 and 3 contain
	 * features relating to category 1.
	 * 
	 * You can normalize the data using the Utils class either before or after writing to 
	 * or reading from the CSV file. 
	 */
	/*
	 * This code is based on a video provided by Dr John Healy.
	 */
	 //Since there are 235 languages make inputs and outputs equal
	static int inputs = 235;
	static int outputs = 235; 
	static int epochs = 10;
	public NeuralNetwork() {
		//Configure the neural network topology. 
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, inputs)); //You need to figure out the activation function
		//network.addLayer(....); //You need to figure out the number of hidden layers and their neurons
		//network.addLayer(....);
		//for some reason layers are not having an affect, issue with reading file?
		network.addLayer(new BasicLayer(new ActivationTANH(),true,40));
		network.addLayer(new BasicLayer(new ActivationTANH(),true,40));
		network.addLayer(new BasicLayer(new ActivationTANH(),true,20));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, outputs));
		network.getStructure().finalizeStructure();
		network.reset();
		System.out.println("\nThis neural network consists of " + inputs + " input nodes and " + outputs + " output node, \nthree layers of neurons 2 sigmoidal for input and output"
				+ " \nand a tanh for the hidden layer which comprises of " + 40 +  " neurons");
		//Read the CSV file "data.csv" into memory. Encog expects your CSV file to have input + output number of columns.
		DataSetCODEC dsc = new CSVDataCODEC(new File("data.csv"), CSVFormat.ENGLISH, false, inputs, outputs, false);
		MemoryDataLoader mdl = new MemoryDataLoader(dsc);
		MLDataSet trainingSet = mdl.external2Memory();
		
		//Use backpropagation training with alpha=0.9 and momentum=0.5
		Backpropagation trainer = new Backpropagation(network, trainingSet, 0.9, 0.5);
		FoldedDataSet folded = new FoldedDataSet(trainingSet);
		System.out.println(trainer.getTraining());
		//may use backpropagation instead
		MLTrain train = new ResilientPropagation(network, folded);
		CrossValidationKFold cv = new CrossValidationKFold(train, 5);
		//Train the neural network
		//get current time for total time trained - taken from labs
		long start = System.currentTimeMillis();	 
		int counter = 0;
		//true positives, true negatives and false negatives
		double tp,tn,fn;
		tp = tn = fn = 0;
		double correct,error;
		error = correct = 0;
		do { 
			//need to test sensitivity and specifity across all 5 validations
			cv.iteration(); 
			
			counter++;
		}while(counter < epochs); 
		//taken from labs & refactored
		//compare y to yd to see if true positive or others
		for (MLDataPair pair : trainingSet) {
 			MLData output = network.compute(pair.getInput());
			//compare actual to ideal
			int y = (int) Math.round(output.getData(0));
			int yd = (int) pair.getIdeal().getData(0);
			if(y == yd) {
				correct++;
				if((int) Math.round(output.getData(0)) ==  (int) pair.getIdeal().getData(0))
					tp++;
			}
			else {
				error++;
				if((int) Math.round(output.getData(0)) ==  (int) pair.getIdeal().getData(0))
					fn++;
				else 
					tn++;
				
			}
			System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1) + ", Y="
					+ (int) Math.round(output.getData(0)) + ", Yd=" + (int) pair.getIdeal().getData(0));
		}
		System.out.println("TRUE POSTIVES: " + tp + " FALSE NEGATIVES " + fn + " TRUE NEGATIVES " + tn);
		System.out.println("\nTOTAL CORRECT: " + correct);
		System.out.println("TRAINING SIZE: " + trainingSet.size());
		System.out.println("TOTAL " + (error + correct));
		System.out.println("TOTAL WRONG " + error);
		//while(cv.getError() > 0.01);	
		long end = System.currentTimeMillis();
		System.out.println("TOTAL ACC: " + (correct / (correct + error)) * 100);
		System.out.println("\nNetwork trained in: " + epochs + " epochs\n"
				+ " Trained in : " + (end - start) /1000.00 +" seconds\nOr approx:"+ Math.round(((end - start) /1000.00) / 60.00) +"minutes"
						+ "\nWith a total error rate of " + (error / (correct + error)) * 100
						+ "\nWith a total acc: " + (correct / (correct + error)) * 100 + " TEST " + cv.getError());
		Utilities.saveNeuralNetwork(network, "./neuralnetwork.nn");
		
	}
	 
}