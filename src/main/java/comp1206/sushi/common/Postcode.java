package comp1206.sushi.common;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.JsonParser;
import comp1206.sushi.common.Postcode;

public class Postcode extends Model  implements Serializable {

	private String name;
	private Map<String,Double> latLong;
	private Number distance;
	private Restaurant restaurant;

	public Postcode(String code) {
		this.name = code;
		calculateLatLong();
		this.distance = Integer.valueOf(0);
	}
	
	public Postcode(String code, Restaurant restaurant) {
		this.name = code;
		calculateLatLong();
		calculateDistance(restaurant);
	}

	public void addRestaurant(Restaurant restaurant){
		this.restaurant = restaurant;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Number getDistance() {
		return this.distance;
	}

	public Map<String,Double> getLatLong() {
		return this.latLong;
	}
	
	public void calculateDistance(Restaurant restaurant) {
		//This function needs implementing
		Postcode destination = restaurant.getLocation();
		double earthRadius = 6371000; //meters
		double dLat = Math.toRadians(this.latLong.get("lat") - restaurant.getLocation().getLatLong().get("lat"));
		double dLng = Math.toRadians(this.latLong.get("lon") - restaurant.getLocation().getLatLong().get("lon"));
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.cos(Math.toRadians(this.latLong.get("lat"))) * Math.cos(Math.toRadians(restaurant.getLocation().getLatLong().get("lat"))) *
						Math.sin(dLng/2) * Math.sin(dLng/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		float dist = (float) (earthRadius * c);

		this.distance = dist;
	}

	public String attachCode(){
		Scanner scanner = new Scanner(this.getName());
		scanner.useDelimiter(" ");
		String str = scanner.next() + scanner.next();
		return str;
	}
	
	protected void calculateLatLong() {
		String url = "https://www.southampton.ac.uk/~ob1a12/postcode/postcode.php?postcode=" + attachCode();
		String json = null;
		try {
			json = new Scanner(new URL(url).openStream(), "UTF-8").useDelimiter("\\A").next();
		} catch (IOException e) {
			e.printStackTrace();
		}
		JsonParser jsonParser = new JsonParser();
		Scanner scanner = new Scanner(jsonParser.parse(json).toString());
		scanner.useDelimiter("\"");
		scanner.findInLine("\"lat\":");
		//This function needs implementing
		this.latLong = new HashMap<String,Double>();
		latLong.put("lat", Double.parseDouble(scanner.next()));
		scanner.findInLine("\"long\":");
		latLong.put("lon", Double.parseDouble(scanner.next()));
	}
	
}
