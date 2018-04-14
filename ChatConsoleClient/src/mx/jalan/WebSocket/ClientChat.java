package mx.jalan.WebSocket;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.time.LocalDateTime;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.spi.JsonProvider;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.google.gson.Gson;

import mx.jalan.Controller.ConsoleController;
import mx.jalan.Model.Message;
import mx.jalan.Model.MessageHelper;
import mx.jalan.Model.User;
import mx.jalan.Security.Algorithms.CipherBase;

@ClientEndpoint
public class ClientChat {
	
	private Session session;
	private User usuario;
	private ConsoleController cc;
	private String usersString;
	private WebSocketContainer container;
	
	private CipherBase cipher;
	
	public ClientChat(URI url, User usuario)throws Exception{
		container = ContainerProvider.getWebSocketContainer();
		container.connectToServer(this, url);
		
		this.usuario = usuario;
	}
	
	@OnOpen
	public void onOpen(Session userSession){
		this.session = userSession;
	}
	
	@OnClose
	public void onClose(Session userSession, CloseReason reason) throws IOException{		
		this.session.close();
		//this.session = null;
	}
	
	@OnError
	public void onError(Throwable err){
		System.out.println("Error: "+err.getMessage());
		System.out.println(err);
	}
	
	@OnMessage
	public void onMessage(String msg){
		Message message = new Gson().fromJson(msg, Message.class);
		
		System.out.println("[DG - OnMessage]: "+message);
		
		switch(message.getAction()){
			case MessageHelper.SIMPLE_MESSAGE:
				if(message.getUserSource() == null){ //Message from Server
					cc.appendMessageText(-1, "[SERVER]: " + message.getMessage());
				}else if(message.getUserDestination() == null){ //Message for All
					cc.appendMessageText(-1, message.getUserSource().getNombre()+": "+message.getMessage());
				}else{ //Private message
					cc.appendMessageText(-1, "[PM de \""+ message.getUserSource().getNombre() +"\"]: " + message.getMessage());				
				}
				
				break;
			case MessageHelper.RES_CHANGES:
				System.out.println("RES_CHANGES invoked!");
				
				break;
			case MessageHelper.ERROR_MESSAGE:
				cc.appendMessageText(1,  
						"[SERVER] Error \""+message.getCode()+"\": " + 
								message.getMessage());
				break;
		}
	}
	
	public void sendMessage(Message message){
		message.setUserSource(this.usuario);
		message.setTimestamp(LocalDateTime.now());
		
		String jsonMessage = new Gson().toJson(message, Message.class);
		
		try{
			System.out.println("[DG - SendMessage]: "+message.toString());
			this.session.getBasicRemote().sendText(jsonMessage);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public Session getSession(){
		return session;
	}
	
	public void disconnect(){
		try{
			session.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void setCipher(CipherBase cipher){
		this.cipher = cipher;
	}
	
	public CipherBase getCipher(){
		return this.cipher;
	}
	
	public void setConsole(ConsoleController cc){
		this.cc = cc;
	}
	
	public ConsoleController getConsole(){
		return cc;
	}
	
	public User getUser(){
		return usuario;
	}
}
