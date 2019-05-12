package comp1206.sushi.client;

import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Message;
import comp1206.sushi.common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class CommsClient implements Runnable {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Client client;


    public CommsClient(int port, String host, Client client){

        this.client = client;

        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
    }

    public CommsClient(Socket socket){
        this.socket = socket;
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message){
        try {
            out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void receiveMessage(){
        try {
            Object object = in.readObject();
            if(object instanceof Dish)
                client.dishes.add((Dish) object);
            else if(object instanceof User)
                client.users.add((User) object);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
