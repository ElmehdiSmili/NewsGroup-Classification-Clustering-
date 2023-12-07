package crossValidation;

import java.io.IOException;
import java.util.Map;

public class Main {
	
	public static void main(String[] args) throws IOException {
		
		CrossValidation cv1= new CrossValidation(6);
		
		
		cv1.reset("C:\\Users\\ADMIN\\eclipse-workspace2\\TextMining3\\dataset");
		cv1.foldersDistribution("C:\\Users\\ADMIN\\eclipse-workspace2\\TextMining3\\dataset");
		Map<Integer,Double> op= cv1.crossvalidationscores("C:\\Users\\ADMIN\\eclipse-workspace2\\TextMining3\\dataset","NaiveBayes"/*,"Arabic"*/);
		
		System.out.print(op.toString());
		
		
		
	}

}
