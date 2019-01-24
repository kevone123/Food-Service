package hello;

import java.io.IOException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class GreetingController {

    @RequestMapping("/restaurants")
    public ResponseEntity<Greeting>greeting(@RequestParam(value="name", defaultValue="World")String name)throws IOException
    {
    	Greeting g= new Greeting();
    	Greeting error = new Greeting();
    	ResponseEntity<Greeting>res=new ResponseEntity<Greeting>(g,HttpStatus.OK);
    	
    	if(name.equals("World")) {
    		ResponseEntity<Greeting> badInput =new ResponseEntity<Greeting>(error,HttpStatus.BAD_REQUEST);
    		Restur error400 = new Restur("Http", "Status", "400, BAD_REQUEST", "Please"
    				+ " input a street address in the following form E.g "
    				+ "http://localhost:8080/restaurants?name=1600+Pensylvania+Ave+NW%2c+Washington+DC+20500");
    		error.getRestaurants().add(error400);
    		return badInput;
    	}
    	
    	String formattedName = name.replaceAll(" ", "+");
    	String trueName = formattedName.replaceAll(",", "%2c");
    	String geocodioUrl = "https://api.geocod.io/v1.3/geocode?q=";
    	geocodioUrl += trueName;
    	geocodioUrl += "&api_key=d3ac6bfdb55256d12c423f021516300ff5d5fbb";
    	RestTemplate geoTemplate = new RestTemplate();
    	
    	ResponseEntity<String> geoResponse = geoTemplate.getForEntity(geocodioUrl, String.class);
    	if(geoResponse.getStatusCodeValue() != 200) {
    		ResponseEntity<Greeting> geoFail = new ResponseEntity<Greeting>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    		Restur error500 = new Restur("Http", "Status", "500, INTERNAL_SERVER_ERROR",
    				"Geocodio had a problem.");
    		error.getRestaurants().add(error500);
    		return geoFail;
    	}
    	
    	ObjectMapper geoMapper = new ObjectMapper();
    	JsonNode geoRoot = geoMapper.readTree(geoResponse.getBody());
    	String results = geoRoot.get("results").toString();
    	int startOfLat = results.indexOf("{\"lat\":");
    	int endOfLng = results.lastIndexOf("},");
    	String latAndLng = results.substring(startOfLat, endOfLng);
    	int latStart = latAndLng.indexOf(":");
    	int latEnd = latAndLng.indexOf(",");
    	String lat = latAndLng.substring(latStart + 1, latEnd);
    	int lngStart = latAndLng.lastIndexOf(":");
    	String lng = latAndLng.substring(lngStart + 1);
    	
    	//need to  fix hardcoded geocodio
    	//need to format gretting response
    	
    	//Api call
    	RestTemplate restTemplate=new RestTemplate();
    	String zomatoUrl="https://developers.zomato.com/api/v2.1/geocode?";
    	zomatoUrl += "lat=" + lat + "&lon=" + lng;
    	
    	HttpHeaders headers= new HttpHeaders();
    	headers.add("user-key","7b3833f0223818dc577e51267cf5ec75");
    	
    	HttpEntity<String> entity= new HttpEntity<String>(headers);
    	//Response Enityt getForEntity
    	ResponseEntity<String> response= restTemplate.exchange(zomatoUrl, HttpMethod.GET, entity,String.class);
    	if(response.getStatusCodeValue() != 200) {
    		ResponseEntity<Greeting> zomatoFail = new ResponseEntity<Greeting>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    		Restur error500Zomato = new Restur("Http", "Status", "500, INTERNAL"
    				+ "_SERVER_ERROR", "Zomato had a problem.");
    		error.getRestaurants().add(error500Zomato);
    		return zomatoFail;
    	}

    	ObjectMapper mapper= new ObjectMapper();
    	JsonNode root=mapper.readTree(response.getBody());

    	JsonNode nearby= root.get("nearby_restaurants");
 
    	ObjectMapper RestObj= new ObjectMapper();
        //create an array of Objects, put each object int the string 
    	
    	for(JsonNode field: nearby) 
    	{

    		//Get One Name
        	
        	JsonNode nameboy=field.get("restaurant").get("name");
  
        	
        	String formatName=nameboy.toString();
        	
        	
        	//Get one address
        	
         	JsonNode addressboy=field.get("restaurant").get("location").get("address");
     		//System.out.println(addressboy.toString());
         	
          	String addressName=addressboy.toString();

     		//get one cuisine 
     		
     		JsonNode cuisineboy=field.get("restaurant").get("cuisines");

     		String cuisineName=cuisineboy.toString();
        	//get one rating
     		
     		JsonNode ratingboy=field.get("restaurant").get("user_rating").get("aggregate_rating");
     		
     		String ratingName=ratingboy.toString();

     		//Format it in Json to not include backspace
        	
        	Restur rest=new Restur(formatName, addressName,cuisineName, ratingName);
     		
        	g.getRestaurants().add(rest);	 	
    	}	
		return res;
    }
}