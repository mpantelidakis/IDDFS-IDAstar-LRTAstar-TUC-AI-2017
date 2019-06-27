import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;


public class LRTAstar {

    public double totalActualCost; //the sum of the actual costs of the roads followed
    public boolean found; // global boolean (true if destination is found , false otherwise)
    public String dest; //a global variable for the destination
    public String src;//a global variable for the source
    public int expandCount; // number of expanded nodes
    private List<String> path ; //the path list to be returned
	private final Graph graph; //the graph in which all the data is stored
	
	private static Map <String , Double > heuristicTable; //the static H(s) of the algorithm , initially empty
	private static Map <Result , String > result; // the static result[Result[action,state],String] hashmap for the mapping of the statespace, initially empty
	private static String prevState ; //static previous state , initially null
	private static Edge prevAction; // static previous action , initially null
	
	
	 public LRTAstar (Graph graph) {
		 this.graph = graph;
		 LRTAstar.heuristicTable = new HashMap<>();
		 LRTAstar.result = new HashMap<>();
		 LRTAstar.prevState = null;
		 LRTAstar.prevAction = null;
		 this.found = false;
		 this.path = new ArrayList<>();
		 this.totalActualCost = 0;
		 this.expandCount=0;

	    }
	 
	 public List<String> LRTAstar_repeat(String source,String destination){
		 
		 dest = destination;
		 src = source;
		 
		 Edge tmp = LRTAstar_agent(source);
		 path.add(tmp.getRoadName());
		 path.add(String.valueOf(tmp.getActualCost()));
		 totalActualCost+=tmp.getActualCost();
		 
		 while(found==false){
			 
			 tmp = LRTAstar_agent(tmp.getDestinationNode());
			 if(tmp==null){
				 
				 return path; //the path returned contains all the actions the agent tried until it found the destination
			 }
			 path.add(tmp.getRoadName());
			 path.add(String.valueOf(tmp.getActualCost()));
			 totalActualCost+=tmp.getActualCost();
			 
		 }
		 return null;
	 }
	
	 /**
	     * ---------------------------------------------------
	     * 			LRTAstar_agent
	     * ---------------------------------------------------
	     * 
	     * @brief
	     * 
		 * @param stonos        == s'== the agent's current state
	     * @return prevAction   the action the agent will take
	     * --------------------------------------------------------------------------------
	     * The agent learns a "map" of the environment-more precisely, the outcome of each 
	     * action in each state-simply by recording each of its experiences.
	     * Moreover the agent acquires more accurate estimates of the value
	     * of each state by using local updating rules.
	     * --------------------------------------------------------------------------------
	     * Starting with an initially empty map of the statespace and and empty H hashmap
	     * the agent returns an action -minimizing LRTA_cost function- to be taken.
	     * After taking the action , visits a new state.Updates the H of the previous state
	     * and the map of the statespace.
	     * @brief
	     */
	
	public Edge LRTAstar_agent(String stonos){
		
		Node currentNode = graph.getGraph().get(stonos);
		
		expandCount++; //expands +1
		
		if(!heuristicTable.containsValue(currentNode.getNodeId())) //making the H(s')
			heuristicTable.put(stonos, currentNode.getH());
		
		if(prevState!= null){
			result.put(new Result(prevAction,prevState),stonos); //result[a,s]<--s'
			List<Double> listOfCosts = new ArrayList<>();
			ListIterator<Edge> i = graph.getGraph().get(prevState).getOutgoingEdges().listIterator();
			while(i.hasNext()) {
		       	 Edge b = i.next();
		       	 listOfCosts.add(LRTAstar_cost(prevState,b,result.get(new Result(b,prevState))));
	        }
			heuristicTable.put(prevState,Collections.min(listOfCosts)); //update the H(s) static heuristic of the previous state
		}
		
		if (stonos.equals(dest)){ //termination condition , destination was found
			found = true;
			return null;
		}
		
		Map <Edge,Double> edgeNcost = new HashMap<>(); //a hashmap storing an action b and the LRTAstar_cost(s',b,result[b,s'])
		ListIterator<Edge> it = currentNode.getOutgoingEdges().listIterator();
		while(it.hasNext()) {
	       	 Edge b = it.next();
	         edgeNcost.put(b,LRTAstar_cost(stonos,b,result.get(new Result(b,stonos))));   
        }
		
		//sort the edgeNcost hashmap , so that the action returned is the one that minimizes the LRTAstar_cost function.
		prevAction = edgeNcost.entrySet().stream().min((a,b) -> a.getValue().compareTo(b.getValue())).get().getKey();
		prevState=stonos; //s = s'
		
		return prevAction;
	}// function LRTAstar_agent
	
	
	/*
	 * stonos == s'
	 * If the current state is not yet set in the result hashmap , return the h of the prevState
	 * else , return the action's cost that led to the current state + the H(s')
	 */
	public double LRTAstar_cost(String s,Edge a,String stonos){
		
		if(stonos == null){
			return graph.getGraph().get(s).getH();
		}
		else{
			return a.getActualCost()+heuristicTable.get(stonos);
		}
	}//function LRTAstar_cost
	
}// class
