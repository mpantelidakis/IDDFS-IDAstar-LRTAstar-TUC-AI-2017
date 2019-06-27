import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

public class IdAstar {
	
	public double bound; //the allowed bound for an iteration of the ida*
	
	private Object found; 
	
	public double totalPredCost; 
    public double totalNaturalCost;
    public double totalActualCost;
   
    public int expandCount; // number of expanded nodes

    private List<String> path ; //the path list to be returned
	
	private final Graph graph; //the graph in which all the data is stored
	
	public String dest;



    public IdAstar (Graph graph) {
        this.graph = graph;
        this.bound = 0.0;
        this.found = "0";
    }


    
	 public List<String> id_astar(String source,String destination)throws Exception{
	    	
		 	dest = destination;
		 	Node sourceNodeData = graph.getGraph().get(source);
	        bound = sourceNodeData.getH();
	        
	        while (!found.equals("found"))
	        {
	        	//-----------------Initializations for the new iteration--------------//
	        	
	        	expandCount=0;
	        	totalActualCost=0;
	        	totalNaturalCost=0;
	        	totalPredCost=0;
	        	path = new ArrayList<>();
	        	//-------------------------------------------------------------------//
	        	
	        	found = search(sourceNodeData, 0, bound);
	        	if (!found.equals("found")){
	        		bound = (Double)found;
	        	}

	          
	
	        }
	        Collections.reverse(path); // reverse the path list 
	        return path;
	    }
    
	 /**
	     * ---------------------------------------------------
	     * 			Recursive bounded search 
	     * ---------------------------------------------------
	     * 
	     * @brief
	     * 
		 * @param nodeData     the candidate node object
	     * @param g		       the cumulative g
	     * @param bound        the allowed bound for expansion
	     * @return found if the destination was reached , else the minimum f that exceeded the allowed bound.
	     * 
	     * ------------------------------------------------------------------------------------------------------------------------------
	     * Sets the g and f of the node,checks if the node is the destination. If yes , returns found.
	     * If the f of the node is gr8er than the allowed bound, that instance of the recursion is terminated and that node's f is returned.
	     * After all instances of the recursion are terminated,the minimum f that exceeded the allowed bound is returned
	     * The algorithm is called again ,starting from the root node and the process is repeated
	     * ------------------------------------------------------------------------------------------------------------------------------
	     *  Unlike A*, IDA* does not utilize dynamic programming and therefore often ends up exploring the same nodes many times.
	     * @brief
	     */

	 Object search(Node nodeData, double g , double bound) {
	
		 Object result;
		 
		 nodeData.setG(g);
		 nodeData.setF();
	
		 if(nodeData.getNodeId().equals(dest)){
			 return "found";
		 }
		 
		 if(nodeData.getF()>bound){
			 return nodeData.getF();
		 }

		 expandCount++;
        
         Map<String,Edge> neighborMap = new HashMap<String,Edge>(); //This hashmap keeps only the most prominent road , connecting two nodes.
         ListIterator<Edge> i = nodeData.getOutgoingEdges().listIterator();
         
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
         
         Double min = Double.MAX_VALUE; //initialization of min
         
         Iterator<Entry<String, Edge>> it = neighborMap.entrySet().iterator();
         while(it.hasNext()){ //for each outgoing edge
        	 
        	 Edge neighbor = it.next().getValue();
	         Node neighborNode = graph.getGraph().get(neighbor.getDestinationNode());
	         double cumulativeG = neighbor.getPredictionCost() + nodeData.getG();
	         result = search(neighborNode,cumulativeG,bound);
	        
	         if (result.equals("found")){
				totalNaturalCost+=neighbor.getNaturalCost();
				totalPredCost+=neighbor.getPredictionCost();
				totalActualCost+=neighbor.getActualCost();
				path.add(String.valueOf(neighbor.getPredictionCost()));
				path.add(neighbor.getRoadName());
				return "found";
	         }
	         if ((Double)result<min) //is result less than the previous min?
	        	 min = (Double)result;  //min is assigned  the result , which is an f lower than the previous min
         }
         return min; 
 }

	 



	

}// class
