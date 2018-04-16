package mx.jalan.Model;

import java.time.LocalDateTime;

public class Message {
	private int code;
    private String action;
    private String message;
    private User userSource;
    private User userDestination;
    private EncryptionAlgorithm encryptProps;
    private LocalDateTime timestamp;

    // Default constructor
    public Message(){}
    
    /*
        Constructor to create message from user to other user by private message
    */
    public Message(String action, String message, User userSource, User userDestination, EncryptionAlgorithm encryptProps, LocalDateTime timestamp, int code) {
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
    public Message(String action, String message, User userSource, EncryptionAlgorithm encryptProps, LocalDateTime timestamp, int code) {
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
    public Message(String action, String message, EncryptionAlgorithm encryptProps, int code){
        this.action = action;
        this.message = message;
        this.encryptProps = encryptProps;
        this.code = code;
        
    }
    
    public Message setAction(String action){
        this.action = action;
        return this;
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

    public Message setMessage(String message) {
        this.message = message;
        
        return this;
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

    public EncryptionAlgorithm getEncryptProps() {
        return encryptProps;
    }

    public void setEncryptProps(EncryptionAlgorithm encryptProps) {
        this.encryptProps = encryptProps;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

	@Override
	public String toString() {
		return "Message [code=" + code + ", action=" + action + ", message=" + message + ", userSource=" + userSource
				+ ", userDestination=" + userDestination + ", encryptProps=" + encryptProps + ", timestamp=" + timestamp
				+ "]";
	}
}
