# Supplementary AI Project - Ultan Kearns
## Produced as part of the GMIT 4th Year Module: Artificial Intelligence 
### Under the supervision of Dr. John Healy

**IMPORTANT NOTE - ALL REFERENCES WILL BE INCLUDED AT THE TOP OF EACH SECTION TO AVOID REFERENCING MULTIPLE TIMES, A COMPLETE LIST OF REFERENCES IS AVAILABLE FROM THE END OF THIS DOCUMENT WHEN THE MARKDOWN SOURCE IS VIEWED, THANK YOU**

## Introduction to Project
This project was developed in lieu of the final year exam, which was cancelled due to the COVID-19 pandemic, for the module Artificial intelligence.  The objective of this project is to create A Language Detection Neural Network with Vector Hashing.  I will document all pertinent information in this readme.  This final goal of this project is to create and train a neural network using a test data provided from the WILI dataset and to use it to detect languages.
## Vector size	
The size of the hashing vector is determined by the user via the UI and it is suggested that this size would be 235 as their are 234 languages in the training set.
## Number of Epochs
The number of epochs is again determined by the user via the UI, it is suggested that the user use 1000 epochs to train the data as this is a good number to ensure that each neuron in the hidden layer is trained with a decent amount of the training data.
## N-grams parsing
I parsed and hashed each ngram into the fixed size vector like so:

		//if ngrams = 4 should only have 4 nums of columns , think need to find way to bail out if it can't be formatted in 4 * 4s
		int counter = 0;
		for (int i = ngrams; i < text.length() - ngrams; i += ngrams) {
			if(counter > NeuralNetwork.inputs * NeuralNetwork.outputs) {
				break;
			}
			if(counter == NeuralNetwork.inputs * NeuralNetwork.outputs) {
				writer.append('\n');
				counter = 0;
			}
			int hashcode = text.substring(i, ngrams + i).hashCode();
			int index = hashcode % vector.length;
			//think this maybe wrong
			vector[Math.abs(index)] = vector[Math.abs(index)] + 1;		
			// write out line to file
			Utilities.normalize(vector, -1, 1);
			writer.append(df.format(vector[Math.abs(index)]));
			counter++;
			writer.append(',');
		}
	}

<br/>

I used a bufferedwriter to write each line to the file and the hashcode was computed by taking each ngram out of the string.  Once this loop ends the entire text will be parsed into a number of ngrams which will be determined by the user via the UI, I suggest you use 5 or 10 ngrams for this as the odds of two 5 or 10 ngrams occuring are 0.38 ^ 5 or 0.38 ^ 10 respectively, for those not good at maths like myself this is a very low number.  The odds that an ngram of these sizes occuring more than once is less than 0.01%, that being said you do not want to make the ngram size too  large as there is a limited amount of text in the data set.  I also ensured that the CSV file was formatted in a way such that the total number of columns would equal the number of input nodes for the neural network times the number of output nodes, so if we had 2 input and 2 output nodes the total number of columns would be 4.  This data.csv file was then passed into the neural network and the neural network used it as training data.

## Neural Network Topology
### Activation Functions
For the input and output layers I used a sigmoidal activation function and for the hidden layers I used TANH, I will explain why I used each of these functions below.

#### Sigmoidal Function
** REFERENCE NOTE ** Rather than reference the same source multiple times I will reference it once here, all information for this chapter was gotten from the sigmoidal wiki page here: [sigmoid][1]
<br/>
<br/>

The sigmoidal function is a mathematical function which for this project was used to determine 

The sigmoidal function gradually increases when provided positive results and has an S shaped curve, this can be useful for training a neural network as the accuracy or similarity of data can be trained using a curve so for example, say we had a string of Chinese characters and we were looking to find out which language it was, Japanese would score fairly highly as a lot of Japanese characters are taken from the Chinese alphabet and Chinese would score highly also it is in this way by using the similarity of the language to the training data that the neural network can decipher the language.

My reasoning for using the sigmoidal function for this neural network is that it works very will in classifying information.

#### TANH Function

### Number of Neurons & Explanation of topology
For the input and output layers I decided to use 235 neurons as there are 234 languages and the array starts at 0 so in reality there are 235 pieces of data in the training array.  For the hidden layer I used 3 layers of TANH neurons the first two layers have 40 and the last layer has 20, I experimented a lot with different levels of neurons and found that this works the best.
 
The overall topology of the neural network would look like such: imagine a layer of 235 nodes which are not connected to each other, now imagine another two layers of nodes which numbers 40 for each layer and another layer of 20 and now imagine that the 235 layers of nodes are connected to the first layer and all the neurons in the second layer are connected to the third layer and all the neurons in the third layer are connected to a final layer of 235.  

The reason I chose two layers of 40 neurons and a final layer of 20 is that 40 seems like a decent number of neurons for a fairly large data set and the reason why I chose to have a final layer of 20 neurons is to limit the error rate as the first two layers will have learned much from the input data set.  The overall result was fairly good and the neural network is trained in approximately 30 seconds depending on the data provided by the user(number of epochs, vector size, etc...)

### Statistics for Neural Network

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

### Error Rate
When training the neural network with the following data: ngrams = 5, vector size = 500, epochs = 1000 - I was able to achieve an error rate of 0.00 and the total time it was trained in : 0.043 seconds.  Overall the training of the network is very fast(about 30 seconds using suggested data) with a very low incidence of error and high accuracy.

## Extras
+ Allowed user to enter in the vector size, ngram size & number of epochs
+ Allowed user to enter in the directory for the training data
+ reported network topology to user via the CLI
+ Gave hints to user on which size to use for vector, ngrams + epochs

 
[1]: https://en.wikipedia.org/wiki/Sigmoid_function
