package Metrics;

import java.util.*;

public class Accuracy {
	

	Map<String,List<List<String>>> R_S;
	Map<String, ArrayList<HashSet<String>>> R_Sf;
	
	public Accuracy(){
		
		R_S= new HashMap<String,List<List<String>>>();
		R_Sf=new HashMap<String, ArrayList<HashSet<String>>>();
		
	}
	
	public void reset() {
		R_S.clear();
		R_Sf.clear();
	}

	public void Remplir(Map<String,String> I_P,Map<String,List<String>> cD) {
		
		for(String c1: cD.keySet()) if (!(R_S.containsKey(c1))) {
			List<List<String>> v1=new ArrayList<List<String>>();
			List<String> v2=new ArrayList<String>();
			v1.add(cD.get(c1));
			v1.add(v2);
			R_S.put(c1, v1);
		}
		System.out.print(I_P);
		for (String i1 : I_P.keySet()) R_S.get(I_P.get(i1)).get(1).add(i1);

		for(String op1: R_S.keySet()){
			ArrayList<HashSet<String>> v1=new ArrayList<HashSet<String>>();
			R_Sf.put(op1,v1);
			for(List<String> op2: R_S.get(op1)) {
				HashSet<String> v2=new HashSet<String>(op2);
				R_Sf.get(op1).add(v2);
			}			
		}
	}
}
