package svm;

import svm.TFIDF;
import java.util.Map;


import java.util.HashMap;

public class SVMClassifier {
    private Map<String, Map<String, Double>> tfIdfMap;
    private Map<String, Integer> classLabels;
    private double learningRate = 0.01;
    private double lambda = 0.1;
    private int numEpochs = 1000;
    private Map<String, Double> weights;



	public SVMClassifier(Map<String, Map<String, Double>> tfIdfMap) {
        this.tfIdfMap = tfIdfMap;
        this.classLabels = new HashMap<>();
        this.weights = new HashMap<>();
    }


	
	
	

	public void train() {
	    // Assign class labels to your data
	    int label = 0;
	    for (String className : tfIdfMap.keySet()) {
	        classLabels.put(className, label);
	        label++;
	    }

	    // Initialize weights
	    for (String feature : tfIdfMap.values().iterator().next().keySet()) {
	        weights.put(feature, 0.0);
	    }

	    // Training loop
	    for (String className : tfIdfMap.keySet()) {
	        Map<String, Double> instance = tfIdfMap.get(className);
	        int trueLabel = classLabels.get(className);

	        double score = 0.0;
	        for (Map.Entry<String, Double> entry : instance.entrySet()) {
	            Double weight = weights.get(entry.getKey());
	            Double featureValue = entry.getValue();

	            if (weight != null && featureValue != null) {
	                score += weight * featureValue;
	            }
	        }

	        for (String feature : instance.keySet()) {
	            Double featureValue = instance.get(feature);

	            if (featureValue != null) {
	                if (trueLabel * score * featureValue < 1) {
	                    Double currentWeight = weights.get(feature);

	                    if (currentWeight != null) {
	                        weights.put(feature, currentWeight + learningRate * (trueLabel - lambda * currentWeight));
	                    }
	                }
	            }
	        }
	    }

	}

	
	
	
	


    public String classify(Map<String, Double> document) {
        double maxScore = Double.NEGATIVE_INFINITY;
        String predictedClass = null;

        for (String className : tfIdfMap.keySet()) {
            Map<String, Double> instance = tfIdfMap.get(className);
            double score = 0.0;
            for (Map.Entry<String, Double> entry : document.entrySet()) {
                if (instance.containsKey(entry.getKey())) {
                    Double weight = weights.get(entry.getKey());
                    Double featureValue = entry.getValue();

                    if (weight != null && featureValue != null) {
                        score += weight * featureValue;
                    }
                }
            }

            if (score > maxScore) {
                maxScore = score;
                predictedClass = className;
            }
        }


        return predictedClass;
    }
    
    
    
    
    private static void displayMap(Map<String, Map<String, Double>> map) {
    	Integer c=0;
        for (Map.Entry<String, Map<String, Double>> entry : map.entrySet()) {
        	//System.out.println(" Classe: " + entry.getKey());
        	c=c+1;
            Map<String, Double> termMap = entry.getValue();
            //Integer count = 0;
            for (Map.Entry<String, Double> termEntry : termMap.entrySet()) {
            	//count =count+1;
                System.out.println(termEntry.getKey() + ": " + termEntry.getValue());
                
            }
           // System.out.println("nember of stemmer in classe :::::::::::"+ count);
        }
        //System.out.println("nember of classe :::::::::::"+ c);
    }
    
    
    
    
    
    
    
    

    public static void main(String[] args) {
        // Create your tf-idf map
		String inputFilePath = "C:\\Users\\Dell\\bdsas s3\\NLP & TEXT MINING\\SVM\\TFIDF.ser";
		String docPath = "C:\\Users\\Dell\\bdsas s3\\NLP & TEXT MINING\\cross validation KNN naive baysienne\\20_newsgroups\\alt.atheism\\0000000";
		//Map<String, Map<String, Double>> tfIdfMap = null;
		Map<String, Map<String, Double>> tfIdfMap = new HashMap<>(); // Populate with your data
		try {
			tfIdfMap = TFIDF.MapMapSer(inputFilePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/////////
		
		// Function to display the map of term occurrences

    	

		SVMClassifier svm = new SVMClassifier(tfIdfMap);
		
        svm.train();

        // Example document for classification
        Map<String, Double> document = new HashMap<>(); // Populate with your document's features
        try {
        	document = TFIDF.DocumentMap(docPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        String predictedClass = svm.classify(document);
        System.out.println("Predicted class: " + predictedClass);
    }
}
