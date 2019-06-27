
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
	
	private Map<String,Node> graph ;
	
	public Graph(){
		this.graph= new HashMap<String,Node>();
	}
	
	//adds a node to the current instance of graph
	public void addNode(String nodeId,List<Edge> outgoingEdges){
		this.graph.put(nodeId,new Node(nodeId, outgoingEdges));
	}
	
	//getter for the current instance of the graph
	Map<String,Node> getGraph(){
		return graph;
	}
	
	
}// class
