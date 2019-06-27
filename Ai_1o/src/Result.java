
public class Result {
	/*
	 * Helper class for the lrta* algorithm.
	 * Specifically Result is used for hashing the result[action,state] table.
	 */
	
	public Edge a;
	public String state;
	
	public Result(Edge a , String state){
		this.a = a;
		this.state=state;
	}
	
	//overriding the hashcode function so that the whole object's correct hashcode is returned and the elements are put in the correct bucket.
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((a == null) ? 0 : a.hashCode())
                + ((state == null) ? 0 : state.hashCode());
        return result;
    }
	
	//overriding the equals method so that 2 keys of the result hashmap are correctly compared.
	 @Override
	    public boolean equals(final Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        final Result other = (Result) obj;
	        if (a == null) {
	            if (other.a != null)
	                return false;
	        } else if (!a.equals(other.a))
	            return false;
	        if (state == null) {
	            if (other.state != null)
	                return false;
	        } else if (!state.equals(other.state))
	            return false;
	        return true;
	    }
}
