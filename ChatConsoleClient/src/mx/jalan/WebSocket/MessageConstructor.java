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
		return new Message()
				.setAction(MessageHelper.SIMPLE_MESSAGE)
				.setMessage(msg);
	}
	
	public static Message createRequestChangesMessage(){
		return new Message().setAction(MessageHelper.REQ_CHANGES);
	}
	
	public static Message registerNewUser(){
		return new Message().setAction(MessageHelper.NEW_USER_MESSAGE);
	}
	
	public static Message enableEncryption(){
		return new Message().setAction(MessageHelper.REQ_ENABLE_ENCRYPTION);
	}
	
	public static Message disableEncryption(){
		return new Message().setAction(MessageHelper.REQ_DISABLE_ENCRYPTION);
	}
	
	public static Message checkEncryption(){
		return new Message().setAction(MessageHelper.CHECK_ENCRYPTION);
	}
	
}
