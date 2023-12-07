package Classifiers;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import java.util.Scanner;

public abstract class Classifier {
	
	public String type;
	Map<String,String> corpus;
	Map<String,Map<String,Double>> words;
	//String langue="";
	
	
	Classifier(){
		this.corpus = new HashMap<String,String>();
		this.words= new HashMap<String,Map<String,Double>>();
	}
	
	public static Classifier Type(String type) {
        if ("NaiveBayes".equals(type)) {
            return new NaiveBayes(type);
        } else if ("KNN".equals(type)) {
        	 Scanner scanner = new Scanner(System.in);
             System.out.print("Enter k value: ");
             int k = scanner.nextInt();      	
        	return new KNN(type,k);
        }else {
            throw new IllegalArgumentException("Choose a valid classifier type");
        }
    }
	
	public void train(String path,int test) {
		

		Analyzer analyzer = new StandardAnalyzer();
		String en = path;

		for (String name : this.corpus.keySet()) if(!name.substring(name.indexOf("&")+1).equals(""+test)){
		    
			String valeur = this.corpus.get(name);
		    name = name.substring(en.length(), name.indexOf("&"));

		    List<String> stemmedWords = new ArrayList<>();
		    try (TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(valeur))) {
		        tokenStream.reset();
		        while (tokenStream.incrementToken()) {
		            stemmedWords.add(tokenStream.getAttribute(CharTermAttribute.class).toString());
		        }
		    } catch (IOException e) {
		    }

		    if (!words.containsKey(name)) {
		        words.put(name, new HashMap<>());
		    }

		    for (String s : stemmedWords) {
		        if (words.get(name).containsKey(s)) {
		            words.get(name).put(s, words.get(name).get(s) + 1);
		        } else {
		            words.get(name).put(s, 1.0);
		        }
		    }
		}
		analyzer.close();
		
		
	}
	
	public String predit(String path) throws FileNotFoundException {
		return null;
	}
	
	public void dataset(String path/*,String l*/) throws IOException {
		
		File dir = new File(path);
		File[] directoryListing = dir.listFiles();
		
		for(File child : directoryListing) {
			
			String data="";
			Scanner myReader = new Scanner(child);
			
			while (myReader.hasNextLine()) {
				data = data + myReader.nextLine();
	        	}
			this.corpus.put(child.getAbsolutePath(),data);
			myReader.close();
			
		}	
		//langue=l;

		
}
	public void reset_secondaire() {
		this.words.clear();
	}
	public void reset_primaire() {
		this.words.clear();
		this.corpus.clear();
		//langue="";
	}
}
