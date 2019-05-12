package comp1206.sushi.common;

import comp1206.sushi.common.Staff;
import comp1206.sushi.server.Server;

import java.util.Map;
import java.util.Random;

public class Staff extends Model implements Runnable {

	private String name;
	private String status;
	private int fatigue;
	private Server server;
	private StockManagement stockLevels;
	
	public Staff(String name) {
		this.setName(name);
		this.setFatigue(0);
	}

	public void setServer(Server server) {
		this.server = server;
		stockLevels = new StockManagement(server);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Number getFatigue() {
		return fatigue;
	}

	public void setFatigue(Number fatigue) {
		this.fatigue = fatigue.intValue();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		notifyUpdate("status",this.status,status);
		this.status = status;
	}

	@Override
	public void run() {
		this.setStatus("Idle");
		while(this.status.equals("Idle")) {
			checkDishes();
			checkOrders();
		}
	}

	public void checkDishes(){
		for (Dish d : server.getDishes()) {
			while (d.getStockAmount().intValue() < d.getRestockThreshold().intValue())
				prepareDish(d, d.getRestockAmount());
		}
		this.setStatus("Idle");
	}

	public void checkOrders(){
		for(Order o : server.getOrders())
			for(Map.Entry<Dish, Number> entry : o.getOrder().entrySet())
				if(entry.getValue().intValue() < entry.getKey().getStockAmount().intValue())
					prepareDish(entry.getKey(), 1);
	}

	public void prepareDish(Dish dish, Number quantity) {

		long timer = (long) (Math.random()  * ((60 - 20) + 1)) + 20;
		for(Dish d : server.getDishes()){
			if(d.getName().equals(dish.getName())){
				for(int x = 0; x < quantity.intValue(); x++) {
					checkIngredients(d);
					while (timer > 0) {
						try {
							this.setStatus("Cooking " + d.getName() + " in " + timer);
							Thread.sleep(1000);
							timer--;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					cookDish(d);
					timer = (long) (Math.random() * ((60 - 20) + 1)) + 20;
					fatigue += 10;
					checkFatigue();
				}
			}
		}
	}

	public void checkFatigue(){
		if(fatigue >= 100)
			this.takeBreak();
	}

	public void takeBreak(){
		int timer = 120;
		while(timer > 0) {
			this.setStatus("Taking a break, " + timer + "sec. remaining");
			try {
				Thread.sleep(1000);
				timer--;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		fatigue = 0;
		this.setStatus("Returning to work");
	}

	public void cookDish(Dish dish){
		for(Map.Entry<Ingredient, Number> entry : dish.getRecipe().entrySet()){
			entry.getKey().use(entry.getValue().intValue());
		}
		dish.cook();
	}

	public void checkIngredients(Dish dish){
		for(Map.Entry<Ingredient, Number> entry : dish.getRecipe().entrySet()){
			while(entry.getKey().getStockAmount().intValue() < entry.getKey().getRestockThreshold().intValue()){
				this.setStatus("Waiting for " + entry.getKey().getName());
			}
		}
	}
}
