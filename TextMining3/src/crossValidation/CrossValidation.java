package crossValidation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Classifiers.Classifier;
import Metrics.Accuracy;
import Metrics.F1score;

public class CrossValidation {
		
	Map<String,List<String>> classDistribution;
	int param;
	
	CrossValidation(int param){
		this.classDistribution=new HashMap<String,List<String>>();
		this.param=param;
	}
	
	public void setParam(int param) {
		this.param=param;
	}
	
	
	public void foldersDistribution(String path) throws IOException {
		
	File dir = new File(path);
	File[] directoryListing = dir.listFiles();
	Map<String,Double> classprob=new HashMap<String,Double>();
	
	for(File child : directoryListing) {
		
		if(this.classDistribution.containsKey(child.getName().substring(child.getName().indexOf("__")+2))) {	
			List<String> v=this.classDistribution.get(child.getName().substring(child.getName().indexOf("__")+2));
			v.add(child.getAbsolutePath());
			this.classDistribution.put(child.getName().substring(child.getName().indexOf("__")+2), v);	
		}
		else {
			List<String> v= new ArrayList<String>();
			v.add(child.getAbsolutePath());
			this.classDistribution.put(child.getName().substring(child.getName().indexOf("__")+2), v);
		}	
	}
	
	for(String c1: classDistribution.keySet()) {
		List<String> shuf1 = classDistribution.get(c1);	
		Collections.shuffle(shuf1);
		classDistribution.put(c1,shuf1);
		classprob.put(c1,(double)(shuf1.size()));
	}
	

	HashMap<String, Double> shallowCopy = new HashMap<String, Double>();
	
	
	List<String> FFake= new ArrayList<String> ();
	
	for(String pors: classDistribution.keySet()) FFake.addAll(classDistribution.get(pors));

	for(int i1=1;i1<param+1;i1++) {
		System.out.print(i1);
		for(String c2: classprob.keySet()) shallowCopy.put(c2, 0.0);
	
		Double au2=0.0;
		Double au4=(double)directoryListing.length;
		
		if (au4%param==0) au4=(au4/param)+1;
		else au4=au4/param;
		
		while(au2<au4 && (!(FFake.isEmpty()))){
			
			for(File child : directoryListing)if(FFake.contains(child.getAbsolutePath()))if(shallowCopy.containsKey(child.getName().substring(child.getName().indexOf("__")+2))){
			
				String c_wach1=child.getName().substring(child.getName().indexOf("__")+2);
				
				Double au3=classprob.get(c_wach1);
			
				if (classprob.get(c_wach1)%param==0) au3=(au3/param)+1;
				else au3=(au3/param);
			
				if(shallowCopy.get(c_wach1)<au3) {
					File renam= new File(child.getAbsolutePath()+"&"+i1);
					child.renameTo(renam);
					shallowCopy.put(c_wach1, shallowCopy.get(c_wach1)+1);
					classDistribution.get(c_wach1).set(classDistribution.get(c_wach1).indexOf(child.getAbsolutePath()),child.getAbsolutePath()+"&"+i1);
					FFake.remove(child.getAbsolutePath());
				}
				else {
					shallowCopy.remove(c_wach1);
				}

				au2=au2+1;
			}
		}
		System.out.print(i1);
	}
}
	
	public Map<Integer,Double> crossvalidationscores(String path,String g/*,String l*/) throws IOException{
		
		Classifier classifieur= Classifier.Type(g);
		classifieur.reset_primaire();
		classifieur.reset_secondaire();
		
		
		Map<Integer,Double> areturn1= new HashMap<Integer, Double>();
		areturn1.put(0,0.0);
		areturn1.put(1,0.0);
		areturn1.put(2,0.0);
		
		
		classifieur.dataset(path/*,l*/);
		
		for(int it=1;it<param+1;it++) {
			System.out.print(it);
			classifieur.reset_secondaire();
			classifieur.train(path,it);
			Map<String,List<String>> cD= new HashMap<String,List<String>>();
			
			for(String c1: classDistribution.keySet()) {
				List<String> l1= new ArrayList<String>();
				for(int i=0;i<classDistribution.get(c1).size();i++) {
					String s0= classDistribution.get(c1).get(i);
					String s1= s0.substring(s0.indexOf("&")+1);
					if(s1.equals(""+it)) l1.add(s0);
				}
				cD.put(c1,l1);
			}
			
			Map<String,String> I_P=new HashMap<String,String>();
			File dir= new File(path);
			File[] docs= dir.listFiles();
			
			
			
			for(File fou: docs) if(fou.getAbsolutePath().substring(fou.getAbsolutePath().indexOf("&")+1).equals(""+it)){
				I_P.put(fou.getAbsolutePath(),classifieur.predit(fou.getAbsolutePath()));	
			}
			
			Accuracy A=new Accuracy();
			A.Remplir(I_P, cD);
			
			Map<Integer,Double> areturn= new HashMap<Integer, Double>();
			areturn.put(0,0.0);
			areturn.put(1,0.0);
			areturn.put(2,0.0);
			
			
			for(String cl: cD.keySet()) {
				F1score f1s=new F1score();
				double a=areturn.get(0)+f1s.calcul_p(A, cl);
				areturn.put(0,a);
				double b=areturn.get(1)+f1s.calcul_r(A, cl);
				areturn.put(1,b);
				double c=areturn.get(2)+f1s.calcul_f(A, cl);
				areturn.put(2,c);
			}
			
			double a=areturn.get(0);
			double b=areturn.get(1);
			double c=areturn.get(2); 
			
			
			areturn.put(0,a/cD.size());
			areturn.put(1,b/cD.size());
			areturn.put(2,c/cD.size());

			
		
			double a1=areturn1.get(0)+areturn.get(0);
			areturn1.put(0,a1);
			double b1=areturn1.get(1)+areturn.get(1);
			areturn1.put(1,b1);
			double c1=areturn1.get(2)+areturn.get(2);
			areturn1.put(2,c1);
			
			A.reset();
			System.out.print(areturn1);
			System.out.print(it);
		}	
			
		double a=areturn1.get(0);
		double b=areturn1.get(1);
		double c=areturn1.get(2); 
		areturn1.put(0,a/param);
		areturn1.put(1,b/param);
		areturn1.put(2,c/param);

		
		classifieur.reset_primaire();
		return areturn1;
	}

	public void reset(String path) {
		
		File dir = new File(path);
		File[] directoryListing = dir.listFiles();
		
		for(File f: directoryListing) if(f.getAbsolutePath().indexOf("&")>-1){
			File renam= new File(f.getAbsolutePath().substring(0,f.getAbsolutePath().indexOf("&")));
			f.renameTo(renam);
			
		}
	}
}


