

import java.util.ArrayList;
import java.util.List;


final class Node { 

    private final String nodeId; //the name of the node
    private final List<Edge> outgoingEdges; // a list of the edges of that node (outgoing may be misleading , as this is an undirected graph)
    private double g;  // g is the cumulative path cost from the source
    private double h;  // h is the heuristic from destination.
    private double f;  // f = g + h 


    public Node (String nodeId,List<Edge> outgoingEdges) {
    	this.h = Double.MAX_VALUE;
    	this.g = Double.MAX_VALUE; 
        this.nodeId = nodeId;
        this.outgoingEdges = new ArrayList<Edge>(outgoingEdges);
    }
    
	//getters and setters 
    public String getNodeId() {
        return nodeId;
    }

	public List<Edge> getOutgoingEdges() {
		return outgoingEdges;
	}
	
	public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getF() {
        return f;
    } 
    public void setF() {
        this.f = this.g + this.h;
    } 

    public double getH() {
        return h;
    }
    public void setH(double h) {
    	this.h = h;
    }


	
}// class