package comp1206.sushi;

import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Ingredient;
import comp1206.sushi.common.Order;
import comp1206.sushi.server.Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Scanner;

public class Configuration {

    String filename;
    Server server;
    File file;

    public Configuration(String filename, Server server){

        file = new File(filename);
        this.server = server;
    }

    public void configure(){

        String modelType;
        String temp;

        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        scanner.useDelimiter(":|\\* |,|\\n");

        while (scanner.hasNext()){
            modelType = scanner.next();
            switch (modelType) {

                case "DRONE":
                    try {
                        server.addDrone(NumberFormat.getInstance().parse(scanner.next()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    System.out.println(server.getDrones());
                    break;


                case "POSTCODE":
                    server.addPostcode(scanner.next());
                    System.out.println("------------POSTCODES: -----------\n" + server.getPostcodes());
                    break;

                case "SUPPLIER":
                    server.addSupplier(scanner.next(), server.getPostcodeByCode(scanner.next()));
                    System.out.println("------------SUPPLIERS: -----------\n" + server.getSuppliers());
                    break;

                case "INGREDIENT":
                    try {
                        server.addIngredient(scanner.next(), scanner.next(), server.getSupplierByName(scanner.next()),
                                NumberFormat.getInstance().parse(scanner.next()),
                                NumberFormat.getInstance().parse(scanner.next()),
                                NumberFormat.getInstance().parse(scanner.next()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    System.out.println("------------INGREDIENTS: -----------\n" + server.getIngredients());
                    break;

                case "DISH":
                    String line = scanner.nextLine();
                    boolean lineEnded = true;
                    Scanner dishScanner = new Scanner(line);
                    dishScanner.useDelimiter(":|\\* |,|\\n");
                    Number num = 0;
                    while (dishScanner.hasNext())
                    {
                        String dishName = dishScanner.next();
                        try {
                            server.addDish(dishName, dishScanner.next(),
                                NumberFormat.getInstance().parse(dishScanner.next()),
                                NumberFormat.getInstance().parse(dishScanner.next()),
                                NumberFormat.getInstance().parse(dishScanner.next()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        while(lineEnded) {
                            String a = dishScanner.next();
                            try {
                                num = NumberFormat.getInstance().parse(a);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String ing;
                            ing = dishScanner.next();
                            server.addIngredientToDish(server.getDishByName(dishName), server.getIngredientByName(ing),
                                num);
                            if (dishScanner.hasNext() == false)
                                lineEnded = false;
                        }
                    }
                    for (Dish d : server.getDishes()) {

                        System.out.println("DISH: " + d.getName() + "\n" + "RECIPE: " + d.getRecipe());

                    }
                    break;

                case "USER":
                    server.addUser(scanner.next(), scanner.next(), scanner.next(), server.getPostcodeByCode(scanner.next()));

                    System.out.println("------------USERS: -----------\n" + server.getUsers());
                    break;

                case "ORDER":
                    String orderLine = scanner.nextLine();
                    Scanner orderScanner = new Scanner(orderLine);
                    orderScanner.useDelimiter(":|\\* |,|\\n");
                    String user;
                    Number number = 0;

                    while(orderScanner.hasNext()) {
                        boolean endOfOrder = true;
                        user = orderScanner.next();
                        server.addOrder(server.getUserByName(user));
                        while (endOfOrder) {
                            String a = orderScanner.next();
                            try {
                                number = NumberFormat.getInstance().parse(a);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String ing;
                            ing = orderScanner.next();
                            server.addDishToOrder(server.getOrderbyUser(user), server.getDishByName(ing), number);
                            if (orderScanner.hasNext() == false)
                                endOfOrder = false;
                        }
                    }

                    for(Order o : server.getOrders())
                        System.out.println("ORDER: " + o.getUser() + "\n CONTAINING: " + o.getOrder());

                    break;

                case "STOCK":
                    String ingredientOrDish = scanner.next();
                    for(Dish d : server.getDishes()) {
                        if (d.getName().equals(ingredientOrDish)) {
                            try {
                                server.setStock(server.getDishByName(ingredientOrDish), NumberFormat.getInstance().parse(scanner.next()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    for(Ingredient i : server.getIngredients())
                        if(i.getName().equals(ingredientOrDish)) {
                            try {
                                server.setStock(server.getIngredientByName(ingredientOrDish), NumberFormat.getInstance().parse(scanner.next()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    break;

                case "STAFF":
                    server.addStaff(scanner.next());
                    break;
            }
        }
    }
}
