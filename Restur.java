package hello;

public class Restur {
	
	private String name;
    private String address;
    private String cuisines;
    private String rating;
    
    
    public Restur(String name,String address,String cuisines,String rating) {
    	this.name=name;
    	this.address=address;
    	this.cuisines=cuisines;
    	this.rating=rating;
    }
       
    /////////////
    public String getName() {
		return name;
	}
    public void setName(String name) {
		this.name = name;
	}
    ////////
    public String getaddress() {
		return address;
	}
    public void setaddress(String address) {
		this.address = address;
	}
    //////////
    public String getcuisines() {
  		return cuisines;
  	}
      public void setcuisines(String cuisines) {
  		this.cuisines = cuisines;
  	}
      
    /////////
    public String getrating() {
    		return rating;
    }
    public void setrating(String rating) {
    	this.rating = rating;
    }   
    
}