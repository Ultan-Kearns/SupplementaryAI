package ie.gmit.sw;

import java.util.Scanner;

public class Runner {
	public static void main(String[] args){
		
		/*			
			Each of the languages in the enum Language can be represented as a number between 0 and 234. You can 
			map the output of the neural network and the training data label to / from the language using the
			following. Eg. index 0 maps to Achinese, i.e. langs[0].  
		*/
		Scanner s = new Scanner(System.in);
		while(true) {
			System.out.println("1. Show Languages\n2. Train\n3. To exit");
			int test = s.nextInt();
			if(test == 1) {
				Language[] langs = Language.values(); //Only call this once...		
				for (int i = 0; i < langs.length; i++){
					System.out.println(i + "-->" + langs[i]);
				}
			}
			else if(test == 2) {
				NeuralNetwork nn = new NeuralNetwork();
		 
			}
			else if(test == 3)
			{
				break;
			}
			else {
				System.out.println("\nInvalid Command");
			}
		}
	}
}