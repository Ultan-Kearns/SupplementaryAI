# Supplementary AI Project - Ultan Kearns
## Produced as part of the GMIT 4th Year Module: Artificial Intelligence 
### Under the supervision of Dr. John Healy

**IMPORTANT NOTE - ALL REFERENCES WILL BE INCLUDED AT THE TOP OF EACH SECTION TO AVOID REFERENCING MULTIPLE TIMES, A COMPLETE LIST OF REFERENCES IS AVAILABLE FROM THE REFERENCE SECTION AT THE END OF THIS DOCUMENT, THANK YOU**

## Introduction to Project
This project was developed in lieu of the final year exam, which was cancelled due to the COVID-19 pandemic, for the module Artificial intelligence.  The objective of this project is to create A Language Detection Neural Network with Vector Hashing.  I will document all pertinent information in this readme.
## Vector size	
The size of the hashing vector is determined by the user via the UI and it is suggested that this size would be 235 as their are 234 languages in the training set.
## Number of Epochs
The number of epochs is again determined by the user via the UI, it is suggested that the user use 1000 epochs to train the data as this is a good number to ensure that each neuron in the hidden layer is trained with a decent amount of the training data.
## N-grams parsing
I parsed and hashed each ngram into the fixed size vector like so:

		for(int i = ngrams; i < ngramString.length() -ngrams; i+=ngrams) {
				int hashcode = ngramString.substring(i,ngrams+i).hashCode();
		 		vector[i % vector.length] = hashcode;
				//write out line to file
		 		Utilities.normalize(vector, -1, 1);
		 		writer.write(df.format(vector[i %vector.length]));
		 		writer.write(",");
		 		//newline
		 		writer.newLine();
			}
I used a bufferedwriter to write each line to the file and the hashcode was computed by taking each ngram out of the string.  Once this loop ends the entire text will be parsed into a number of ngrams which will be determined by the user via the UI, I suggest you use 5 or 6 ngrams for this as the odds of two 5 or 6 grams occuring are 0.26 ^ 5 or 0.26 ^ 6 respectively, for those not good at maths like myself this is a very low number.  The odds that an ngram of these sizes occuring more than once is less than 0.01%, that being said you do not want to make the ngram size too  large as there is a limited amount of text in the data set.
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
As you can see from the above image the sigmoidal function gradually increases, this can be useful for training a neural network as the accuracy or similarity of data can be trained using a curve so for example, say we had a string of Chinese characters and we were looking to find out which language it was, Japanese would score fairly highly as a lot of Japanese characters are taken from the Chinese alphabet and Chinese would score highly also it is in this way by using the similarity of the language to the training data that the neural network can decipher the language.

#### TANH Function

### Number of Neurons & Explanation of topology
For the input and output layers I decided to use 235 neurons as there are 234 languages and the array starts at 0 so in reality there are 235 pieces of data in the training array.  For the hidden layer I used 20 neurons as it seemed a good number due to the fact it is 1/10th of the entire trainingset and would give each neuron a decent amount of data to learn from.

The overall topology of the neural network would look like such: imagine a layer of 235 nodes which are not connected to each other, now imagine another layer of nodes which numbers 20 in total and now imagine that the 235 layers of nodes are connected to each of the 20 nodes and each of the 20 nodes are connected to a final layer of 235 nodes.  It is in this way that the neural network functions.  Each node in the hidden layer, which is the layer of 20 nodes, is sent data from the initial 235 nodes, once the hidden layer neurons are sent this data they are trained to recognize patterns in the data through activation functions such as sigmoidal and tanh.  Once the training is complete, the timing of which depends on the amount of training data, number of neurons, epochs etc..., then the final data is sent to the end nodes which will determine the language of the test data passed into the neural network.

### Error Rate
## Extras
+ Allowed user to enter in the vector size, ngram size & number of epochs
+ Allowed user to enter in the directory for the training data
+ reported network topology to user via the CLI
+ Gave hints to user on which size to use for vector, ngrams + epochs

## References:
Below I will include references to where I got the information for each section of this readme.

[1]: https://en.wikipedia.org/wiki/Sigmoid_function
2.