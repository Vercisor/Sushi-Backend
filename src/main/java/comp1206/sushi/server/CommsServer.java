package comp1206.sushi.server;

import comp1206.sushi.client.CommsClient;
import comp1206.sushi.common.Message;
import comp1206.sushi.common.Order;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class CommsServer implements Runnable{

    private Socket socket;
    private ServerSocket serverSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ArrayList<CommsClient> clients;
    private LinkedBlockingQueue<Object> messages;
    private Server server;

    public CommsServer(int port, Server server){

        clients = new ArrayList<>();
        this.server = server;

        try{
            serverSocket = new ServerSocket(port);
            log("Listening on port "+ port +"...");
        }
        catch(IOException e){ e.printStackTrace(); }
    }

    @Override
    public void run() {

        try {
            socket = serverSocket.accept();
            log("Accepted connection!");
            //clients.add(new CommsClient(socket));
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void log(String s){
        System.out.println(s);
    }


    public void sendMessage(Message message){
        for(CommsClient client : clients) {
            try {
                out.writeObject(message.getObject());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void receiveMessage(){

        try {
            Object msg = in.readObject();
            if(msg instanceof Message) {
                if (((Message) msg).getObject() instanceof Order && ((Message) msg).getOperation().equals("ADD")) {
                    for (Order o : server.getOrders())
                        if (!((((Order) ((Message) msg).getObject()).getName()).equals(o.getName())))
                            server.orders.add((Order) ((Message) msg).getObject());
                }
                else if (((Message) msg).getObject() instanceof Order && ((Message) msg).getOperation().equals("REMOVE"))
                    for (Order o : server.getOrders())
                        if (!((((Order) ((Message) msg).getObject()).getName()).equals(o.getName())))
                            server.orders.remove(o);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
