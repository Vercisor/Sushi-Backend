package comp1206.sushi.common;

import comp1206.sushi.common.Supplier;

import java.io.Serializable;

public class Supplier extends Model implements Serializable {

	private String name;
	private Postcode postcode;
	private Number distance;
	private Restaurant restaurant;

	public Supplier(String name, Postcode postcode) {
		this.name = name;
		this.postcode = postcode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Postcode getPostcode() {
		return this.postcode;
	}
	
	public void setPostcode(Postcode postcode) {
		this.postcode = postcode;
	}

	public void addRestaurant(Restaurant restaurant){
		this.restaurant = restaurant;
	}

	public Number getDistance(){
		calculateDistance(restaurant);
		return distance;
	}

	public void calculateDistance(Restaurant restaurant) {
		getPostcode().calculateLatLong();
		getPostcode().calculateDistance(restaurant);
		distance = postcode.getDistance();
	}

}
