public class Road {
	/*
	 * helper class for better manipulation of the given roads in the file.
	 * for each road, a road object is created.
	 * this reserves some memory and may be a bad implementation for big graphs.
	 * startingPoint and endingPoint may be misleading variable names ,as the graph created is undirected.
	 */
	private String roadName;
	private String startingPoint;
	private String endingPoint;
	private double cost;
	
	public Road(String roadName , String startingPoint, String endingPoint , double cost){
		
		this.roadName = roadName;
		this.startingPoint = startingPoint;
		this.endingPoint = endingPoint;
		this.cost = cost;
	}

	//setters and getters
    public String getName() {
       return roadName;
    }
    public void setName(String roadName) {
        this.roadName = roadName;
    }
    public String getStartingPoint() {
        return startingPoint;
    }
    public void setStartingPoint(String startingPoint) {
        this.startingPoint =startingPoint ;
    }
    public String getEndingPoint() {
        return endingPoint;
    }
    public void setEndingPoint(String endingPoint) {
        this.endingPoint = endingPoint;
    }
    public double getCost() {
    	return cost;
    }
    public void setCost(double cost) {
	       this.cost = cost;
	}
}// class