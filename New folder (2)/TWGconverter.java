import java.io.*;
import java.util.*;

class TWGconverter{

	//create hashtable to store user behavior 
	private static LinkedHashMap<String, Integer> behav;
	public static void main(String[] args){
		if(args.length != 2){ //makes sure only two file are given to convert
			System.err.println("usage:java TGWconverter <pages.nosql> <products.nosql>");
			return;
		}

		try{ 	
			//populate hashtable with the available categories
			populateHash();
			//print hashtable to console - works 
			//printHash(behav);
			//read the pages.nosql file and extract info
			String input1 = args[0];
			pagesInfo(input1);
			//read the products.nosql file and extract info
			String input2 = args[1];
			productsInfo(input2);
			//append the user instance to the train.arff file
			String fileName = "train.arff";
        		File train = new File(fileName);  
        		try (FileWriter writer = new FileWriter(train, true)){
				//this boolean is to add the comma only after the first number been written out, i.e. 0, 5, 6, etc. 
				boolean firstTime = true;
				//for every category 
				for (String name: behav.keySet()){
					//get the count and write that to the arff file
					String value = behav.get(name).toString(); 
					if(firstTime == false) writer.write(",");
					writer.write(value);
					firstTime = false;  
				} 
			writer.write(System.getProperty( "line.separator" ));
			}
		
		}
		catch (Exception e){
			System.out.println(e);	
		}
	}

	//gets the relevant data about categories from the pages.nosql file
	public static void pagesInfo(String pages){
		try{ 
			//declare a reader to read the nosql file
			BufferedReader reader = new BufferedReader(new FileReader(pages));
			//read a line
			String line =reader.readLine();	
			//index -- keeps track of the index of the word categories/ in the string, 
			//Its either followed by '/' which is not useful or followed by a category related to the page being viewed, 
			//in which case, it is useful. 
			int index = -1;
			String category = "";
			int temp;
			//StringBuilder helps in building the letters of the category into one word
			StringBuilder s;
			while(line!=null){

				index = line.indexOf("categories/");
				//if the word category is mentioned				
				while(index!=-1){
					//check if there is category to read
					if(line.charAt(index + 11) != '\\'){
					//string builder to collect the letters of the category till you reach '/'
					s = new StringBuilder();
					//skip the word categories/ and read what's after
					temp = index + 11;
					//while there is characters left to read
					while(line.charAt(temp) != '/'){
						s.append(line.charAt(temp));
						temp++;
					}
					category = s.toString().toLowerCase();
					//find if this category exist in the hashmap
					if(behav.get(category) != null){
						//increment the count of this particular category
						behav.put(category, behav.get(category) + 1);
					}
					}
					//skip the current 'categories/' that was found and find the next one
					line = line.substring(index + 11);
					index = line.indexOf("categories/");

				}
				//read the next line in file
				line =reader.readLine();

			}
			//print current hashmap - for debugging purposes 
			//printHash(behav);
		}
		catch (Exception e){
			System.out.println(e);	
		}
	}
	
	//gets the relevant data about categories from the products.nosql file
	public static void productsInfo(String products){
		try{ 
			//declare a reader to read the nosql file
			BufferedReader reader = new BufferedReader(new FileReader(products));
			//read a line
			String line =reader.readLine();	
			//index -- keeps track of the index of the word categories":" in the string, 
			//Its always followed by the product category that the user interacted with 
			int index = -1;
			String category = "";
			int temp;
			//StringBuilder helps in building the letters of the category into one word
			StringBuilder s;
			while(line!=null){
				index = line.indexOf("category\":\"");
				//if the word category is mentioned				
				while(index!=-1){
					//string builder to collect the letters of the category till you reach '"'
					s = new StringBuilder();
					//skip the word categories/ and read what's after
					temp = index + 11;
					//while there is characters left to read
					while(line.charAt(temp) != '"'){
						s.append(line.charAt(temp));
						temp++;
					}
					category = s.toString().toLowerCase();
					//find if this category exist in the hashmap
					if(behav.get(category) != null){
						//increment the count of this particular category
						behav.put(category, behav.get(category) + 1);
					}
					
					//skip the current categories/ that was found and find the next one
					line = line.substring(index + 11);
					index = line.indexOf("category\":\"");

				}
				//read the next line in file
				line =reader.readLine();

			}
			//print current hashmap - for debugging purposes 
			//printHash(behav);
		}
		catch (Exception e){
			System.out.println(e);	
		}
	}

	//adds the warehouse categories to the behav hashmap with a default count of 0 for user interactions with these categories
	public static void populateHash(){
	behav = new LinkedHashMap<String, Integer>()
	{{
		put("activewearwomens", 0);
		put("dresses", 0);
		put("tops&sweartshirtswomens", 0);
		put("shirts&shorts", 0);
		put("jeanspants&leggings", 0);
		put("jacketswomens", 0);
		put("swimwearwomens", 0);
		put("sleepwearwomens", 0);
		put("lingeriesocks&tights", 0);
		put("maternity", 0);
		put("plussize", 0);
		put("accessorieswomens", 0);
		put("activewear", 0);
		put("tops&sweartshirts", 0);
		put("jeansshorts&pants", 0);
		put("accessories", 0);
		put("jackets", 0);
		put("underwear&sleepwear", 0);
		put("workwear", 0);
		put("swimwear", 0);
		put("mensshoes", 0);
		put("womensshoes", 0);
		put("kidsshoes", 0);
		put("baby&toddler", 0);
		put("youngerboys(3-7yrs)", 0);
		put("olderboys(8-16yrs)", 0);
		put("youngergirls(3-7yrs)", 0);
		put("oldergirls(8-16yrs)", 0);
		put("sleepwear", 0);
		put("handbags", 0);
		put("backpacks&sports", 0);
		put("suitcases&travel", 0);
		put("bracelets&bangles", 0);
		put("diamondjewellery", 0);
		put("watches", 0);
		put("earrings", 0);
		put("necklaces&pendants", 0);
		put("rings", 0);
		put("bedroom", 0);
		put("diningroom", 0);
		put("kidsroom", 0);
		put("lounge", 0);
		put("office", 0);
		put("outdoorfurniturefurniture", 0);
		put("bathroom", 0);
		put("laundry&cleaning", 0);
		put("bedding", 0);
		put("kitchen", 0);
		put("diningandtableware", 0);
		put("curtains&blinds", 0);
		put("homeacessories&décor", 0);
		put("kidshomewares", 0);
		put("rugs&mats", 0);
		put("cushions&throws", 0);
		put("kitchenappliances", 0);
		put("vacuumcleaners", 0);
		put("fansheaters&dehumidifiers", 0);
		put("lighting&lamps", 0);
		put("irons", 0);
		put("gardentools", 0);
		put("gardenpowertools", 0);
		put("sheds&greenhouses", 0);
		put("hoses&watering", 0);
		put("pots&planters", 0);
		put("plants&bulbs", 0);
		put("outdoorfurniture", 0);
		put("bbqs&accessories", 0);
		put("picnic&beach", 0);
		put("televisions", 0);
		put("dvd&blu-rayplayers", 0);
		put("accessories&mounts", 0);
		put("homeaudio", 0);
		put("headphones", 0);
		put("portablespeakers", 0);
		put("audioaccessories", 0);
		put("mobilephones", 0);
		put("mobileaccessories", 0);
		put("homephone", 0);
		put("gps&satnav", 0);
		put("laptops", 0);
		put("laptop&pcaccessories", 0);
		put("tablets&ipads", 0);
		put("tabletaccessories", 0);
		put("storage&hardrives", 0);
		put("printers&scanners", 0);
		put("digitalcameras", 0);
		put("instantcameras", 0);
		put("accessoriescameras", 0);
		put("playstation", 0);
		put("nintendo", 0);
		put("xbox", 0);
		put("pcgaming", 0);
		put("movies", 0);
		put("tvshows", 0);
		put("family&kids", 0);
		put("music&concerts", 0);
		put("documentaries", 0);
		put("pop", 0);
		put("complimations", 0);
		put("vinyl", 0);
		put("fiction", 0);
		put("cooking", 0);
		put("biographies", 0);
		put("mindbody&spirit", 0);
		put("non-fiction", 0);
		put("teenbooks", 0);
		put("kidsbooks", 0);
		put("actionfigures", 0);
		put("interactivepets", 0);
		put("blasters&nerf", 0);
		put("buildingblocks&lego", 0);
		put("dolls&accessories", 0);
		put("games&puzzles", 0);
		put("pre-schooltoys", 0);
		put("softtoys", 0);
		put("vehicle&remotecontrol", 0);
		put("kites", 0);
		put("rideontoys", 0);
		put("beach&waterfun", 0);
		put("starwars", 0);
		put("lego", 0);
		put("pawpatrol", 0);
		put("hatchimal", 0);
		put("lol", 0);
		put("barbie", 0);
		put("nerf", 0);
		put("maybelline", 0);
		put("covergirl", 0);
		put("rimmel", 0);
		put("colourco", 0);
		put("nailcare", 0);
		put("makeup&toiletrybags", 0);
		put("tools&accessories", 0);
		put("bathaccessories", 0);
		put("bodycream&lotion", 0);
		put("bodywash&showergel", 0);
		put("barsoap", 0);
		put("mensfragrances", 0);
		put("womensfragrances", 0);
		put("celebrityfragrances", 0);
		put("computers", 0);
		put("speakers", 0);
		put("gadgets", 0);
		put("tablets", 0);
		

	}};


	}
	//a linkedHashmap is used to preserve order
	//this method prints the hashmap of categories with their corresponding counts
	public static void printHash(LinkedHashMap<String, Integer> behav){
		for (String name: behav.keySet()){
		    String key =name.toString();
		    String value = behav.get(name).toString();  
		    System.out.println(key + " " + value);  
		} 
	}
}

