
public class Edge {
	
	private final String destinationNode;
	private final String roadName;
	private final double naturalCost;
	private double predictionCost;
	private double actualCost;

	 public Edge (String destinationNode,String roadName,double naturalCost) {
	        this.destinationNode = destinationNode; 
	        this.roadName = roadName;
	        this.naturalCost = naturalCost; 
	    }

	public String getDestinationNode() {
		return destinationNode;
	}

	public String getRoadName() {
		return roadName;
	}

	public double getNaturalCost() {
		return naturalCost;
	}


	public double getPredictionCost() {
		return predictionCost;
	}

	public void setPredictionCost(double predictionCost) {
		this.predictionCost = predictionCost;
	}

	public double getActualCost() {
		return actualCost;
	}

	public void setActualCost(double actualCost) {
		this.actualCost = actualCost;
	}
	
}// class
