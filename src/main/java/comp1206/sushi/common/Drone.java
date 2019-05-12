package comp1206.sushi.common;

import comp1206.sushi.common.Drone;
import comp1206.sushi.server.Server;

import java.util.Map;

public class Drone extends Model implements Runnable {

	private Number speed;
	private Number progress;
	private int distanceToTarget;
	
	private Number capacity;
	private int battery;
	
	private String status;
	
	private Postcode source;
	private Postcode destination;

	private boolean threadIsActive;

	private Restaurant restaurant;

	private Server server;
	private StockManagement stockLevels;

	public Drone(Number speed) {
		this.setSpeed(speed);
		this.setCapacity(1);
		this.setBattery(100);
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	public void setServer(Server server) {
		this.server = server;
		stockLevels = new StockManagement(server);
	}

	public Number getSpeed() {
		return speed;
	}

	
	public Number getProgress() {
		if(progress == null)
			return null;
		if(progress.intValue() >= 100)
			return null;
		return progress;
	}
	
	public void setProgress(Number progress) {
		this.progress = progress;
	}
	
	public void setSpeed(Number speed) {
		this.speed = speed;
	}
	
	@Override
	public String getName() {
		return "Drone (" + getSpeed() + " speed)";
	}

	public Postcode getSource() {
		return source;
	}

	public void setSource(Postcode source) {
		this.source = source;
	}

	public Postcode getDestination() {
		return destination;
	}

	public void setDestination(Postcode destination) {
		this.destination = destination;
	}

	public Number getCapacity() {
		return capacity;
	}

	public void setCapacity(Number capacity) {
		this.capacity = capacity;
	}

	public Number getBattery() {
		return battery;
	}

	public void setBattery(Number battery) {
		this.battery = battery.intValue();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		notifyUpdate("status",this.status,status);
		this.status = status;
	}

	public void setThreadIsActive(boolean threadIsActive) {
		this.threadIsActive = threadIsActive;
	}

	@Override
	public void run() {
			System.out.println("Thread started " + Thread.currentThread());
			threadIsActive = true;
			battery = 100;
			this.setStatus("Idle");
			while(this.status.equals("Idle") && threadIsActive == true) {
				checkIngredients();
				checkOrders();
			}
		System.out.println("Thread " + Thread.currentThread() + " stopped");
	}

	public void checkIngredients(){
		for (Ingredient i : server.getIngredients())
			if(i.getStockAmount().intValue() < i.getRestockThreshold().intValue() && (i.isBeingRestock() == false)){
				i.setBeingRestock(true);
				restockIngredient(i);
				i.setBeingRestock(false);
		}
		this.setStatus("Idle");
	}

	public void checkIngredients(Dish dish) {
		for (Map.Entry<Ingredient, Number> entry : dish.getRecipe().entrySet()) {
			while ((entry.getKey().getStockAmount().intValue() < entry.getKey().getRestockThreshold().intValue())
					&& (entry.getKey().isBeingRestock() == false)) {
				entry.getKey().setBeingRestock(true);
				restockIngredient(entry.getKey());
				entry.getKey().setBeingRestock(false);
			}
		}
	}

	public void checkBattery(){
		if(battery <= 0){
			returnToChargingStation();
		}
	}

	public void returnToChargingStation(){
		this.source = source;
		this.destination = restaurant.getLocation();
		distanceToTarget = 0;
		while(distanceToTarget < destination.getDistance().intValue()){
			distanceToTarget += speed.intValue();
			try {
				progress = calculateProgress(distanceToTarget, destination.getDistance().intValue());
				this.setStatus("Going back to Charge");

				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		charge();
	}

	public void charge(){
		int timer = 120;
		while(timer > 0) {
			this.setStatus("Charging, " + timer + "sec. remaining");
			try {
				Thread.sleep(1000);
				timer--;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		battery = 100;
		this.setStatus("Idle");
	}

	public void checkOrders(){
		boolean status;
		for(Order o : server.getOrders()){
			if(o.isReady() == true && !(o.getStatus().equals("Delivering") || (o.getStatus().equals("Completed")))){
				if(threadIsActive) {
					o.setStatus("Delivering");
					travel(restaurant.getLocation(), o.getUser().getPostcode(), o);
					o.setStatus("Completed");
				}
			}
		}
		this.setStatus("Idle");
	}


	public void travel(Postcode source, Postcode destination, Ingredient ingredient){
		this.source = source;
		this.destination = destination;
		distanceToTarget = 0;
		while(distanceToTarget < destination.getDistance().intValue()){
			distanceToTarget += speed.intValue();
			try {
				progress = calculateProgress(distanceToTarget, destination.getDistance().intValue());
				this.setStatus("Getting " + ingredient.getName());

				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		distanceToTarget = 0;

		this.destination = source;
		this.source = destination;
		battery -= 10;
		checkBattery();

		while(distanceToTarget < destination.getDistance().intValue()){
			distanceToTarget += speed.intValue();
			try {
				progress = calculateProgress(distanceToTarget, destination.getDistance().intValue());
				this.setStatus("Traveling back to Restaurant");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if(threadIsActive)
			ingredient.restock(ingredient.getRestockAmount().intValue());
		else
			ingredient.setBeingRestock(false);
		this.destination = null;
		this.source = null;
		checkBattery();
		battery -= 20;

	}

	public void travel(Postcode source, Postcode destination, Order order){

		this.source = source;
		this.destination = destination;

		if(threadIsActive)
			for(Map.Entry<Dish, Number> entry : order.getOrder().entrySet())
				entry.getKey().pickUpDish(entry.getValue().intValue());

		distanceToTarget = 0;
		while(distanceToTarget < destination.getDistance().intValue()){
			distanceToTarget += speed.intValue();
			try {
				progress = calculateProgress(distanceToTarget, destination.getDistance().intValue());
				this.setStatus("Delivering to " + destination.getName());
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if(threadIsActive)
			order.setStatus("Completed");

		battery -=10;
		checkBattery();
		this.source = destination;
		this.destination = source;

		distanceToTarget = 0;
		while(distanceToTarget < destination.getDistance().intValue()){
			distanceToTarget += speed.intValue();
			try {
				progress = calculateProgress(distanceToTarget, destination.getDistance().intValue());
				this.setStatus("Traveling back to " + source.getName());
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		this.setStatus("Idle");
		this.destination = null;
		this.source = null;
		battery -=20;
		checkBattery();
	}

	public int calculateProgress(int droneLocation, int targetDistance){
		int totalDistance = (droneLocation*100)/targetDistance;
		return totalDistance;
	}

	public void restockIngredient(Ingredient ingredient){
		source = restaurant.getLocation();
		destination = ingredient.getSupplier().getPostcode();
		travel(source, destination, ingredient);
	}
}
