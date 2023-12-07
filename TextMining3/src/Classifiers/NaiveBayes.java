package Classifiers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class NaiveBayes extends Classifier{
	
	Map<String,Double> Probability_classes;
	Map<String,Map<String,Double>> Probability_word_knowing_class;
	Map<String,Double> nplusmclass;
	
	NaiveBayes(String type) {
		super();
		this.type=type;
		Probability_classes = new HashMap<String,Double>();
		Probability_word_knowing_class = new HashMap<String,Map<String,Double>>();
		nplusmclass = new HashMap<String,Double>();
	}
	
	public void train(String path,int test) {
		
		Probability_classes = new HashMap<String,Double>();
		Probability_word_knowing_class = new HashMap<String,Map<String,Double>>();
		nplusmclass = new HashMap<String,Double>();
		super.train(path, test);
		
		for (String name : words.keySet()) {
			
			String[] C = name.split("__");
			String c1= C[1];
			
			
			if (!(Probability_classes.containsKey(c1))) {
				
				Probability_classes.put(c1, 1.0);
			
			}else {
				
				Double v=Probability_classes.get(c1);
				Probability_classes.put(c1, v+1.0);
				
			}	

		}
		
			
		for (String name : Probability_classes.keySet()) {
			Double v= Probability_classes.get(name);
			Probability_classes.put(name, v/(words.size()));
			
		}
		
	
		Map<String, Double> v = new HashMap<>();

		for (String name : Probability_classes.keySet()) {
		    
			Probability_word_knowing_class.put(name, v);

		    Double n = 0.0;
		    Set<String> m = new HashSet<String>(); 

		    for (String name2 : words.keySet())for (String name3 : words.get(name2).keySet()) if (!m.contains(name3)) {
	                m.add(name3);
		    }
		    
		    for (String name2 : words.keySet()) if(name2.split("__")[1].equals(name)){
		
		        String c2 = name2.split("__")[1];
		        Probability_word_knowing_class.putIfAbsent(c2, new HashMap<>());

		        for (String name3 : words.get(name2).keySet()) {
		            
		            Double v2 = Probability_word_knowing_class.get(c2).getOrDefault(name3, 2.0);
		            Probability_word_knowing_class.get(c2).put(name3, v2 + words.get(name2).get(name3));

		            n=n+words.get(name2).get(name3); 
		        }
		    }

		    for (String name2 : Probability_word_knowing_class.get(name).keySet()) {
		        Double v2 = Probability_word_knowing_class.get(name).get(name2);
		        Probability_word_knowing_class.get(name).put(name2, v2 / (n + m.size()));
		    }
		    List<String> sor = new ArrayList<>(Probability_word_knowing_class.get(name).keySet());
		    Collections.sort(sor,Collections.reverseOrder());
		    
			nplusmclass.put(name,n+m.size());
		}
	}
	
public String predit(String a) throws FileNotFoundException {
		
		String walou=super.predit(a);
		File f=new File(a);
		String data="";
		Scanner myReader = new Scanner(f);
		
		while (myReader.hasNextLine()) {
			data = data + myReader.nextLine();
        	}
		myReader.close();
		
		Analyzer analyzer = new StandardAnalyzer();
		List<String> stemmedWords = new ArrayList<>();
		
		try (TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(data))) {
		   
			tokenStream.reset();
		    
		    while (tokenStream.incrementToken()) {
		        stemmedWords.add(tokenStream.getAttribute(CharTermAttribute.class).toString());
		    }
		} catch (IOException e) {
		   
		}

		Map<String, Double> vect = new HashMap<>();

		for (String s : stemmedWords) {
		    if (vect.containsKey(s)) {
		        vect.put(s, vect.get(s) + 1);
		    } else {
		        vect.put(s, 1.0);
		    }
		}
		analyzer.close();
		
		Map<String,Double> ehn= new HashMap<String,Double>();
		Map<String,Long> classes_grade= new HashMap<String,Long>(); 
		
		for (String cl : Probability_classes.keySet()) {
			
			ehn.put(cl, null);
			double v3=1.0;
			long grade=0;
			
			for(String wo : vect.keySet()) {
				if (Probability_word_knowing_class.get(cl).containsKey(wo)) {
				v3=v3*vect.get(wo)*Probability_word_knowing_class.get(cl).get(wo);
				while(v3<1) {
					v3=10*v3;
					grade++;
				}
			}
			}
			
			v3=v3*Probability_classes.get(cl);
			ehn.put(cl, v3);
			classes_grade.put(cl, grade);
		}
		
		
		
		
		String cl12=null;
		double v12=0.0;
		long vgrade=1000000000;
		

		
		for (String yyy :ehn.keySet()) {
			if(classes_grade.get(yyy)<=vgrade) if(ehn.get(yyy)> v12) {
				cl12=yyy;
				v12=ehn.get(yyy);
				vgrade=classes_grade.get(yyy);
				}
		}
				
		return cl12;
		
	}
	
public void reset_secondaire() {
	super.reset_secondaire();
	Probability_classes.clear();
	Probability_word_knowing_class.clear();
	nplusmclass.clear();
}

}
