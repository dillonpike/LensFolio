package nz.ac.canterbury.seng302.portfolio.model;

public class EventMessage {

    private String message;

    public EventMessage(String message) {
        this.message = message;
    }

    public EventMessage() {}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
