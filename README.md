# Supplementary AI Project - Ultan Kearns
## Produced as part of the GMIT 4th Year Module: Artificial Intelligence 
### Under the supervision of Dr. John Healy

**IMPORTANT NOTE - ALL REFERENCES WILL BE INCLUDED AT THE TOP OF EACH SECTION TO AVOID REFERENCING MULTIPLE TIMES, A COMPLETE LIST OF REFERENCES IS AVAILABLE FROM THE END OF THIS DOCUMENT WHEN THE MARKDOWN SOURCE IS VIEWED, THANK YOU**

## Introduction to Project
This project was developed in lieu of the final year exam, which was cancelled due to the COVID-19 pandemic, for the module Artificial intelligence.  The objective of this project is to create A Language Detection Neural Network with Vector Hashing.  I will document all pertinent information in this readme.
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

I used a bufferedwriter to write each line to the file and the hashcode was computed by taking each ngram out of the string.  Once this loop ends the entire text will be parsed into a number of ngrams which will be determined by the user via the UI, I suggest you use 5 or 6 ngrams for this as the odds of two 5 or 6 grams occuring are 0.26 ^ 5 or 0.26 ^ 6 respectively, for those not good at maths like myself this is a very low number.  The odds that an ngram of these sizes occuring more than once is less than 0.01%, that being said you do not want to make the ngram size too  large as there is a limited amount of text in the data set.  I also ensured that the CSV file was formatted in a way such that the total number of columns would equal the number of input nodes for the neural network times the number of output nodes, so if we had 2 input and 2 output nodes the total number of columns would be 4.  This data.csv file was then passed into the neural network and the neural network used it as training data.

## Neural Network Topology
### Activation Functions
For the input and output layers I used a sigmoidal activation function and for the hidden layer I used TANH, I will explain why I used each of these functions below.

#### Sigmoidal Function
** REFERENCE NOTE ** Rather than reference the same source multiple times I will reference it once here, all information for this chapter was gotten from the sigmoidal wiki page here: [sigmoid][1]
<br/>
<br/>
The Sigmoidal function has the characteristic of having an S shape curve and can be determined by the following equation:
<center>
![Image of equation](https://wikimedia.org/api/rest_v1/media/math/render/svg/9537e778e229470d85a68ee0b099c08298a1a3f6 "Wikipedia image of sigmoidal equation")
<br/>
Image courtesy of wikipedia
<br/>
<br/>
</center>
The above equation is used to calculate the overall structure of the curve.
<center>
![Image of sigmoidal function](https://upload.wikimedia.org/wikipedia/commons/8/88/Logistic-curve.svg "Wikipedia image of sigmoidal function")
<br/>
Image courtesy of wikipedia
</center>
<br/>
As you can see from the above image the sigmoidal function gradually increases, this can be useful for training a neural network as the accuracy or similarity of data can be trained using a curve so for example, say we had a string of Chinese characters and we were looking to find out which language it was, Japanese would score fairly highly as a lot of Japanese characters are taken from the Chinese alphabet and Chinese would score highly also it is in this way by using the similarity of the language to the training data that the neural network can decipher the language.

#### TANH Function

### Number of Neurons & Explanation of topology
For the input and output layers I decided to use 235 neurons as there are 234 languages and the array starts at 0 so in reality there are 235 pieces of data in the training array.  For the hidden layer I used 20 neurons as it seemed a good number due to the fact it is 1/10th of the entire trainingset and would give each neuron a decent amount of data to learn from.
<br/>
<center>
<img src="https://cdn-images-1.medium.com/max/1200/1*3fA77_mLNiJTSgZFhYnU0Q@2x.png" height="500px" width="500px"/>
<br/>
<br/>
Image courtesy of Medium
<br/>
<br/>
A typical neural network, if you imagine 10 input nodes, 20 hidden nodes and 10 output nodes, then you'll have a decent image of the neural network for this project
</center>
<br/>
The overall topology of the neural network would look like such: imagine a layer of 235 nodes which are not connected to each other, now imagine another layer of nodes which numbers 20 in total and now imagine that the 235 layers of nodes are connected to each of the 20 nodes and each of the 20 nodes are connected to a final layer of 235 nodes.  It is in this way that the neural network functions.  Each node in the hidden layer, which is the layer of 20 nodes, is sent data from the initial 235 nodes, once the hidden layer neurons are sent this data they are trained to recognize patterns in the data through activation functions such as sigmoidal and tanh.  Once the training is complete, the timing of which depends on the amount of training data, number of neurons, epochs etc..., then the final data is sent to the end nodes which will determine the language of the test data passed into the neural network.

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
When training the neural network with the following data: ngrams = 5, vector size = 235, epochs = 1000 - I was able to achieve an error rate of 0.8553195590121987 and the total time it was trained in : 0.043 seconds.  Overall the training of the network is very fast with a very low incidence of error.

## Extras
+ Allowed user to enter in the vector size, ngram size & number of epochs
+ Allowed user to enter in the directory for the training data
+ reported network topology to user via the CLI
+ Gave hints to user on which size to use for vector, ngrams + epochs

 
[1]: https://en.wikipedia.org/wiki/Sigmoid_function
