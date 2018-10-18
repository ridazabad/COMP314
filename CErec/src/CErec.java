import weka.core.*;
import weka.core.neighboursearch.*;
import java.io.*;

import java.util.*;

public class CErec 
{
	//References: http://csci.viu.ca/~barskym/teaching/DM2012/labs/LAB5/Lab%205.html
	
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
		boolean debug2 = false;
		if(args.length<2)
		{
			System.out.println("Usage: java CErec <training file name> <active user file name>");
			System.exit(0);
		}
	
		
		Instances dataset=null;
		Instances activeUsers=null;
		Instance activeUser=null;
		
		//read user data from training file
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
		//read user data from active user file
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
		//plot users in Linear K Nearest Neighbour algorithm
		LinearNNSearch kNN=new LinearNNSearch(dataset);
		Instances neighbors=null;
		double [] distances=null;
		
		try
		{
			//get the 20 nearest neighbours to the active user and their Euclidean distances
			neighbors=kNN.kNearestNeighbours(activeUser, 20);
			distances=kNN.getDistances();
		}
		catch(Exception e)
		{
			System.out.println("Neighbors could not be found.");
			return;
		}
		
		//calculate a weighted similarity score for the neighbours based on the distance from the active user
		double [] similarities=new double[distances.length];
		for(int i=0;i<distances.length;i++)
		{
			similarities[i]=1.0/distances[i];
			if(debug2) System.out.println(distances[i]);
			if(debug2) System.out.println(similarities[i]);
		}
		
		if (debug2) System.out.println(neighbors);
		
		//look at neighbours one at a time
		Enumeration nInstances=neighbors.enumerateInstances();
		//one entry per category
		Map <String,List<Integer>> recommendations=new HashMap <String,List<Integer>>();

		//loop through nearest neighbours
		while(nInstances.hasMoreElements())
		{
			Instance currNeighbor=(Instance)nInstances.nextElement();
			//adding recommended categories to the list
			//loop through every attribute for the neighbour
			for(int i=0;i<currNeighbor.numAttributes();i++)
			{
				if(debug2) System.out.println(activeUser.value(i));
				//if user has NOT interacted with the category, 0 if the active user did not interact, >0 if they did
				if(activeUser.value(i)<1 )
				{
					//retrieve the name of the category
					String attrName=activeUser.attribute(i).name();
					List<Integer> lst=new ArrayList <Integer>();
					if(recommendations.containsKey(attrName))
					{
						lst=recommendations.get(attrName);
					}
					
					if( currNeighbor.value(i)>0) //if the neighbour interacted with this category
					{
						int y = (int)currNeighbor.value(i);
						if(debug2) System.out.println(y);
						lst.add(y);
					}
					else
					{
						
						//add zero for this neighbour if they did not interact with this category
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
		//loop through every category the user has not interacted with but the nearest neighbours have and calculate a score
		while(it.hasNext())
		{
			String atrName=it.next();
			double totalImpact=0;
			double weightedSum=0;
			List <Integer> ranks=recommendations.get(atrName); //the click event information of each neighbour for the category
			//calculating score for every category
			//sum the similarity of each neighbour who interacted with this category 
			for(int i=0;i<ranks.size();i++)
			{ 
				int val=ranks.get(i);
				totalImpact+=similarities[i];
				weightedSum+=(double)similarities[i]*val;
				
			}
			//creating a new Record to store the score of each category
			RecommendationRecord rec=new RecommendationRecord();
			rec.attributeName=atrName;
			rec.score=weightedSum/totalImpact;
			
			finalRanks.add(rec);
		}
		Collections.sort(finalRanks); //sort categories based on score in descending order
		
		//print top 7 categories the ActiveUser has NOT interacted with, that they will most likely interact with
		System.out.println("Recommended categories user has not interacted with");
		System.out.println("---------------------------------------------------");
		System.out.println( finalRanks.get(0));
		System.out.println( finalRanks.get(1));
		System.out.println( finalRanks.get(2));
		System.out.println( finalRanks.get(3));
		System.out.println( finalRanks.get(4));
		System.out.println( finalRanks.get(5));
		System.out.println( finalRanks.get(6));

		//Find the top categories the user interacted with the most and store in mostClicked
		HashMap <Integer,Double> mostClicked=new HashMap <Integer,Double>();
		double[] da = activeUser.toDoubleArray();
		for(int j =0; j<da.length; j++){
			mostClicked.put(j, da[j]);
		}

	    Map<Integer, Double> sorted = sortByValue(mostClicked);
	    
	    String Category1=activeUser.attribute((int)sorted.keySet().toArray()[0]).name();
	    String Category2=activeUser.attribute((int)sorted.keySet().toArray()[1]).name();
	    String Category3=activeUser.attribute((int)sorted.keySet().toArray()[2]).name();
	    
	    //print top 3 categories the ActiveUser HAS interacted with
	    System.out.println("\nRecommended categories user has interacted with");
		System.out.println("---------------------------------------------------");
	    System.out.println(Category1);
	    System.out.println(Category2);
	    System.out.println(Category3);
	    
	}
	//internal class to keep score of each category
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
