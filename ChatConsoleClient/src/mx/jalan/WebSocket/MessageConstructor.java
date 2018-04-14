package mx.jalan.WebSocket;

import mx.jalan.Model.Message;
import mx.jalan.Model.MessageHelper;
import mx.jalan.Model.User;

public class MessageConstructor {

	public static Message createPrivateMessage(String msg, User usrDest){
		Message message = new Message();
		message.setAction(MessageHelper.SIMPLE_MESSAGE);
		message.setUserDestination(usrDest);
		message.setMessage(msg);
		
		return message;
	}
	
	public static Message createMessage(String msg){
		Message message = new Message();
		message.setAction(MessageHelper.SIMPLE_MESSAGE);
		message.setMessage(msg);
		
		return message;
	}
	
	public static Message createRequestChangesMessage(){
		Message message = new Message();
		message.setAction(MessageHelper.REQ_CHANGES);
		
		return message;
	}
	
	public static Message registerNewUser(){
		Message message = new Message();
		message.setAction(MessageHelper.NEW_USER_MESSAGE);
		
		return message;
	}
	
	public static Message enableEncryption(){
		Message message = new Message();
		//message.setAction(MessageHelper.);
		return null;
	}
	
}
