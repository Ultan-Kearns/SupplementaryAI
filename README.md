# Supplementary AI Project - Ultan Kearns
## Produced as part of the GMIT 4th Year Module: Artificial Intelligence 
### Under the supervision of Dr. John Healy

## Introduction to Project

## Parsing
To parse the training data I thought it was a good idea to let the user decide the number of Ngrams in the Runner class and then I used the user specified ngrams to break each language string in the training data up into the specified size.  

The data is then parsed using the following method:

		StringBuffer ngramString = new StringBuffer(text);
		for(int i = ngrams; i < text.length(); i+= ngrams) {
			ngramString.insert(i, " ");
			i++;
		}
		
This code goes through each line of text and inserts a space after every i characters, in this way the text of the entire file is broken up and then inserted into the vector array which will be used to train the data set.
	
## Hashing
I hased each ngram into the fixed size vector like so:

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
I used a bufferedwriter to write each line to the file and the hashcode was computed by taking each ngram out of the string.
## Neural Network Topology

### Activation Functions
### Number of Neurons
### Error Rate
## Extras
