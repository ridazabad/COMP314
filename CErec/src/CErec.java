import weka.core.*;
import weka.core.neighboursearch.*;
import java.io.*;

import java.util.*;

public class CErec 
{
	// function to sort hashmap by values 
    public static HashMap<Integer, Double> sortByValue(HashMap<Integer, Double> hm) 
    { 
        // Create a list from elements of HashMap 
        List<Map.Entry<Integer, Double> > list = 
               new LinkedList<Map.Entry<Integer, Double> >(hm.entrySet()); 
  
        // Sort the list 
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double> >() { 
            public int compare(Map.Entry<Integer, Double> o1,  
                               Map.Entry<Integer, Double> o2) 
            { 
                return (o2.getValue()).compareTo(o1.getValue()); 
            } 
        }); 
          
        // put data from sorted list to hashmap  
        HashMap<Integer, Double> temp = new LinkedHashMap<Integer, Double>(); 
        for (Map.Entry<Integer, Double> aa : list) { 
            temp.put(aa.getKey(), aa.getValue()); 
        } 
        return temp; 
    } 
  
	public static void main(String [] args)
	{
		boolean debug=false;
		boolean debug2 = true;
		if(args.length<2)
		{
			System.out.println("to run: java WekaRecommender <ratings file name> <active user description in file <file name>>");
			System.exit(0);
		}
	
		
		Instances dataset=null;
		Instances activeUsers=null;
		Instance activeUser=null;
		
		//loop through the file and read instances
		try
		{
			BufferedReader bufRead = new BufferedReader(
	                new FileReader(args[0]));
			dataset = new Instances(bufRead);
			bufRead.close();
			if(debug2) System.out.println(dataset.attribute(2));
			
		}
		catch(IOException ioe)
		{
			System.out.println("Error reading file '"+args[0]+"' ");
			if(debug)
				ioe.printStackTrace();
			else
				System.out.println(ioe.getMessage());
			System.exit(0);
		}
		
		try
		{
			BufferedReader bufRead = new BufferedReader(
	                new FileReader(args[1]));
			activeUsers = new Instances(bufRead);
			Enumeration en=activeUsers.enumerateInstances();
			activeUser=(Instance)en.nextElement();
			bufRead.close();	            
			
		}
		catch(IOException ioe)
		{
			System.out.println("Error reading file '"+args[1]+"' ");
			if(debug)
				ioe.printStackTrace();
			else
				System.out.println(ioe.getMessage());
			System.exit(0);
		}
	
		LinearNNSearch kNN=new LinearNNSearch(dataset);
		Instances neighbors=null;
		double [] distances=null;
		
		try
		{
			neighbors=kNN.kNearestNeighbours(activeUser, 20);
			distances=kNN.getDistances();
		}
		catch(Exception e)
		{
			System.out.println("Neighbors could not be found.");
			return;
		}
		
		double [] similarities=new double[distances.length];
		for(int i=0;i<distances.length;i++)
		{
			similarities[i]=1.0/distances[i];
			if(debug) System.out.println(distances[i]);
			if(debug) System.out.println(similarities[i]);
		}
		
		if (debug) System.out.println(neighbors);
		//look at neighbours one at a time
		Enumeration nInstances=neighbors.enumerateInstances();
		//one entry per book - holds list of recommended books		
		Map <String,List<Integer>> recommendations=new HashMap <String,List<Integer>>();
		System.out.println(distances.length);
		//loop for nearest neighbours
		while(nInstances.hasMoreElements())
		{
			Instance currNeighbor=(Instance)nInstances.nextElement();
			//adding recommended categories to the list
			//loop through every attribute for the neighbour
			for(int i=0;i<currNeighbor.numAttributes();i++)
			{
				if(debug2) System.out.println(activeUser.value(i));
				//if user has not interacted with the category
				if(activeUser.value(i)<1 ) //item is not ranked by the active user, but ranked by a critique: 0 -yes, 1-no I DISAGREE: 0- NO, 1-YES
				{
					//retrieve the name of the category
					String attrName=activeUser.attribute(i).name();
					List<Integer> lst=new ArrayList <Integer>();
					if(recommendations.containsKey(attrName))
					{
						lst=recommendations.get(attrName);
					}
					
					if( currNeighbor.value(i)>01) //read -we assume that ranked at 5
					{
						int y = (int)currNeighbor.value(i);
						//System.out.println(y);
						lst.add(y);
					}
					else
					{
						
						//add zero for this neighbour
						lst.add(0);
					}
					recommendations.put(attrName, lst);
				}
			}		
			
		}
		
		if(debug2) System.out.println(recommendations);
		//get weighted scores and similarity
		List <RecommendationRecord> finalRanks=new ArrayList <RecommendationRecord>();
		
		Iterator <String> it=recommendations.keySet().iterator();
		while(it.hasNext())
		{
			String atrName=it.next();
			double totalImpact=0;
			double weightedSum=0;
			List <Integer> ranks=recommendations.get(atrName);
			for(int i=0;i<ranks.size();i++)
			{ 
				int val=ranks.get(i);
				
				totalImpact+=similarities[i];
				weightedSum+=(double)similarities[i]*val;
				
			}
			//calculating score for every category
			RecommendationRecord rec=new RecommendationRecord();
			rec.attributeName=atrName;
			rec.score=weightedSum/totalImpact;
			
			finalRanks.add(rec);
		}
		Collections.sort(finalRanks); //descending order
		
		//print top 10 categories
		System.out.println( finalRanks.get(0));
		System.out.println( finalRanks.get(1));
		System.out.println( finalRanks.get(2));
		System.out.println( finalRanks.get(3));
		System.out.println( finalRanks.get(4));
		System.out.println( finalRanks.get(5));
		System.out.println( finalRanks.get(6));
		//System.out.println( finalRanks.get(7));
		//System.out.println( finalRanks.get(8));
		//System.out.println( finalRanks.get(9));
		HashMap <Integer,Double> mostClicked=new HashMap <Integer,Double>();
		double[] da = activeUser.toDoubleArray();
		for(int j =0; j<da.length; j++){
			mostClicked.put(j, da[j]);
		}
		///Map <Integer,Double> sorted=new HashMap <Integer,Double>();
	    //System.out.println(mostClicked);
	    Map<Integer, Double> sorted = sortByValue(mostClicked);
	    /*for (Map.Entry<Integer, Double> en : sorted.entrySet()) { 
            System.out.println("Key = " + en.getKey() +  
                          ", Value = " + en.getValue()); 
        } */
	    
	    String attrName=activeUser.attribute((int)sorted.keySet().toArray()[0]).name();
	    String attrName1=activeUser.attribute((int)sorted.keySet().toArray()[1]).name();
	    String attrName2=activeUser.attribute((int)sorted.keySet().toArray()[2]).name();
	    System.out.println(attrName);
	    System.out.println(attrName1);
	    System.out.println(attrName2);
	    
	}
	
	static class RecommendationRecord implements Comparable <RecommendationRecord>
	{
		public double score;
		public String attributeName;
		
		public int compareTo(RecommendationRecord other)
		{
			if(this.score>other.score)
				return -1;
			if(this.score<other.score)
				return 1;
			return 0;
		}
		
		public String toString()
		{
			return attributeName+": "+score;
		}
	}
}
