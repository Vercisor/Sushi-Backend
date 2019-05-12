package comp1206.sushi.common;

public class Message {

    private String operation;
    private Object object;

    public Message(Object object, String operation){
        this.object = object;
        this.operation = operation;
    }

    public Object getObject() {
        return object;
    }

    public String getOperation() {
        return operation;
    }
}
