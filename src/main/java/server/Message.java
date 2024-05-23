package server;

import java.time.Instant;

public class Message {
    private final Instant time;
    private final String user;
    private final String message;


    public Message(String user, String message) {
        this.time = Instant.now();
        this.user = user;
        this.message = message;
    }

    @Override
    public String toString(){
        return time + " " + user + ": " + message;
    }

    public static Message parse(String string) {
        String[] arr = string.split(";", 2);
        return new Message(arr[0], arr[1]);
    }

    // Getter for date
    public Instant getTime() {
        return time;
    }

    // Getter for user
    public String getUser() {
        return user;
    }

    // Getter for message
    public String getMessage() {
        return message;
    }

    // Setter for user
    public void setUser(String user) {
        // This would not work since 'user' is final
        // Uncomment the next line if you remove 'final' from 'user' declaration
        // this.user = user;
        throw new UnsupportedOperationException("User is final and cannot be changed");
    }

    // Setter for message
    public void setMessage(String message) {
        // This would not work since 'message' is final
        // Uncomment the next line if you remove 'final' from 'message' declaration
        // this.message = message;
        throw new UnsupportedOperationException("Message is final and cannot be changed");
    }
}
