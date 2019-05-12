package comp1206.sushi.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import comp1206.sushi.Configuration;
import comp1206.sushi.client.Client;
import comp1206.sushi.client.CommsClient;
import comp1206.sushi.common.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
 
public class Server implements ServerInterface {

    private static final Logger logger = LogManager.getLogger("Server");
	
	public Restaurant restaurant;
	public ArrayList<Dish> dishes = new ArrayList<Dish>();
	public ArrayList<Drone> drones = new ArrayList<Drone>();
	public ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
	public ArrayList<Order> orders = new ArrayList<Order>();
	public ArrayList<Staff> staff = new ArrayList<Staff>();
	public ArrayList<Supplier> suppliers = new ArrayList<Supplier>();
	public ArrayList<User> users = new ArrayList<User>();
	public ArrayList<Postcode> postcodes = new ArrayList<Postcode>();
	private ArrayList<UpdateListener> listeners = new ArrayList<UpdateListener>();
	private Configuration configuration;
	private StockManagement stockManagement;
	private CommsServer commsServer;
	
	public Server() {
        logger.info("Starting up server...");
		Postcode restaurantPostcode = new Postcode("SO17 1BJ");
		restaurant = new Restaurant("Mock Restaurant",restaurantPostcode);
		stockManagement = new StockManagement(this);
		restaurantPostcode.getLatLong();
		Thread commsThread = new Thread(new CommsServer(2239, this));
		commsThread.start();
	}


	@Override
	public List<Dish> getDishes() {
		return this.dishes;
	}

	@Override
	public Dish addDish(String name, String description, Number price, Number restockThreshold, Number restockAmount) {
		Dish newDish = new Dish(name,description,price,restockThreshold,restockAmount);
		this.dishes.add(newDish);
		this.notifyUpdate();
		return newDish;
	}
	
	@Override
	public void removeDish(Dish dish) {
		this.dishes.remove(dish);
		this.notifyUpdate();
	}

	@Override
	public Map<Dish, Number> getDishStockLevels() {
		List<Dish> dishes = getDishes();
		HashMap<Dish, Number> levels = new HashMap<Dish, Number>();
		for(Dish dish : dishes) {
			levels.put(dish,dish.getStockAmount());
		}
		return levels;
	}


	@Override
	public void setRestockingIngredientsEnabled(boolean enabled) {
		
	}

	@Override
	public void setRestockingDishesEnabled(boolean enabled) {
		
	}
	
	@Override
	public void setStock(Dish dish, Number stock) {

		for(Dish d : dishes)
			if(d.getName().equals(dish.getName()))
				d.setStockAmount(stock);
	
	}

	@Override
	public void setStock(Ingredient ingredient, Number stock) {
		for(Ingredient i : ingredients)
			if(ingredient.getName().equals(i.getName()))
				i.setStockAmount(stock);
	}

	@Override
	public List<Ingredient> getIngredients() {
		return this.ingredients;
	}

	@Override
	public Ingredient addIngredient(String name, String unit, Supplier supplier,
			Number restockThreshold, Number restockAmount, Number weight) {
		Ingredient mockIngredient = new Ingredient(name,unit,supplier,restockThreshold,restockAmount,weight);
		this.ingredients.add(mockIngredient);
		this.notifyUpdate();
		return mockIngredient;
	}

	@Override
	public void removeIngredient(Ingredient ingredient) {
		int index = this.ingredients.indexOf(ingredient);
		this.ingredients.remove(index);
		this.notifyUpdate();
	}

	@Override
	public List<Supplier> getSuppliers() {
		return this.suppliers;
	}

	@Override
	public Supplier addSupplier(String name, Postcode postcode) {
		Supplier mock = new Supplier(name,postcode);
		mock.addRestaurant(getRestaurant());
		this.suppliers.add(mock);
		this.notifyUpdate();
		return mock;
	}


	@Override
	public void removeSupplier(Supplier supplier) {
		int index = this.suppliers.indexOf(supplier);
		this.suppliers.remove(index);
		this.notifyUpdate();
	}

	@Override
	public List<Drone> getDrones() {
		return this.drones;
	}

	@Override
	public Drone addDrone(Number speed) {
		Drone mock = new Drone(speed);
		mock.setRestaurant(getRestaurant());
		mock.setServer(this);
		mock.setThreadIsActive(true);
		Thread thread = new Thread(mock);
		this.drones.add(mock);
		this.notifyUpdate();
		thread.start();
		return mock;
	}

	public User addUser(String username, String passowrd, String address, Postcode postcode){
		User mock = new User(username, passowrd, address, postcode);
		mock.setRestaurant(restaurant);
		this.users.add(mock);
		this.notifyUpdate();
		return mock;
	}

	@Override
	public void removeDrone(Drone drone) {
		int index = this.drones.indexOf(drone);
		this.drones.get(index).setThreadIsActive(false);
		this.drones.remove(index);
		this.notifyUpdate();
	}

	@Override
	public List<Staff> getStaff() {
		return this.staff;
	}

	@Override
	public Staff addStaff(String name) {
		Staff mock = new Staff(name);
		mock.setServer(this);
		Thread thread = new Thread(mock);
		this.staff.add(mock);
		this.notifyUpdate();
		thread.start();
		return mock;
	}

	@Override
	public void removeStaff(Staff staff) {
		this.staff.remove(staff);
		this.notifyUpdate();
	}

	@Override
	public List<Order> getOrders() {
		return this.orders;
	}

	@Override
	public void removeOrder(Order order) {
		int index = this.orders.indexOf(order);
		this.orders.remove(index);
		this.notifyUpdate();
	}
	
	@Override
	public Number getOrderCost(Order order) {
		double sum = 0.0;
		for (Map.Entry<Dish, Number> entry : order.getOrder().entrySet())
			sum += entry.getKey().getPrice().doubleValue() * entry.getValue().intValue();
		return sum;
	}

	@Override
	public Map<Ingredient, Number> getIngredientStockLevels() {
		Random random = new Random();
		List<Ingredient> dishes = getIngredients();
		HashMap<Ingredient, Number> levels = new HashMap<Ingredient, Number>();
		for(Ingredient ingredient : ingredients) {
			levels.put(ingredient,ingredient.getStockAmount());
		}
		return levels;
	}

	@Override
	public Number getSupplierDistance(Supplier supplier) {
		supplier.calculateDistance(getRestaurant());
		return supplier.getDistance();
	}

	@Override
	public Number getDroneSpeed(Drone drone) {
		return drone.getSpeed();
	}

	@Override
	public Number getOrderDistance(Order order) {
		Order mock = (Order)order;
		return mock.getDistance();
	}

	@Override
	public void addIngredientToDish(Dish dish, Ingredient ingredient, Number quantity) {
		if(quantity == Integer.valueOf(0)) {
			removeIngredientFromDish(dish,ingredient);
		} else {
			dish.getRecipe().put(ingredient,quantity);
		}
		this.notifyUpdate();
	}

	public void addDishToOrder(Order order, Dish dish, Number quantity){
		if(quantity == Integer.valueOf(0)) {
			removeDishFromOrder(order, dish, quantity);
		} else {
			order.getOrder().put(dish,quantity);
		}
		this.notifyUpdate();
	}

	public Order addOrder(User user){
		Order mock = new Order(user);
		this.orders.add(mock);
		this.notifyUpdate();
		return mock;
	}

	public void removeDishFromOrder(Order order, Dish dish, Number quantity){
		order.getOrder().remove(dish, quantity);
		this.notifyUpdate();
	}

	@Override
	public void removeIngredientFromDish(Dish dish, Ingredient ingredient) {
		dish.getRecipe().remove(ingredient);
		this.notifyUpdate();
	}

	@Override
	public Map<Ingredient, Number> getRecipe(Dish dish) {
		return dish.getRecipe();
	}

	@Override
	public List<Postcode> getPostcodes() {
		return this.postcodes;
	}

	@Override
	public Postcode addPostcode(String code) {
		Postcode mock = new Postcode(code);
		this.postcodes.add(mock);
		this.notifyUpdate();
		return mock;
	}

	public Postcode getPostcodeByCode(String postcode){
		for(Postcode p : postcodes)
			if(p.getName().equals(postcode))
				return p;
			return null;
	}

	public Supplier getSupplierByName(String supName){
		for(Supplier s : suppliers)
			if(s.getName().equals(supName))
				return s;
			return null;
	}

	public Ingredient getIngredientByName(String ingName){
		for(Ingredient i : ingredients)
			if(i.getName().equals(ingName))
				return i;
		return null;
	}

	public Dish getDishByName(String dishName){
		for(Dish d : dishes)
			if(d.getName().equals(dishName))
				return d;
		return null;
	}

	public Order getOrderbyUser(String user){
		for(Order o : orders)
			if(o.getUser().getName().equals(user))
				return o;
		return null;
	}

	public User getUserByName(String user){
		for(User u : users)
			if(u.getName().equals(user))
				return u;
		return null;
	}

	@Override
	public void removePostcode(Postcode postcode) throws UnableToDeleteException {
		this.postcodes.remove(postcode);
		this.notifyUpdate();
	}

	@Override
	public List<User> getUsers() {
		return this.users;
	}
	
	@Override
	public void removeUser(User user) {
		this.users.remove(user);
		this.notifyUpdate();
	}

	@Override
	public void loadConfiguration(String filename) {
		configuration = new Configuration(filename, this);
		System.out.println("Loaded configuration: " + filename);
		configuration.configure();
		this.notifyUpdate();
	}

	@Override
	public void setRecipe(Dish dish, Map<Ingredient, Number> recipe) {
		for(Entry<Ingredient, Number> recipeItem : recipe.entrySet()) {
			addIngredientToDish(dish,recipeItem.getKey(),recipeItem.getValue());
		}
		this.notifyUpdate();
	}

	@Override
	public boolean isOrderComplete(Order order) {
		return true;
	}

	@Override
	public String getOrderStatus(Order order) {
		Random rand = new Random();
		if(rand.nextBoolean()) {
			return "Complete";
		} else {
			return "Pending";
		}
	}
	
	@Override
	public String getDroneStatus(Drone drone) {
		return drone.getStatus();
	}
	
	@Override
	public String getStaffStatus(Staff staff) {
		return staff.getStatus();
	}

	@Override
	public void setRestockLevels(Dish dish, Number restockThreshold, Number restockAmount) {
		dish.setRestockThreshold(restockThreshold);
		dish.setRestockAmount(restockAmount);
		this.notifyUpdate();
	}

	@Override
	public void setRestockLevels(Ingredient ingredient, Number restockThreshold, Number restockAmount) {
		ingredient.setRestockThreshold(restockThreshold);
		ingredient.setRestockAmount(restockAmount);
		this.notifyUpdate();
	}

	@Override
	public Number getRestockThreshold(Dish dish) {
		return dish.getRestockThreshold();
	}

	@Override
	public Number getRestockAmount(Dish dish) {
		return dish.getRestockAmount();
	}

	@Override
	public Number getRestockThreshold(Ingredient ingredient) {
		return ingredient.getRestockThreshold();
	}

	@Override
	public Number getRestockAmount(Ingredient ingredient) {
		return ingredient.getRestockAmount();
	}

	@Override
	public void addUpdateListener(UpdateListener listener) {
		this.listeners.add(listener);
	}
	
	@Override
	public void notifyUpdate() {
		this.listeners.forEach(listener -> listener.updated(new UpdateEvent()));
	}

	@Override
	public Postcode getDroneSource(Drone drone) {
		return drone.getSource();
	}

	@Override
	public Postcode getDroneDestination(Drone drone) {
		return drone.getDestination();
	}

	@Override
	public Number getDroneProgress(Drone drone) {
		return drone.getProgress();
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
	public Restaurant getRestaurant() {
		return restaurant;
	}


	public void sendMessage(Message message, Client client){

	}

}
