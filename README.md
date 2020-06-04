# COMP1206: Sushi Coursework part 2 - The Backend

## Introduction
The goal of this coursework is to create a fully functional back-end to a sushi restaurant. It is the continuation of the Sushi-Frontend Coursework. The central system communicates with multiple client programs which can send orders over the internet to the restaurant.

## Specifications

There can be only one restaurant running the program at a time, but as many clients as you wish may run the program and connect to the restaurant. They either need to log in with pre-existing details or sign-up to access the menu to order. The client needs to be able to communicate with the restaurant and the restaurant needs to be able to communicate with specific clients.

A restaurant needs to keep in stock Dishes and Ingredients. Dishes are restocked by Staff, who wait until a Dish needs to be restocked. Staff members use Ingredients specified within a recipe that is associated with the Dish. Ingredients are restocked by Drones, which go to Suppliers linked to the Ingredient.

Each Ingredient and Dish have a restock threshold and restock amount. Once one goes below, the item is restocked to the restock threshold and restock amount.

Drones also have a double function of delivering Orders, which are placed by Clients.

## Main source tree
The main source tree can be found in src/main/java

## Dependencies:
- The project has a pom.xml file which contains all needed dependencies.
- Executing mvn install builds /target/sushi-2.jar in the target folder.
- It is possible to run just the sushi-2.jar directly with no parameters from the target folder.

## Execution: 
- Executing java -jar target/sushi-2.jar server will start the server
- Executing java -jar sushi-2.jar client will start the client.

## Creating the jar file
    ```mvn install```

## Running
    ```
    java -jar target/sushi-2.jar
    java -jar target/sushi-2.jar server
    java -jar target/sushi-2.jar client
    ```

