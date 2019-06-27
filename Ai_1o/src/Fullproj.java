
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.charset.Charset;
import java.nio.file.*;


public class Fullproj{
	
	
	private static String input; //the file input
	private static String sourceText; //the starting point, parsed from the file
	private static String destinationText; //the destination, parsed from the file
	private static List<String> roadList; //a list of road names,connected nodes and natural costs, parsed from the file
	public static  List<String> predictionsDailyList; //a list of road names and predicted traffic, parsed from the file
	public static  List<String> actualsDailyList;//a list of road names and actual traffic, parsed from the file
	private static List<Road> roadObjList = new ArrayList<Road>();//a list of road objects , used for ease of manipulation (memory inefficient)
	private static Set<String> nodeSet; //a Set of unique nodes
	private static Graph graph; // the graph in which all the nodes and edges are stored (memory inefficient)
	private static double avgRoadCost ; //the average cost of all roads in the file (in the current implementation of the heuristic , it's not used)

	
	public static void main(String[] args) throws Exception
	{
		input = readFile("sampleGraph3.txt",Charset.defaultCharset()); //change that to give a different file as input

		roadParsing(); //parse roads
		roadCreation(); //create the road objects
		nodesNedgesCreation(); //create the graph representing the input file
		runFor80days(); //run IDDFS,IDA* and LRTA* for 80 days.
		
	}
	
	//Method to read from file , returns the whole input as a string
	static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
			}
	//Method to parse the text contained between the first occurrence of pattern given as @param input
	private static String parseSingle(String input, Pattern pattern)throws Exception {
	    Matcher matcher = pattern.matcher(input);
	    matcher.find();
	    return matcher.group(1);

	}
	
	//method that parses the roads along with the source and destination.
	public static void roadParsing()throws Exception{
		
		
		final Pattern source = Pattern.compile("<Source>(.+?)</Source>");
		sourceText = parseSingle(input,source).replaceAll("\\s","");
		
		final Pattern destination = Pattern.compile("<Destination>(.+?)</Destination>");
		destinationText = parseSingle(input,destination).replaceAll("\\s","");
		
		final Pattern roads = Pattern.compile("<Roads>(.+?)</Roads>",Pattern.DOTALL);
		String roadsText = parseSingle(input,roads);
		roadList =new ArrayList<String>(Arrays.asList(roadsText.replace("\n", ";").split("\\s*;\\s*")));
    	roadList.remove(0);
    	//System.out.println(roadList);
		
		
	}
	
	//runs IDDFS,IDA* and LRTA* for 80 days , calculates heuristics for each day and prints output in a text file.
	public static void runFor80days() throws Exception{
	
	
		final Pattern pred = Pattern.compile("<Predictions>(.+?)</Predictions>",Pattern.DOTALL);
		final Pattern atpd = Pattern.compile("<ActualTrafficPerDay>(.+?)</ActualTrafficPerDay>",Pattern.DOTALL);
		String textPredictionTags = parseSingle(input,pred);//get all text between prediction tags
		String textAtpdTags = parseSingle(input,atpd); //get all text between actual traffic tags
		Matcher matcherPred = Pattern.compile("<Day>(.+?)</Day>",Pattern.DOTALL).matcher(textPredictionTags); //matcher for a day of predictions
		Matcher matcherAtpd = Pattern.compile("<Day>(.+?)</Day>",Pattern.DOTALL).matcher(textAtpdTags); //matcher for a day of actual costs
		
		//-------------Lists used for the average cost of 80 days-------------//
		List<Double> actCostsListIDDFS = new ArrayList<>();
		List<Double> actCostListIdastar= new ArrayList<>();
		List<Double> actCostListLRTAstar= new ArrayList<>();
		//-------------------------------------------------------------------//
		
		
		File file = new File("output.txt");
		  
        //Create the file
        if (file.createNewFile()){
          System.out.println("File is created!");
        }else{
          System.out.println("Output file already exists.");
        }
         
        //Write Content
        FileWriter writer = new FileWriter(file);
        
		
		for(int i=0;i<80;i++){
			
			matcherPred.find();
			predictionsDailyList = new  ArrayList<String>(Arrays.asList(matcherPred.group(1).replace("\n", ";").split("\\s*;\\s*"))); //get the next day from the file
			predictionsDailyList.remove(0);
			
			matcherAtpd.find();
			actualsDailyList = new  ArrayList<String>(Arrays.asList(matcherAtpd.group(1).replace("\n", ";").split("\\s*;\\s*"))); //get the next day from the file
			actualsDailyList.remove(0);
			
			dailyCostAssignment(); //assign predicted and actual costs 
			calculate_heuristic(); //calculate the heuristic
		   
			System.out.println("---------------------------------------------\nInitiating IDDFS\n---------------------------------------------");
			IDDFS iddfs = new IDDFS(graph);
			long startTimeIddfs = System.nanoTime();
			List<String> iddfsOut=iddfs.iddfs(sourceText, destinationText);
			long endTimeIddfs = System.nanoTime();
			double durationIddfs = (double)(endTimeIddfs - startTimeIddfs)/1000000; //time in milliseconds
			actCostsListIDDFS.add(iddfs.totalActualCost);
		
			System.out.println("Expanded nodes : "+iddfs.expandCount +"\nTotal natural cost : "+iddfs.totalNaturalCost
				+"\nTotal prediction cost : "+iddfs.totalPredCost+"\nTotal actual cost : "+iddfs.totalActualCost);
	        System.out.println("Destination was found at depth : "+(iddfs.depth-1));
			System.out.println("Recommended path with predicted cost for each road : " +iddfsOut);
			System.out.println("IDDFS took ~" +durationIddfs +"ms~ to run");
			
			
			writer.write("---------------------------------------------\nInitiating IDDFS\n---------------------------------------------"
			+"\nExpanded nodes : "+iddfs.expandCount +"\nTotal natural cost : "+iddfs.totalNaturalCost
					+"\nTotal prediction cost : "+iddfs.totalPredCost+"\nTotal actual cost : "+iddfs.totalActualCost
					+"\nDestination was found at depth : "+(iddfs.depth-1)+"\nRecommended path with predicted cost for each road : " +iddfsOut
					+"\nIDDFS took ~" +durationIddfs +"ms~ to run");
	        

		
		  
			System.out.println("---------------------------------------------\nInitiating IDA*\n---------------------------------------------");
			IdAstar idastar = new IdAstar(graph);
			long startTimeIdastar = System.nanoTime();
			List<String> idastarOut=idastar.id_astar(sourceText, destinationText);
			long endTimeIdastar = System.nanoTime();
			double durationIdastar = (double)(endTimeIdastar - startTimeIdastar)/1000000; //time in milliseconds
			actCostListIdastar.add(idastar.totalActualCost);
		
			System.out.println("Expanded nodes : "+idastar.expandCount +"\nTotal natural cost : "+idastar.totalNaturalCost
				+"\nTotal prediction cost : "+idastar.totalPredCost+"\nTotal actual cost: "+idastar.totalActualCost);
			System.out.println("Recommended path with predicted cost for each road : " +idastarOut);
			System.out.println("IDA* took ~" +durationIdastar +"ms~ to run");
			
			writer.write("\n---------------------------------------------\nInitiating IDA*\n---------------------------------------------"
					+"\nExpanded nodes : "+idastar.expandCount +"\nTotal natural cost : "+idastar.totalNaturalCost
							+"\nTotal prediction cost : "+idastar.totalPredCost+"\nTotal actual cost : "+idastar.totalActualCost
							+"\nRecommended path with predicted cost for each road : " +idastarOut
							+"\nIDA* took ~" +durationIdastar +"ms~ to run");
			
			System.out.println("---------------------------------------------\nInitiating LRTA*\n---------------------------------------------");
			long startTimeLRTAstar = System.nanoTime();
			LRTAstar lrtastar = new LRTAstar(graph);
			List<String> lrtastarout=lrtastar.LRTAstar_repeat(sourceText, destinationText);
			long endTimeLRTAstar = System.nanoTime();
			double durationLRTAstar = (double)(endTimeLRTAstar - startTimeLRTAstar)/1000000; //time in milliseconds
			actCostListLRTAstar.add(lrtastar.totalActualCost);
		
			System.out.println("Expanded nodes : "+lrtastar.expandCount 
				+"\nTotal actual cost : "+lrtastar.totalActualCost);
			System.out.println("Path with actual cost for each road : " +lrtastarout);
			System.out.println("LRTA* took ~" +durationLRTAstar +"ms~ to run");
			
			writer.write("\n---------------------------------------------\nInitiating LRTA*\n---------------------------------------------"
					+"\nExpanded nodes : "+lrtastar.expandCount +"\nTotal actual cost : "+lrtastar.totalActualCost
							+"\nPath with actual cost for each road : " +lrtastarout
							+"\nLRTA* took ~" +durationLRTAstar +"ms~ to run");
		}
		
		double averageActual=0;
		ListIterator<Double> iter = actCostsListIDDFS.listIterator();
		while(iter.hasNext()){
			averageActual+=iter.next();
		}
		averageActual = averageActual/80;
		System.out.println("\n IDDFS average actual cost for 80 days : " +averageActual);
		writer.write("\n\n IDDFS average actual cost for 80 days : " +averageActual);
		
		averageActual=0;
		iter = actCostListIdastar.listIterator();
		while(iter.hasNext()){
			averageActual+=iter.next();
		}
		averageActual = averageActual/80;
		System.out.println("\n IDA* average actual cost for 80 days : " +averageActual);
		writer.write("\n IDA* average actual cost for 80 days : " +averageActual);
		
		averageActual=0;
		iter = actCostListLRTAstar.listIterator();
		while(iter.hasNext()){
			averageActual+=iter.next();
		}
		averageActual = averageActual/80;
		System.out.println("\n LRTA* average actual cost for 80 days : " +averageActual);
		writer.write("\n LRTA* average actual cost for 80 days : " +averageActual);
	    
		
		writer.close();
	}
	
	//creates a list of road objects for better manipulation but it is memory inefficient
	public static void roadCreation(){
		
		for(int i=0;i<roadList.size()/4;i++){ 
			Road road = new Road("","","",0.0);
			road.setName(roadList.get(4*i));
			road.setStartingPoint(roadList.get(4*i+1));
			road.setEndingPoint(roadList.get(4*i+2));
			road.setCost(Double.parseDouble(roadList.get(4*i+3)));
			roadObjList.add(road);
		}
	}

	/*
	 *  Method for the creation of a graph. (Bad for large files)
	 *  The input data extracted from the file are used to create a graph that contains all the nodes and edges(roads) of the given file.
	 *  Each node is assigned a List of edges.
	 *  Each edge is assigned its natural cost.
	 */

	public static void nodesNedgesCreation(){
		
		
        graph = new Graph();
        
		List<Edge> listemp = new ArrayList<Edge>();
		nodeSet = new HashSet<String>();
		ListIterator<Road> i = roadObjList.listIterator();
		while(i.hasNext()){
			Road roadTemp = i.next();
			if(!nodeSet.contains(roadTemp.getStartingPoint())){
				nodeSet.add(roadTemp.getStartingPoint());
			}
			if(!nodeSet.contains(roadTemp.getEndingPoint())){
				nodeSet.add(roadTemp.getEndingPoint());
			}
		}
		//System.out.println("Total Number of nodes in the file : " +nodeSet.size());
		
		for(String s  : nodeSet){;
			i = roadObjList.listIterator();
			while(i.hasNext()){
				Road roadTemp = i.next();
				if(roadTemp.getStartingPoint().equals(s)){
					Edge tempEdge = new Edge(roadTemp.getEndingPoint(),roadTemp.getName(),roadTemp.getCost());
					listemp.add(tempEdge);
				}
				if(roadTemp.getEndingPoint().equals(s)){
					Edge tempEdge = new Edge(roadTemp.getStartingPoint(),roadTemp.getName(),roadTemp.getCost());
					listemp.add(tempEdge);
				}
			}
			graph.addNode(s,listemp);
			listemp.clear();
		}
	}
	
	//Method for the update of the predicted cost and actual cost of each road.
	public static void dailyCostAssignment(){
		
		Iterator<Node> graphIt = graph.getGraph().values().iterator();
		while(graphIt.hasNext()){
			Node tempNode = graphIt.next();
			Iterator<Edge> edgeIt = tempNode.getOutgoingEdges().iterator();
			while(edgeIt.hasNext()){
				Edge edgeTemp = edgeIt.next();
				double costTempPred=0;
				ListIterator<String> predIterator = predictionsDailyList.listIterator();
				while(predIterator.hasNext()){
					String predTemp = predIterator.next();
					if(edgeTemp.getRoadName().equals(predTemp)){
						String predictionCost = predIterator.next();
						if(predictionCost.equals("low"))
							costTempPred = 0.6*edgeTemp.getNaturalCost()*0.9 +0.2*edgeTemp.getNaturalCost()+0.2*edgeTemp.getNaturalCost()*1.25;
						else if(predictionCost.equals("heavy"))
							costTempPred = 0.6*edgeTemp.getNaturalCost()*1.25+0.2*edgeTemp.getNaturalCost()+0.2*edgeTemp.getNaturalCost()*0.9;	
						else
							costTempPred = 0.6*edgeTemp.getNaturalCost()+0.2*edgeTemp.getNaturalCost()*1.25+0.2*edgeTemp.getNaturalCost()*0.9;
						break;
					}
				}
				double costTempAct=0;
				ListIterator<String> actIterator = actualsDailyList.listIterator();
				while(actIterator.hasNext()){
					String actTemp = actIterator.next();
					if(edgeTemp.getRoadName().equals(actTemp)){
						String actualCost = actIterator.next();
						if(actualCost.equals("low"))
							costTempAct = edgeTemp.getNaturalCost()*0.9;
						else if(actualCost.equals("heavy"))
							costTempAct = edgeTemp.getNaturalCost()*1.25;	
						else
							costTempAct = edgeTemp.getNaturalCost();
						break;
					}
				}
				edgeTemp.setPredictionCost(costTempPred);
				edgeTemp.setActualCost(costTempAct);
			}
		}
	}
	
	
	public static Map<String,Double> heuristicMap = new HashMap<>();;
	
	//sets the H of the destination to 0 and calls the recursive_heuristic method
	public static void calculate_heuristic(){
		
		Iterator<Road> roadIt = roadObjList.iterator();
		while (roadIt.hasNext()){
			avgRoadCost+=roadIt.next().getCost();
		}
		avgRoadCost = avgRoadCost/roadObjList.size();
		
		graph.getGraph().get(destinationText).setH(0.0);
		heuristicMap.put(destinationText, 0.0);
		
		recursive_heuristic(graph.getGraph().get(destinationText),1.0);
	
	}
	 //heuristic calculation
	 public static void recursive_heuristic(Node nodeData , double levelOfConnection ){
			
		if(levelOfConnection > 50) //instance terminating condition
			return;

		Map<String,Edge> neighborMap = new HashMap<String,Edge>();
		List<Edge> listOfEdges = nodeData.getOutgoingEdges();
		
		//find distinct destinations and keep only the roads with the lowest cost.
		ListIterator<Edge> i = listOfEdges.listIterator();
		     
		while(i.hasNext()) {
			Edge neighborTemp = i.next();
			if(!neighborMap.containsKey(neighborTemp.getDestinationNode())){
				neighborMap.put(neighborTemp.getDestinationNode(),neighborTemp);
			}
			else{
				if(neighborTemp.getPredictionCost() < neighborMap.get(neighborTemp.getDestinationNode()).getPredictionCost()){
					neighborMap.put(neighborTemp.getDestinationNode(),neighborTemp);
				}	 
			}
		}
	
		Iterator<Entry<String, Edge>> it = neighborMap.entrySet().iterator();
		while(it.hasNext()){
			
			Entry<String,Edge> tempEntry = it.next();
			Node neighborNode = graph.getGraph().get(tempEntry.getKey());
			
			
			//double tempHeur = 0.3*(levelOfConnection-1)*avgRoadCost +0.1*tempEntry.getValue().getPredictionCost(); //an idea
			
			double tempHeur = nodeData.getH() + 0.25*tempEntry.getValue().getPredictionCost(); //this is the formula for the h.
			if(heuristicMap.containsKey(neighborNode.getNodeId())){
				if(heuristicMap.get(neighborNode.getNodeId())>levelOfConnection){
					heuristicMap.put(neighborNode.getNodeId(),levelOfConnection);
					neighborNode.setH(tempHeur);
				}
			}
			else{
				heuristicMap.put(tempEntry.getKey(),levelOfConnection);
				neighborNode.setH(tempHeur);
				recursive_heuristic(neighborNode,levelOfConnection+1);
			}
		}
		return;
	}//recursive_heuristic

}// class