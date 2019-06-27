
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;


public class IDDFS

{
    private final Graph graph; //the graph in which all the data is stored
    private Node found; //helper for the termination of dls
    //--- The costs to be printed ---// 
    public double totalPredCost; 
    public double totalNaturalCost;
    public double totalActualCost;
    //------------------------------// 
    
    public int expandCount; // number of expanded nodes
    public int depth; //depth for which the dls is called , public so it can be retrieved from Fullproj class and printed.

 
    private List<String> path ; //the path list to be returned
    private Map<String,Integer> visited; // the hashmap that serves as memory for the algorithm

    
    //Initialization//
    public IDDFS(Graph graph){
    	this.graph = graph;
    	found=null;
    }

    
    public List<String> iddfs(String source,String destination){
    	
        depth = 0; //Initial value for the depth dls will run.
        while (found == null)
        {
        	//-----------------Initializations for the new iteration--------------//
        	expandCount=0;
        	totalActualCost=0;
        	totalNaturalCost=0;
        	totalPredCost=0;
        	path= new ArrayList<>();
        	visited= new HashMap<String,Integer>();
        	//-------------------------------------------------------------------//
        	
            found = depth_limited_search(graph.getGraph().get(source), destination, depth); //run dls
            depth++; //next iteration will run dls for a gr8er depth

        }
        Collections.reverse(path); // reverse the path list 
        return path;
    }
    
    /**
     * ---------------------------------------------------
     * 			Recursive depth-limited search 
     * with a slight memory mod to contain node expansions
     * ---------------------------------------------------
     * 
     * @brief
     * 
     * Performs dfs by recursively calling itself.
     * 
	 * @param nodeData     the candidate node object
     * @param destination  the destination NodeId
     * @param depth2run    the allowed depth for expansion
     * @return found if the destination was reached , else null.
     * 
     * Once it reaches the allowed depth , checks if the destination is found. If so it assigns the destination node to "found"
     * If the destination is not found , null is returned from that specific instance of the recursion.
     * If all instances return null , then it means the destination should be deeper in the graph.(dls must be called with a gr8er value of depth2run)
     * If one instance returns found , then all others return found too , and the recommended path is created.
     * ------------------------------------------------------------------------------------------------------------------------
     * ~Normally IDDFS doesn't avoid repeated states and hence the number of expansions is big~
     * In order to somehow contain the number of expanded nodes , i used a hashmap (visited) in which i stored the NodeId (key) 
     * along with the gr8est @param depth2run for which it can be expanded(value).
     * The principle is quite simple , if the node is going to be expanded for a gr8er @param depth2run ,don't expand him for recursions with smaller @param depth2run.
     * By applying this , expanded nodes dropped to roughly 1/3 of the initial expansions.
     * ------------------------------------------------------------------------------------------------------------------------
     * There still is room for improvement , as the expansions are still more than needed..
     * This happens due to the fact that if a node is encountered for the first time with an allowed @param depth2run gr8er than 0 , the algorithm automatically expands it.
     * The algorithm can't know if that node will be found l8er from a different path with a gr8er @param depth2run than the previous. 
     * Result is that node will be expanded again. This is necessary evil , as the second expansion will run for higher depth , and therefore is more prominent.
     * ------------------------------------------------------------------------------------------------------------------------
     * I tried containing this phenomenon by not allowing the node to expand for a second time (even with gr8er allowed depth).
     * Expansions dropped dramatically but the solution was found deeper in the graph due to the fact that 
     * the function returned and was called again from function iddfs for a gr8er depth. 
     * As a result i rolled back to the previous implementation.
     * ------------------------------------------------------------------------------------------------------------------------
     * Finally , in case there are more than one roads connecting node A to node B , the algorithm chooses the road with the lowest predicted cost.
     * ------------------------------------------------------------------------------------------------------------------------
     * @brief
     */
    public Node depth_limited_search(Node nodeData,String destination, int depth2run)
    {

    	if(nodeData.getNodeId().equals(destination) && (depth2run==0)){ //terminating condition
    		found = nodeData;
			return found;
        }
    	else if (depth2run>0){
    		
    		if(!visited.containsKey(nodeData.getNodeId())){
         		visited.put(nodeData.getNodeId(),depth2run);
     		}
         	else{
         		if(visited.get(nodeData.getNodeId())>depth2run){ //terminating condition
         			//The node will be expanded l8er for a more prominent depth , return null
         			return null; //comment this return for IDDFS without the memory mod
         		}
         	}
    		
    		expandCount++; //Expands +1

    		Map<String,Edge> neighborMap = new HashMap<String,Edge>();   // a hashmap containing all the Nodes that we can travel to from node nodeData , along with the edges that lead to them.
            ListIterator<Edge> i = nodeData.getOutgoingEdges().listIterator();
            
            while(i.hasNext()) {  

            	//If there are more than one roads connecting point A to point B , choose the cheapest one based on the predicted cost.
            	Edge neighborTemp = i.next();
				if(!neighborMap.containsKey(neighborTemp.getDestinationNode())){
					neighborMap.put(neighborTemp.getDestinationNode(),neighborTemp);
				}
				else{
					//there already is a road connecting these 2 Nodes
					if(neighborTemp.getPredictionCost() < neighborMap.get(neighborTemp.getDestinationNode()).getPredictionCost()){ //if one road's predicted cost is lower than the others' 
						neighborMap.put(neighborTemp.getDestinationNode(),neighborTemp);// put the first one in the hashmap
					}
				}
            }
            
            Iterator<Entry<String, Edge>> it = neighborMap.entrySet().iterator();
            while(it.hasNext()){ 
            	//for each outgoing edge from the Node , put the destination of that edge , along with the allowed depth in the visited hashmap
            	//see @brief for more info
            	String tempEntry = it.next().getKey();
	   	  
		   	    if(!visited.containsKey(tempEntry)){
		     		visited.put(tempEntry,depth2run-1);
		 		}
            }
            it = neighborMap.entrySet().iterator();
            while(it.hasNext()){
            	//now for all neighbor nodes , call the function again
            	Entry<String,Edge> tempEntry = it.next();
	           	Edge toNeighborEdge = tempEntry.getValue();
	   	        Node neighborNode = graph.getGraph().get(tempEntry.getKey());
	   	        
	   	        found = depth_limited_search(neighborNode,destination,depth2run-1); //dls is called again
	   	        
	   	       
	   	        if (found!=null){ //terminating condition
	   	            totalNaturalCost+=toNeighborEdge.getNaturalCost();
					totalPredCost+=toNeighborEdge.getPredictionCost();
					totalActualCost+=toNeighborEdge.getActualCost();
					path.add(String.valueOf(toNeighborEdge.getPredictionCost()));
					path.add(toNeighborEdge.getRoadName());
	 				return found;
	   	        }
            }
    	}
    	return null; //the allowed depth was reached and the destination was not found, return
    }
  

        
}// class