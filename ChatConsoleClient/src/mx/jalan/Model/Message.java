package mx.jalan.Model;

import java.time.LocalDateTime;

public class Message {
	private int code;
    private String action;
    private String message;
    private User userSource;
    private User userDestination;
    private EncryptionProperties encryptProps;
    private LocalDateTime timestamp;

    // Default constructor
    public Message(){}
    
    /*
        Constructor to create message from user to other user by private message
    */
    public Message(String action, String message, User userSource, User userDestination, EncryptionProperties encryptProps, LocalDateTime timestamp, int code) {
        this.action = action;
        this.message = message;
        this.userSource = userSource;
        this.userDestination = userDestination;
        this.encryptProps = encryptProps;
        this.timestamp = timestamp;
        this.code = code;
    }

    /*
        Constructor to create message from user to all
    */
    public Message(String action, String message, User userSource, EncryptionProperties encryptProps, LocalDateTime timestamp, int code) {
        this.action = action;
        this.message = message;
        this.userSource = userSource;
        this.encryptProps = encryptProps;
        this.timestamp = timestamp;
        this.code = code;
    }
    
    /*
        Constructor to create message from server
    */
    public Message(String action, String message, EncryptionProperties encryptProps, int code){
        this.action = action;
        this.message = message;
        this.encryptProps = encryptProps;
        this.code = code;
        
    }
    
    public void setAction(String action){
        this.action = action;
    }
    
    public String getAction(){
        return action;
    }
    
    public void setCode(int code){
        this.code = code;
    }
    
    public int getCode(){
        return this.code;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUserSource() {
        return userSource;
    }

    public void setUserSource(User userSource) {
        this.userSource = userSource;
    }

    public User getUserDestination() {
        return userDestination;
    }

    public void setUserDestination(User userDestination) {
        this.userDestination = userDestination;
    }

    public EncryptionProperties getEncryptProps() {
        return encryptProps;
    }

    public void setEncryptProps(EncryptionProperties encryptProps) {
        this.encryptProps = encryptProps;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
