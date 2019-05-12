package comp1206.sushi.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comp1206.sushi.common.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Client implements ClientInterface {

    private static final Logger logger = LogManager.getLogger("Client");
	public Restaurant restaurant;
	public ArrayList<Dish> dishes = new ArrayList<Dish>();
	public ArrayList<Drone> drones = new ArrayList<Drone>();
	public ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
	public ArrayList<Order> orders = new ArrayList<Order>();
	public ArrayList<Staff> staff = new ArrayList<Staff>();
	public ArrayList<Supplier> suppliers = new ArrayList<Supplier>();
	public ArrayList<User> users = new ArrayList<User>();
	public ArrayList<Postcode> postcodes = new ArrayList<Postcode>();
	public ArrayList<UpdateListener> listeners = new ArrayList<UpdateListener>();
	HashMap<Dish, Number> basket = new HashMap<>();
	CommsClient commsClient;
	
	public Client() {
        logger.info("Starting up client...");
         commsClient = new CommsClient(2239, "localhost", this);
         Thread clientThread = new Thread(commsClient);
         clientThread.start();


		Postcode restaurantPostcode = new Postcode("SO17 1BJ");
		restaurant = new Restaurant("Mock Restaurant",restaurantPostcode);

		postcodes.add(new Postcode("SO17 1TJ"));
		postcodes.add(new Postcode("SO17 1BX"));
		postcodes.add(new Postcode("SO17 2NJ"));

		users.add(new User("admin", "admin","12 Winchester Street", postcodes.get(0)));

		/*
		Postcode postcode4 = new Postcode("SO17 1TW");
		Postcode postcode5 = new Postcode("SO17 2LB");
		*/

		suppliers.add(new Supplier("Supplier 1",postcodes.get(0)));
		suppliers.add(new Supplier("Supplier 2",postcodes.get(1)));
		suppliers.add(new Supplier("Supplier 3",postcodes.get(2)));

		ingredients.add(new Ingredient("Ingredient 1","grams",suppliers.get(0),1,5,1));
		ingredients.add(new Ingredient("Ingredient 2","grams",suppliers.get(1),1,5,1));
		ingredients.add(new Ingredient("Ingredient 3","grams",suppliers.get(2),1,5,1));

		dishes.add(new Dish("Dish 1","Dish 1",1,1,10));
		dishes.add(new Dish("Dish 2","Dish 2",2,1,10));
		dishes.add(new Dish("Dish 3","Dish 3",3,1,10));

		dishes.get(0).addIngredient(ingredients.get(0),1);
		dishes.get(0).addIngredient(ingredients.get(1),2);
		dishes.get(1).addIngredient(ingredients.get(1),3);
		dishes.get(1).addIngredient(ingredients.get(2),1);
		dishes.get(2).addIngredient(ingredients.get(0),2);
		dishes.get(2).addIngredient(ingredients.get(2),1);

		staff.add(new Staff("Staff 1"));
		staff.add(new Staff("Staff 2"));
		staff.add(new Staff("Staff 3"));

		drones.add(new Drone(1));
		drones.add(new Drone(2));
		drones.add(new Drone(3));
	}
	
	@Override
	public Restaurant getRestaurant() {

		return restaurant;
	}
	
	@Override
	public String getRestaurantName() {
		return restaurant.getName();
	}

	@Override
	public Postcode getRestaurantPostcode() {
		return restaurant.getLocation();
	}
	
	@Override
	public User register(String username, String password, String address, Postcode postcode) {
		User user = new User(username, password, address, postcode);
		users.add(user);
		return user;
	}

	@Override
	public User login(String username, String password) {
		for(User u : users)
			if(u.getName().equals(username) && u.getPassword().equals(password))
				return u;
		return null;
	}

	@Override
	public List<Postcode> getPostcodes() {
		return postcodes;
	}

	@Override
	public List<Dish> getDishes() {
		return dishes;
	}

	@Override
	public String getDishDescription(Dish dish) {
		return dish.getDescription();
	}

	@Override
	public Number getDishPrice(Dish dish) {
		return dish.getPrice();
	}

	@Override
	public Map<Dish, Number> getBasket(User user) {
		return basket;
	}

	@Override
	public Number getBasketCost(User user) {
		double sum = 0.0;
		for (Map.Entry<Dish, Number> entry : basket.entrySet())
			sum += entry.getKey().getPrice().doubleValue() * entry.getValue().intValue();
		return sum;
	}

	@Override
	public void addDishToBasket(User user, Dish dish, Number quantity) {
		basket.put(dish, quantity);
	}

	@Override
	public void updateDishInBasket(User user, Dish dish, Number quantity) {
		if((int) quantity == 0)
			basket.remove(dish);
		else
			basket.put(dish, quantity);

	}

	@Override
	public Order checkoutBasket(User user) {
		HashMap<Dish, Number> checkedOutBasket = new HashMap<>();
		checkedOutBasket.putAll(basket);
		Order order = new Order(checkedOutBasket, user);
		orders.add(order);
		commsClient.sendMessage(new Message(order, "ADD"));
		clearBasket(user);
		return order;
	}

	@Override
	public void clearBasket(User user) {
		basket.clear();
	}

	@Override
	public List<Order> getOrders(User user) {
		ArrayList<Order> list = new ArrayList<>();
		for(Order o : orders)
			if(o.getUser().getName().equals(user.getName()))
				list.add(o);
			return list;
	}

	@Override
	public boolean isOrderComplete(Order order) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getOrderStatus(Order order) {
		return order.getStatus();
	}

	@Override
	public Number getOrderCost(Order order) {
		return order.price;
	}

	@Override
	public void cancelOrder(Order order) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addUpdateListener(UpdateListener listener) {
		this.listeners.add(listener);

	}

	@Override
	public void notifyUpdate() {
		this.listeners.forEach(listener -> listener.updated(new UpdateEvent()));
	}


	public void sendMessage(Message message){

	}
}
