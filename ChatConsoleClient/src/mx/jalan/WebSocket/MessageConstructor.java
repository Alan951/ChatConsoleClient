package mx.jalan.WebSocket;

import mx.jalan.Model.Message;
import mx.jalan.Model.MessageHelper;
import mx.jalan.Model.User;

public class MessageConstructor {

	public static Message createPrivateMessage(String msg, User usuario){
		return null;
	}
	
	public static Message createMessage(String msg){
		return null;
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
	
}
