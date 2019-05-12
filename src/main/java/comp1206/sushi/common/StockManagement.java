package comp1206.sushi.common;

import comp1206.sushi.server.Server;

import java.util.ArrayList;
import java.util.HashMap;

public class StockManagement {

    private Server server;
    private ArrayList<Staff> staff;
    private HashMap<Dish, Number> dishStockLevel;
    private HashMap<Dish, Number> dishRestockThreshold;
    private HashMap<Dish, Number> dishRestock;


    public StockManagement(Server server){
        this.server = server;
        dishStockLevel = new HashMap<>();
        dishRestock = new HashMap<>();
        dishRestockThreshold = new HashMap<>();
    }

    public ArrayList<Staff> getStaffFromServer(){
        staff = (ArrayList<Staff>) server.getStaff();
        return staff;
    }

    public void fulfillOrder(){

    }

    public void updateStockLevels(){
        for(Dish d : server.getDishes())
            dishStockLevel.put(d, d.getStockAmount());
    }

    public HashMap<Dish, Number> getDishStockLevel() {
        for(Dish d : server.getDishes())
            dishStockLevel.put(d, d.getStockAmount());
        return dishStockLevel;
    }

    public HashMap<Dish, Number> getDishRestockThreshold() {
        for(Dish d : server.getDishes())
            dishStockLevel.put(d, d.getRestockThreshold());
        return dishRestockThreshold;
    }

    public HashMap<Dish, Number> getDishRestock() {
        for(Dish d : server.getDishes())
            dishRestock.put(d, d.getRestockAmount());
        return dishRestock;
    }
}
