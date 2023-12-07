import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Clustering {
	
	int nb_clusters=0;
	Map<Integer,HashMap<String,List<Double>>> clusters;
	Map<Integer,HashMap<Integer,Double>> clusters_distances;
	Map<Integer,List<Double>> centres_gravite_clusters;

	Clustering(){
		clusters = new HashMap<Integer,HashMap<String,List<Double>>>();
		clusters_distances = new HashMap<Integer,HashMap<Integer,Double>>();
		centres_gravite_clusters = new HashMap<Integer,List<Double>>();
	}
	
	void clusteringAscendant(Map<String,ArrayList<Double>> espace,int k) {
		
		nb_clusters=k;
		int espace_size=0;
		
		for(String doc:espace.keySet()) {
			espace_size++;
			HashMap<String,List<Double>> doc_to_class=new HashMap<String,List<Double>>();
			doc_to_class.put(doc, espace.get(doc));
			clusters.put(espace_size, doc_to_class);
		}
		
		for(int cluster1: clusters.keySet()) for(int cluster2: clusters.keySet())if(cluster1!=cluster2){
			
			if(clusters_distances.containsKey(cluster2)) continue;
			
			if(clusters_distances.containsKey(cluster1)) {
				if(!(clusters_distances.get(cluster1).containsKey(cluster2))) {
					HashMap<Integer,Double> distanced_doc=new HashMap<Integer,Double>();
					distanced_doc.put(cluster2, Distance.centre_gravite_d(clusters.get(cluster1), clusters.get(cluster2)));
					clusters_distances.put(cluster1, distanced_doc);
				}else {	
				clusters_distances.get(cluster1).put(cluster2,Distance.centre_gravite_d(clusters.get(cluster1), clusters.get(cluster2)));	
				}
			}else {
				HashMap<Integer,Double> distanced_doc=new HashMap<Integer,Double>();
				distanced_doc.put(cluster2, Distance.centre_gravite_d(clusters.get(cluster1), clusters.get(cluster2)));
				clusters_distances.put(cluster1,distanced_doc);	
			}
		}
		
		double distance_minimale=0.0;
		int cluster1=0;
		int cluster2=0;
		
		for(int i1:clusters_distances.keySet())for(int i2:clusters_distances.keySet()) {
			distance_minimale=clusters_distances.get(i1).get(i2);
			break;
		}
		
		while(clusters.size()>nb_clusters) {
			
			for(int i1:clusters.keySet())for(int i2:clusters.keySet())if(distance_minimale>clusters_distances.get(i1).get(i2)) {
				distance_minimale=clusters_distances.get(i1).get(i2);
				cluster1=i1;
				cluster2=i2;
			}
			
			merge(cluster1,cluster2);
			
			for(int cluster: clusters_distances.keySet()) for(int cluster_inside: clusters_distances.get(cluster).keySet()){
				
				if(cluster_inside==cluster2) {
					clusters_distances.get(cluster).remove(cluster_inside);
					continue;
				}

				if(cluster_inside==cluster1) {
					clusters_distances.get(cluster).put(cluster_inside, Distance.centre_gravite_d(clusters.get(cluster), clusters.get(cluster_inside)));
					continue;
				}
				
				if(cluster==cluster1) {
					clusters_distances.get(cluster).put(cluster_inside, Distance.centre_gravite_d(clusters.get(cluster), clusters.get(cluster_inside)));
					continue;
				}
			}	
			
			clusters_distances.remove(cluster2);
			
			nb_clusters--;
		}
	}
	
	void KMeans(Map<String,ArrayList<Double>> espace,int k) {
		
		nb_clusters=k;
		Random rand = new Random(); 
		
		for(String doc:espace.keySet()) {
			
			int cluster=rand.nextInt(nb_clusters)+1;
			
			if(clusters.containsKey(cluster)) {
				clusters.get(cluster).put(doc, espace.get(doc));
			}else {
				HashMap<String,List<Double>> doc_de_class=new HashMap<String,List<Double>>();
				doc_de_class.put(doc, espace.get(doc));
				clusters.put(cluster, doc_de_class);
			}
			
		}
		
		for(int cluster: clusters.keySet()) centres_gravite_clusters.put(cluster, Distance.centreGravite(clusters.get(cluster)));
		
		int converge=0;
		
		while(converge==0){
			
			converge=1;
			
			for(int cluster:clusters.keySet()) for(String doc:clusters.get(cluster).keySet()){
				
				int change=0;
				int clusterproche=0;
				double distance_minime=0.0;
				
				for(int cluster1: centres_gravite_clusters.keySet()) {
					distance_minime=Distance.distance(centres_gravite_clusters.get(cluster),clusters.get(cluster).get(doc));
					break;
				}
				
				for(int cluster1: centres_gravite_clusters.keySet()) if(Distance.distance(centres_gravite_clusters.get(cluster1),clusters.get(cluster).get(doc))<distance_minime) {
						distance_minime=Distance.distance(centres_gravite_clusters.get(cluster1),clusters.get(cluster).get(doc));
						clusterproche=cluster1;
						change=1;
				}
				
				if(change==1) {
					clusters.get(clusterproche).put(doc,clusters.get(cluster).get(doc));
					clusters.get(cluster).remove(doc);
				}		
			}
			
			for(int cluster: clusters.keySet()) if(centres_gravite_clusters.get(cluster).equals(Distance.centreGravite(clusters.get(cluster)))){
				centres_gravite_clusters.put(cluster, Distance.centreGravite(clusters.get(cluster)));
				converge=0;
			}
			
		}
	}
	
	void merge(int cluster1,int cluster2) {
		for(String doc: clusters.get(cluster2).keySet()) {
			clusters.get(cluster1).put(doc, clusters.get(cluster2).get(doc));
		}
		clusters.get(cluster2).clear();
		clusters.remove(cluster2);
	}

}
