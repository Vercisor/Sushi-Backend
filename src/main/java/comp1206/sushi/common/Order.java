package comp1206.sushi.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import comp1206.sushi.common.Order;

public class Order extends Model {

	private String status;
	private User user;
	private Map<Dish,Number> order;
	private boolean isReady;
	public Number price;

	public Order() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		this.name = dtf.format(now);
		order = new HashMap<>();
		status = "Waiting";
	}

	public Order(HashMap<Dish, Number> basket, User user){
		this.user = user;
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		this.name = dtf.format(now);
		order = basket;
		status = "Waiting";
	}
	
	public Order(User user) {
		this.user = user;
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		this.name = dtf.format(now);
		order = new HashMap<>();
		status = "Waiting";
	}

	public Map<Dish, Number> getOrder() {
		return order;
	}

	public void setOrder(Map<Dish, Number> order) {
		this.order = order;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Dish addDish(Dish dish, Number quantity){
		order.put(dish, quantity);
		return dish;
	}
	public Number getDistance() {
		return this.getUser().getDistance();
	}

	public void calculatePrice(){
		double sum = 0.0;
		for (Map.Entry<Dish, Number> entry : order.entrySet())
			sum += entry.getKey().getPrice().doubleValue() * entry.getValue().intValue();
		price = sum;
	}

	public void setReady(boolean ready) {
		isReady = ready;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public String getStatus() {
		return status;
	}

	public boolean isReady() {
		isReady = true;
		for(Map.Entry<Dish, Number> entry : order.entrySet()){
			if(!(entry.getKey().isInStock(entry.getValue().intValue()))){
				return false;
			}
		}
		return isReady;
	}


	public void setStatus(String status) {
		notifyUpdate("status",this.status,status);
		this.status = status;
	}

}
