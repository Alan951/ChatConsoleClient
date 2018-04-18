package mx.jalan.WebSocket;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import javafx.application.Platform;
import mx.jalan.Controller.ConsoleController;
import mx.jalan.Model.EncryptionAlgorithm;
import mx.jalan.Model.Message;
import mx.jalan.Model.MessageHelper;
import mx.jalan.Model.User;
import mx.jalan.Security.CipherFactory;
import mx.jalan.Security.EncryptionAlgorithms;
import mx.jalan.Security.Algorithms.CipherBase;
import mx.jalan.Utils.JsonUtils;

@ClientEndpoint
public class ClientChat {
	
	private Session session;
	private User usuario;
	private ConsoleController cc;
	private String usersString;
	private WebSocketContainer container;
	
	private List<EncryptionAlgorithm> encryptionSupport;
	private List<MessageListener> messageListeners;
	
	private CipherBase cipher;
	private EncryptionAlgorithm encyptAlgSelected;
	
	public ClientChat(URI url, User usuario)throws Exception{
		container = ContainerProvider.getWebSocketContainer();
		container.connectToServer(this, url);
		
		this.usuario = usuario;
		
		//Init encryptionSupport
		this.encryptionSupport = new ArrayList<EncryptionAlgorithm>();
		Map<String, String> syncProp = new HashMap<String, String>();
        syncProp.put("key", "");
        
        this.encryptionSupport.add(new EncryptionAlgorithm(EncryptionAlgorithms.CAESAR, 
                EncryptionAlgorithms.SYNC_CIPHER, 
                syncProp));
        
        //Init listeners
        messageListeners = new ArrayList<MessageListener>();
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
		Message message;
		
		if(JsonUtils.isJsonObject(msg)){ //msg es un json
			message = new Gson().fromJson(msg, Message.class);
		}else{ //Probablemente sea un mensaje cifrado.
			System.out.println("[DG - OnMessage Encrypted?]: "+msg);
			
			String msgDecoded = this.cipher.decode(msg);
			
			if(JsonUtils.isJsonObject(msgDecoded)){
				message = new Gson().fromJson(msgDecoded, Message.class);
			}else{
				return;
			}
		}
		
		
		System.out.println("[DG - OnMessage]: "+message);
		
		if(message != null)
			Platform.runLater(() -> this.messageListeners.forEach(msgListener -> msgListener.onMessage(message)));
		
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
			case MessageHelper.REQ_CHANGES:
				System.out.println("RES_CHANGES invoked!");
				
				break;
			case MessageHelper.ERROR_MESSAGE:
				cc.appendMessageText(1,  
						"[SERVER] Error \""+message.getCode()+"\": " + 
								message.getMessage());
				break;
		}
	}
	
	public void sendMessage(Message msg){
		msg.setUserSource(this.usuario);
		msg.setTimestamp(LocalDateTime.now());
		
		String jsonMessage = new Gson().toJson(msg, Message.class);
		
		String message = null;
		
		if(this.cipher != null){
			String messageBase64 = null;
			String messageEncrypted = null;
			String messageEncryptedBase64 = null;
			
			messageBase64 = new String(Base64.getEncoder().encode(jsonMessage.getBytes()));
			messageEncrypted = this.getCipher().encode(messageBase64);
            messageEncryptedBase64 = new String(Base64.getEncoder().encode(messageEncrypted.getBytes()));
            message = messageEncryptedBase64;
		}else{
			message = jsonMessage;
		}
		
		try{
			System.out.println("[DG - SendMessage]: "+msg.toString());
			this.session.getBasicRemote().sendText(message);
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
	
	public void enableEncryptionAlgorithm(){
		this.cipher = new CipherFactory<String, Long>().getCipher(this.encyptAlgSelected.getAlgorithm());
		if(!this.cipher.isAsyncCipher()){
			this.cipher.setKey(Long.parseLong(this.encyptAlgSelected.getProperties().get("key")));
		}
	}
	
	public void disableEncryptionAlgorithm(){
		this.cipher = null;
		//this.encyptAlgSelected = null;
	}
	
	public void setEncryptionAlgorithmSelected(EncryptionAlgorithm encAlg){
		this.encyptAlgSelected = encAlg;
	}
	
	public EncryptionAlgorithm getEncryptionAlgorithmSelected(){
		return this.encyptAlgSelected;
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
	
	public List<EncryptionAlgorithm> getEncryptionSupport(){
		return this.encryptionSupport;
	}
	
	public void addListener(MessageListener msgListener){
		this.messageListeners.add(msgListener);
	}
}
