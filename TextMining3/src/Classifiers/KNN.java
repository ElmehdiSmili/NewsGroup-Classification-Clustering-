package Classifiers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
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

import safar.basic.morphology.stemmer.factory.StemmerFactory;
import safar.basic.morphology.stemmer.interfaces.IStemmer;
import safar.basic.morphology.stemmer.model.WordStemmerAnalysis;
import safar.util.remover.Remover;



public class KNN extends Classifier{
	
	Map<String,Map<String,Double>> TFs;
	Map<String, Double> IDFs;
	Map<String,Map<String,Double>> TF_IDFs;
	int k;
	
	public KNN(String type,int k) {
		super();
		this.k=k;
		this.type=type;
		TFs = new HashMap<String,Map<String,Double>>();
		IDFs = new HashMap<String,Double>();
		TF_IDFs = new HashMap<String,Map<String,Double>>();
	
	}
	
	
	public void train(String path,int test) {
		
		super.train(path, test);
		TFs = new HashMap<String,Map<String,Double>>();
		IDFs = new HashMap<String,Double>();
		TF_IDFs = new HashMap<String,Map<String,Double>>();
		
		for (String name: words.keySet()) {
			
			Map<String,Double> yui2= new HashMap<String,Double>();
			TFs.put(name, yui2);
			
			for (String name1: words.get(name).keySet()) {
				
				TFs.get(name).put(name1, words.get(name).get(name1)/words.get(name).values().stream().reduce(0.0, Double::sum));
				
				}
		}
			
		for (String name: words.keySet()) 
			for (String name1: words.get(name).keySet()) {
				if (!(IDFs.containsKey(name1)))	IDFs.put(name1,1.0);
				else IDFs.put(name1,IDFs.get(name1)+1);
				}
		for (String name: IDFs.keySet()) IDFs.put(name,Math.log(words.size()/IDFs.get(name)));
		
		for (String name: TFs.keySet()) {
			
			Map<String,Double> yui3= new HashMap<String,Double>();
			TF_IDFs.put(name, yui3);
			
			for(String name1: TFs.get(name).keySet()) {
			TF_IDFs.get(name).put(name1,TFs.get(name).get(name1)*IDFs.get(name1));
			}
			
		}
		
	}
		
	public String predit(String a) throws FileNotFoundException {
		
		String walou=super.predit(a);
		
		List<Double> sims = new ArrayList<Double>();
		List<String> paths = new ArrayList<String>();
		for(int it=0;it<k;it++) {
			sims.add(0.0);
			paths.add("");
		}
		
		for (String name: words.keySet()){
			double d177=similarite(name,a);
			sims.add(d177);
			Collections.sort(sims);
			if(sims.get(sims.size()-1)!=d177) {
				int yy=sims.indexOf(d177);
				paths.add(name);
				for(int io=yy;io<paths.size()-1;io++) {
					String echange= paths.get(yy);
					paths.add(yy, paths.get(paths.size()-1));
					paths.add(paths.size()-1,echange);
				}
				sims.remove(sims.size()-1);
				paths.remove(sims.size()-1);
			}
			
		}
		
		
		
		for(int it=0;it<k;it++) paths.set(it, paths.get(it).substring(paths.get(it).indexOf("__")+2));
		
		Map<String,Integer> pref = new HashMap<String,Integer>();
		
		int cont=0;
		String cl="";
		
		for(String cl2:paths) {
			if(pref.containsKey(cl2)) {
				pref.put(cl2, pref.get(cl2)+1);
			}else {
				pref.put(cl2, 1);
			}
		}
		
		for(String cl13: pref.keySet())if(pref.get(cl13)>cont) {
			cont=pref.get(cl13);
			cl=cl13;
		}
		
		return cl;
}
		
	double similarite(String d1, String d2) throws FileNotFoundException {
	
		Double sim=0.0;
		Double n1=0.0;
		Double n2=0.0;
		
		Map<String, Double> D1 = TF_IDFs.get(d1);
		
		Map<String, Double> vect =	new HashMap<String, Double>();
		File dir = new File(d2);
		String temp= dir.getName();

		String data="";
		Scanner myReader = new Scanner(dir);
		
		while (myReader.hasNextLine()) {
			data = data + myReader.nextLine();
        }
		myReader.close();
	
		Analyzer analyzer = new StandardAnalyzer();
		temp = temp.substring(0, d2.indexOf("&"));
		System.out.print(temp);
		List<String> stemmedWords = new ArrayList<>();
	    try (TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(data))) {
	        tokenStream.reset();
	        while (tokenStream.incrementToken()) {
	            stemmedWords.add(tokenStream.getAttribute(CharTermAttribute.class).toString());
	        }
	    } catch (IOException e) {
	    }

	    for (String s : stemmedWords) {
	        if (vect.containsKey(s)) {
	            vect.put(s, 1.0 + vect.get(s));
	        } else {
	            vect.put(s, 1.0);
	        }
	    }
		analyzer.close();
		
		for(String wo: vect.keySet()) {
			vect.put(wo, (IDFs.get(temp)*vect.get(wo))/vect.values().stream().reduce(0.0, Double::sum));
		}
		for(String name: D1.keySet()) {
			Double n11= (D1.get(name))*(D1.get(name));
			n1= n1 + n11;
		}
		for(String name: vect.keySet()) {
			Double n22= (vect.get(name))*(vect.get(name));
			n2= n2 + n22;
		}
		
		n1= Math.sqrt(n1);
		n2 = Math.sqrt(n2);
		
		for (String t: D1.keySet()) if(!(vect.containsKey(t))) vect.put(t, 0.0); 
		for (String t: vect.keySet()) if(!(D1.containsKey(t))) D1.put(t, 0.0);
		
		for (String t: D1.keySet()) sim= sim + (D1.get(t)*vect.get(t));
		
		sim= sim/(n1*n2);
		
		return sim;
	
}
	
public void reset_secondaire() {
	super.reset_secondaire();
	TFs.clear();
	IDFs.clear();
	TF_IDFs.clear();
}

}
