# Supplementary AI Project - Ultan Kearns
## Produced as part of the GMIT 4th Year Module: Artificial Intelligence 
### Under the supervision of Dr. John Healy

## Introduction to Project
This project was developed in lieu of the final year exam, which was cancelled due to the COVID-19 pandemic, for the module Artificial intelligence.  The objective of this project is to create A Language Detection Neural Network with Vector Hashing.  I will document all pertinent information in this readme.  This final goal of this project is to create and train a neural network using a test data provided from the WILI dataset and to use it to detect languages.
## Vector size	
The size of the hashing vector is determined by the user via the UI and it is suggested that this size would be 235 as their are 234 languages in the training set.
## Number of Epochs
The number of epochs is again determined by the user via the UI, it is suggested that the user use 1000 epochs to train the data as this is a good number to ensure that each neuron in the hidden layer is trained with a decent amount of the training data.
## N-grams parsing
I parsed and hashed each ngram into the fixed size vector like so:
 		public void process(String line, int ngrams, BufferedWriter bw) throws Exception {
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
		if (text.length() < 235 * ngrams) {
			StringBuffer s = new StringBuffer(text.length());
			for (int i = text.length(); i < NeuralNetwork.inputs; i++) {
				s.append("1");
			}
			text += s.toString();
			System.out.println("TTTTTTTTTT" + text.length());
		}
		// issue here? issue with last line of file maybe it's > 235 - ngrams from
		// length maybe? * Ngrams for both this and above
		for (int i = 0; i < vector.length * ngrams; i += ngrams) {
			System.out.println(i);
			try {
				int hashcode = text.substring(i, ngrams + i).hashCode();
				int index = hashcode % vector.length;
				// think this maybe wrong
				vector[Math.abs(index) + 1] = index;
				// write out line to file
				Utilities.normalize(vector, -1, 1);
				bw.append(df.format(vector[Math.abs(index)]));
				bw.append(",");
				System.out.println("TEST of vector : " + i);
				System.out.println("INDEX: " + index);
			} catch (Exception e) {
				bw.append('0');
				bw.append(',');
			}

		}
		if (vector.length >= NeuralNetwork.inputs) {
			System.out.println("VECTOR LENGTH " + vector.length);
		}
		// use a counter to determine language of file so you can label it - issue may
		// be with counter
		if (!language.equalsIgnoreCase(lang[counter].toString()) && counter < 234) {

			// write out language label - this will fill in 235 values - don't know if it's
			// working
			for (int j = 0; j <= lang.length; j++) {
				System.out.println("LANGUAGE " + j);
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
I will explain the code below:

I utilized a buffred reader to read in the file line by line then I passed the line, ngrams and the writer to the process function, from here I initialized the vector to equal all zeros. Once all this was finished I read in each ngram using the substring method on the line and hashed each ngram into the vector index and incremented said index by one.  If the text < then the vector length I append 0s to make it equal to the vector length.  I also let the user decide how many ngrams they want to parse the line into, I suggested 5 as it is very unlikely that the same 5 ngram will appear more than once, it is about (0.26 ^ 5)% likely to occur twice which is an extremely low number.  I also allow the user to choose the vector size which I suggest is kept at 235 as there are 235 languages. My reasoning for keeping the vector size at 235 simply boils down to the processing time and the fact there are 235 languages, the larger the vector the longer it will take to fill up and to process.  I ensured that the text was formatted correctly for Encog by ensuring that the csv file was formatted as follows: 235 columns and 235 rows.  I only allowed 235 ngram hashcodes for the first part of the CSV row and the rest was filled with the language label which was a 1 for the language postion in the array followed by 235 zeros for the languages which were not the current language.

When reading in the Test file I basically did the same thing as I did when reading in the test file, excluding writing out the language label or making the text equal a certain number.

## Neural Network Topology

## What is an Artificial Neural Network? 
An artificial neural network can be considered a weighted graph, that is to say if we imagine a layer of nodes all connected to another layer of N nodes each with a particular weight attached to each node then we have the basics of an artificial neural network.

### Activation Functions
For the input and output layers I used a Softmax activation function and for the hidden layers I used RELU(Rectified Linear) as I found that this combination worked best for my neural network.

### Number of Neurons & Explanation of topology & Network overall
 For the input and output layers I decided to use 235 neurons as there are 234 languages and the array starts at 0 so in reality there are 235 languages.  I then connected these to a hidden layer which had 90 neurons. Then once the topology was established I fed in the data to the neural network, to get the accuracy I compared the actual data to the neural networks ideal data, the percentage correct was 99.57446808510639% in the end and the only wrong answer comes from the dodgy line at the bottom of the CSV file.  The neural network seems to be overfitted and has a hard time determing different language files, I tried my best to get the neural network to be as accurate as possible but with the limited dataset and my limited experience with encog I found that it was incredibly hard to get it to predict information accurately.
### results from Neural Network

### Code used for NeuralNetwork
		package ie.gmit.sw;
		
		import java.io.File;
		
		import org.encog.Encog;
		import org.encog.engine.network.activation.ActivationSigmoid;
		import org.encog.engine.network.activation.ActivationTANH;
		import org.encog.ml.MLFactory;
		import org.encog.ml.data.MLDataSet;
		import org.encog.ml.data.basic.BasicMLDataSet;
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
				int hidden = 20;
				//Configure the neural network topology. 
				BasicNetwork network = new BasicNetwork();
				network.addLayer(new BasicLayer(new ActivationSigmoid(), true, inputs)); //You need to figure out the activation function
				//network.addLayer(....); //You need to figure out the number of hidden layers and their neurons
				//network.addLayer(....);
				network.addLayer(new BasicLayer(new ActivationTANH(),true,hidden));
				network.addLayer(new BasicLayer(new ActivationSigmoid(), true, outputs));
				network.getStructure().finalizeStructure();
				network.reset();
				System.out.println("\nThis neural network consists of " + inputs + " input nodes and " + outputs + " output node, \nthree layers of neurons 2 sigmoidal for input and output"
						+ " \nand a tanh for the hidden layer which comprises of " + hidden +  " neurons");
				//Read the CSV file "data.csv" into memory. Encog expects your CSV file to have input + output number of columns.
				DataSetCODEC dsc = new CSVDataCODEC(new File("data.csv"), CSVFormat.ENGLISH, false, inputs, outputs, false);
				MemoryDataLoader mdl = new MemoryDataLoader(dsc);
				MLDataSet trainingSet = mdl.external2Memory();
				
				//Use backpropagation training with alpha=0.1 and momentum=0.2
				Backpropagation trainer = new Backpropagation(network, trainingSet, 0.1, 1);
				FoldedDataSet folded = new FoldedDataSet(trainingSet);
				System.out.println(trainer.getTraining());
				//may use backpropagation instead
				MLTrain train = new Backpropagation(network, folded);
				CrossValidationKFold cv = new CrossValidationKFold(train, 5);
				//Train the neural network
				//get current time for total time trained - taken from labs
				long start = System.currentTimeMillis();
				double errorRate = 0;
				int counter = 0;
				do { 
					cv.iteration(); 
					errorRate += cv.getError(); 
					counter++;
				}while(counter < epochs); 
				//while(cv.getError() > 0.01);	
				long end = System.currentTimeMillis();
		
				System.out.println("Network trained in: " + epochs + " epochss\n"
						+ " Trained in : " + (end - start) /1000.00 +" seconds\nOr approx:"+ Math.round(((end - start) /1000.00) / 60.00) +"minutes\nWith an error rate of: " + (errorRate / epochs));
				Utilities.saveNeuralNetwork(network, "./neuralnetwork.nn");
				
			}
			 
		}
		
Once trained I output the following stats to the user: Time trained in seconds, time trained in minutes which is an approximation, and the error rate.

 
## Extras
+ Allowed user to enter in the vector size, ngram size & number of epochs
+ Allowed user to enter in the directory for the training data
+ reported network topology to user via the CLI
+ Gave hints to user on which size to use for vector, ngrams + epochs
+ Has error handling
+ Did a small CLI loading bar
 
 
