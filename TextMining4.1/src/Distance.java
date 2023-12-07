import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Distance {
	
	static Double distance(List<Double> doc1,List<Double> doc2) {
		
		double dist=0.0;
		for(int i=0;i<doc1.size();i++)dist=dist+(doc1.get(i)-doc2.get(i))*(doc1.get(i)-doc2.get(i));
		
		dist= Math.sqrt(dist);
		
		return dist;
	}
	
	
	static Double minimum_d(Map<String,List<Double>> c1,Map<String,List<Double>> c2) {
		
		double dist=0.0;
		
		for(String doc_1:c1.keySet())for(String doc_2:c2.keySet()) {
			dist=Distance.distance(c1.get(doc_1), c2.get(doc_2));
			break;
		}
		
		for(String doc_1:c1.keySet())for(String doc_2:c2.keySet()) if(Distance.distance(c1.get(doc_1), c2.get(doc_2))<dist) dist=Distance.distance(c1.get(doc_1), c2.get(doc_2));
		
		return dist;
		
	}
	
	static Double maximum_d(Map<String,List<Double>> c1,Map<String,List<Double>> c2) {
		
		double dist=0.0;
		
		for(String doc_1:c1.keySet())for(String doc_2:c2.keySet()) {
			dist=Distance.distance(c1.get(doc_1), c2.get(doc_2));
			break;
		}
		
		for(String doc_1:c1.keySet())for(String doc_2:c2.keySet()) if(Distance.distance(c1.get(doc_1), c2.get(doc_2))>dist) dist=Distance.distance(c1.get(doc_1), c2.get(doc_2));
		
		return dist;
	}
	
	static Double average_d(Map<String,List<Double>> c1,Map<String,List<Double>> c2) {
		return null;
	}
	
	static Double centre_gravite_d(Map<String,List<Double>> c1,Map<String,List<Double>> c2) {
		return Distance.distance(Distance.centreGravite(c1), Distance.centreGravite(c2));
	}
	
	
	static Double ward_d(Map<String,List<Double>> c1,Map<String,List<Double>> c2) {
		return null;
	}
	static List<Double> centreGravite(Map<String,List<Double>> c) {
		
		List<Double> centre_gravite=new ArrayList<Double>();
		int dim=0;
		
		for(String point: c.keySet()) {
			dim = c.get(point).size();
			break;
		}
		
		for(int i=0;i<dim;i++) centre_gravite.add(0.0);
		
		for(String point: c.keySet()) for(int i=0;i<dim;i++) centre_gravite.add(i, centre_gravite.get(i)+c.get(point).get(i));
		
		for(int i=0;i<dim;i++) centre_gravite.add(i,centre_gravite.get(i)/dim);
		
		return centre_gravite;
	}
}
