package mx.jalan.WebSocket;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;

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

import mx.jalan.Controller.ConsoleController;
import mx.jalan.Model.Usuario;

@ClientEndpoint
public class ClientChat {
	
	private Session session;
	
	private Usuario usuario;
	
	private ConsoleController cc;
	
	private String usersString;
	
	public ClientChat(URI url, Usuario usuario)throws Exception{
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		container.connectToServer(this, url);
		
		this.usuario = usuario;
	}
	
	@OnOpen
	public void onOpen(Session userSession){
		this.session = userSession;
	}
	
	@OnClose
	public void onClose(Session userSession, CloseReason reason){		
		this.session = null;
	}
	
	@OnError
	public void onError(Throwable err){
		System.out.println("Error: "+err.getMessage());
		System.out.println(err);
	}
	
	@OnMessage
	public void onMessage(String message){
		try(JsonReader reader = Json.createReader(new StringReader(message))){
			JsonObject jsonMessage = reader.readObject();
			
			System.out.println("[DG - OnMessage]: "+jsonMessage.toString());
			
			if(jsonMessage.getString("action").equals("msg")){
				String contentMessage = jsonMessage.getString("content");
				
				if(jsonMessage.getString("rem").equals("server")){
					contentMessage = "[SERVER]: "+contentMessage+"\n";
					cc.appendMessageText(-1, contentMessage);
				}else if(!jsonMessage.getString("destin").equals("all")){
					contentMessage = "[PM de \""+jsonMessage.getString("rem")+"\"]: "+contentMessage+"\n";
					cc.appendMessageText(-1, contentMessage);
				}else{
					contentMessage = jsonMessage.getString("rem")+": "+contentMessage+"\n";
					cc.appendMessageText(-1, contentMessage);
				}
			}if(jsonMessage.getString("action").equals("updateData")){
				JsonArray jarr = jsonMessage.getJsonArray("data");
				cc.getUsers().clear();
				//cc.getUsers().addAll(jarr.);
				usersString = "Usuarios conectados: ";
				jarr.forEach((jsonvalue) -> {
					JsonObject js = (JsonObject)jsonvalue;
					Usuario userL = new Usuario(js.getString("usuario"));
					cc.getUsers().add(userL);
					usersString += "["+userL.getNombre()+"] ";
				});
				
				cc.appendMessageText(-1, usersString+"\n");
			}if(jsonMessage.getString("action").equals("msgError")){
				String contentMessage = jsonMessage.getString("content");
				
				contentMessage = "ERROR: "+contentMessage+"\n";
				
				cc.appendMessageText(1, contentMessage);
			}
		}
	}
	
	public void sendMessage(JsonObject message){
		try{
			System.out.println("[DG - SendMessage]: "+message.toString());
			this.session.getBasicRemote().sendText(message.toString());
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
	
	public JsonObject createMessage(String msg){
		JsonProvider provider = JsonProvider.provider();
		JsonObject sendMessage = provider.createObjectBuilder()
				.add("action", "msg")
				.add("destin", "all")
				.add("rem", usuario.getNombre())
				.add("content", msg).build();
		return sendMessage;
	}
	
	public JsonObject createPrivateMessage(String msg, String usuario){
		JsonProvider provider = JsonProvider.provider();
		JsonObject sendPrivateMessage = provider.createObjectBuilder()
				.add("action", "msg")
				.add("destin", usuario)
				.add("rem", this.usuario.getNombre())
				.add("content", msg).build();
		return sendPrivateMessage;
	}
	
	public JsonObject creatRequestChanges(){
		JsonProvider provider = JsonProvider.provider();
		JsonObject request = provider.createObjectBuilder()
				.add("action", "requestChanges").build();
		
		return request;
	}
	
	public JsonObject createNewUsr(){
		JsonProvider provider = JsonProvider.provider();
		JsonObject msg = provider.createObjectBuilder()
				.add("action", "newUsr")
				.add("nombre", usuario.getNombre())
				.add("avatarURL", JsonObject.NULL).build();
		
		return msg;
	}
	
	public void setConsole(ConsoleController cc){
		this.cc = cc;
	}
	
	public ConsoleController getConsole(){
		return cc;
	}
	
	public Usuario getUser(){
		return usuario;
	}
}
