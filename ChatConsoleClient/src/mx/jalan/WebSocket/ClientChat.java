package mx.jalan.WebSocket;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
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
import mx.jalan.Utils.KeyUtils;
import mx.jalan.WebSocket.Services.EncryptionService;

@ClientEndpoint
public class ClientChat {
	
	private Session session;
	private User usuario;
	private ConsoleController cc;
	private String usersString;
	private WebSocketContainer container;
	
	private List<EncryptionAlgorithm> encryptionSupport;
	private List<MessageListener> messageListeners;
	
	//private CipherBase cipher;
	private EncryptionService encryptionService = EncryptionService.getInstance();
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
                syncProp,
                Long.class
        ));
        
        this.encryptionSupport.add(new EncryptionAlgorithm(EncryptionAlgorithms.DES, 
                EncryptionAlgorithms.SYNC_CIPHER,
                syncProp,
                String.class
        ));
        
        //Init listeners
        messageListeners = new ArrayList<MessageListener>();
	}
	
	@OnOpen
	public void onOpen(Session userSession){
		this.session = userSession;
	}
	
	@OnClose
	public void onClose(Session userSession, CloseReason reason) throws IOException{	
		if(this.session.isOpen())
			this.session.close();
		System.out.println(reason);
		Platform.runLater(() -> this.cc.onDisconnect());
		//this.cc.onDisconnect();
		//this.session = null;
	}
	
	@OnError
	public void onError(Throwable err){
		System.out.println("Error: "+err.getMessage());
		err.printStackTrace();
	}
	
	@OnMessage
	public void onMessage(String msg){
		Message message;
		boolean isEncrypted = false;
		
		if(JsonUtils.isJsonObject(msg)){ //msg es un json
			message = new Gson().fromJson(msg, Message.class);
		}else{ //Probablemente sea un mensaje cifrado.
			System.out.println("[DG - OnMessage Encrypted?]: "+msg);
			
			//if(this.cipher == null){ //El mensaje esta cifrado y no hay un metodo criptografico en el cliente activado
			if(!this.encryptionService.cipherActive()){
				System.out.println("[Cipher not setted yet]");
				return;
			}
			
			String msgDecoded = this.encryptionService.getCipher().decode(msg);
			
			if(JsonUtils.isJsonObject(msgDecoded)){
				message = new Gson().fromJson(msgDecoded, Message.class);
				isEncrypted = true;
			}else{ //No es un mensaje
				return;
			}
		}
		
		System.out.println("[DG - OnMessage"+ (isEncrypted ? " Decrypted" : "") +"]: "+message);
		
		if(message != null){
			Platform.runLater(() -> {
				System.out.println("[DG - ClientChat OnMessage]: Dispatch Event from listener for: " + this.messageListeners);
				
				ArrayList<MessageListener> listeners = new ArrayList<>(this.messageListeners);
				
				listeners.forEach(msgListener -> msgListener.onMessage(message));				
			});
		}
		
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
			case MessageHelper.WELCOME_MESSAGE:
				cc.appendMessageText(-1, "[SERVER]: " + message.getMessage());
				break;
			case MessageHelper.REQ_CHANGES:
				
				break;
			case MessageHelper.ERROR_MESSAGE:
				cc.appendMessageText(1,  
						"[SERVER] Error \""+message.getCode()+"\": " + 
								message.getMessage());
				break;
			case MessageHelper.DISABLE_ENCRYPTION:
				cc.appendMessageText(1, "[SERVER] Se ha deshabilitado el metodo de cifrado.");
				EncryptionService.getInstance().disableCipher();
				//this.setCipher(null);
				break;
		}
	}
	
	public void sendMessage(Message msg){
		msg.setUserSource(this.usuario);
		msg.setTimestamp(LocalDateTime.now());
		
		String jsonMessage = new Gson().toJson(msg, Message.class);
		
		String message = null;
		
		//if(this.cipher != null){ //Si hay un cifrado activo
		if(this.encryptionService.cipherActive()){ //Si hay un cifrado activo
			message = this.encryptionService.getCipher().encode(jsonMessage); //Cifrar el mensaje
		}else{ //De lo contrario, enviarlo en texto plano.
			message = jsonMessage; 
		}
		
		try{
			System.out.println("[DG - SendMessage]: "+msg.toString());
			if(this.encryptionService.cipherActive())	System.out.println("[DG - SendMessage Encrypted]: " + message);
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
	
	/*public void setCipher(CipherBase cipher){
		this.cipher = cipher;
	}
	
	public CipherBase getCipher(){
		return this.cipher;
	}*/
	
	/*public void enableEncryptionAlgorithm(){
		if(this.encyptAlgSelected.getAlgorithmType() == EncryptionAlgorithms.SYNC_CIPHER){ //Tipo de cifrado
			if(this.encyptAlgSelected.getKeyType() == Long.class){ //Tipo de llave
				String checkKey = this.encyptAlgSelected.getProperties().get("key");
				if(KeyUtils.isValidKey(checkKey, encyptAlgSelected)){ //Validar llave
					this.cipher = new CipherFactory<String, Long>().getCipher(this.encyptAlgSelected.getAlgorithm());
					this.cipher.setKey(KeyUtils.getLongKey(checkKey));
				}
				
			}else if(this.encyptAlgSelected.getKeyType() == String.class){
				String checkKey = this.encyptAlgSelected.getProperties().get("key");
				if(KeyUtils.isValidKey(checkKey, encyptAlgSelected)){ //Validar llave
					this.cipher = new CipherFactory<String, String>().getCipher(this.encyptAlgSelected.getAlgorithm());
					this.cipher.setKey(KeyUtils.getStringKey(checkKey));
				}
			}
		}
	}*/
	
	/*public void disableEncryptionAlgorithm(){
		this.cipher = null;
		//this.encyptAlgSelected = null;
	}*/
	
	/*public void setEncryptionAlgorithmSelected(EncryptionAlgorithm encAlg){
		this.encyptAlgSelected = encAlg;
	}
	
	public EncryptionAlgorithm getEncryptionAlgorithmSelected(){
		return this.encyptAlgSelected;
	}*/
	
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
	
	public MessageListener addListener(MessageListener msgListener){
		this.messageListeners.add(msgListener);
		
		return msgListener;
	}
	
	public boolean removeListener(MessageListener msgListener){
		
		return this.messageListeners.remove(msgListener);
	}
	
	public void removeListener(MessageListener... listeners){
		this.messageListeners.removeAll(Arrays.asList(listeners));
	}
}
