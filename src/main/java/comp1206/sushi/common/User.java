package comp1206.sushi.common;

import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.User;

import java.util.ArrayList;
import java.util.List;

public class User extends Model {
	
	private String name;
	private String password;
	private String address;
	private Postcode postcode;
	private Number distance;
	private Restaurant restaurant;

	public User(String username, String password, String address, Postcode postcode) {
		this.name = username;
		this.password = password;
		this.address = address;
		this.postcode = postcode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	public Number getDistance() {
		calculateDistance(restaurant);
		return postcode.getDistance();
	}

	public void calculateDistance(Restaurant restaurant) {
		getPostcode().calculateLatLong();
		getPostcode().calculateDistance(restaurant);
		distance = postcode.getDistance();
	}

	public Postcode getPostcode() {
		return this.postcode;
	}
	
	public void setPostcode(Postcode postcode) {
		this.postcode = postcode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
