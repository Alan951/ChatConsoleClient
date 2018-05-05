package mx.jalan.Controller;

import java.net.ConnectException;
import java.net.URI;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.jalan.Model.Message;
import mx.jalan.Model.MessageHelper;
import mx.jalan.Model.TextMessage;
import mx.jalan.Model.User;
import mx.jalan.WebSocket.ClientChat;
import mx.jalan.WebSocket.MessageConstructor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;


public class ConsoleController {

	@FXML TextFlow consoleArea;
	@FXML TextField txtLine;
	
	@FXML ScrollPane scrollPane;
	
	@FXML MenuItem mConectar;
	@FXML MenuItem mDesconectar;
	@FXML MenuItem mGstcifr;
	@FXML MenuItem mSalir;
	@FXML Menu	   menuArchivo;
	
	private ClientChat client;
	
	private boolean listenEnableEncryption;
	
	private Set<User> users = new HashSet<User>();
	
	private final String URL = "ws://192.168.0.3:8080/ChatWebSocket/chat";
	
	private final static String CHAT_TITLE = "Console Chat with WebSocket";
	private Stage stage;
	
	@FXML
	public void initialize(){
		scrollPane.setFitToWidth(true);
		
		
		for(int x = 0 ; x < 100 ; x++){
			consoleArea.getChildren().add(new Text("Hola"));
		}
		
		consoleArea.getChildren().clear();
		
	}
	
	public void init(Stage stage){
		this.stage = stage;
		
		stage.setTitle(CHAT_TITLE);
		//onConnect("Jorge"+Math.random());
		this.initListeners();
	}
	
	public ConsoleController(){}
	
	@FXML
	private void onEnter(ActionEvent e){
		String input = txtLine.getText();
		
		boolean scrollDown = scrollPane.getVvalue() >= 0.70 || scrollPane.getVvalue() == 0;
		
		txtLine.setText("");
		
		if(input.startsWith("/")){ //Chaca que el input sea un comando
			command(input);
		}else{ //SI no es un comando, es un mensaje
			String content = input;
	
			String msg = null;
			
			if(client != null)
				msg = client.getUser().getNombre()+": "+content;
			else
				msg = content;
			
			appendMessageText(-1, msg);
			
			if(client != null)
				client.sendMessage(MessageConstructor.createMessage(content));
		}
		
		if(scrollDown)
			scrollPane.setVvalue(1.0);
		
	}
	
	private void command(String command){
		if(command.equalsIgnoreCase("/clear") || command.equalsIgnoreCase("/cls")){
			consoleArea.getChildren().clear();
		}else if(command.equalsIgnoreCase("/help") || command.equalsIgnoreCase("/h") || command.equalsIgnoreCase("/ayuda")){
			Text text = new Text(getHelp());
			
			consoleArea.getChildren().add(text);
		}else if(command.equalsIgnoreCase("/exit") || command.equalsIgnoreCase("/salir")){
			Platform.setImplicitExit(false);
			Platform.exit();
		}else if(command.startsWith("/connect".toLowerCase()) || command.startsWith("/conn".toLowerCase())){
			String []data = command.split(" ");
			String usr = data[1];
			String url = null;
			if(data.length == 3){
				url = data[2];
				String port = "8080";
				
				if(!url.contains(":")){
					url = url+":"+port;
				}
				
				url = "ws://"+ url +"/ChatWebSocket/chat";
			}
			
			try{
				System.out.println(url);
				onConnect(usr, url);
			}catch(ConnectException e){}
			
			/*if(client == null || client.getSession() == null || !client.getSession().isOpen()){
				try{
					client = new ClientChat(new URI(URL), new User(usr));
					client.setConsole(this);
					Text ok = new Text("[Ha sido conectado al WS]\n");
					
					ok.setStyle("-fx-font-weight: bold; "
							+ "-fx-fill: rgb(81, 48, 45);");
					consoleArea.getChildren().add(ok);
					
					//client.sendMessage(client.createNewUsr());
					client.sendMessage(MessageConstructor.registerNewUser());
					stage.setTitle(CHAT_TITLE+" | "+client.getUser().getNombre());
					
				}catch(ConnectException ce){
					Text error = new Text("[Error al conectar WS]: "+ce);
					error.setStyle("-fx-font-weight: bold; "); 
					
					consoleArea.getChildren().add(error);
					ce.printStackTrace();
				}catch(Exception e){
					Text error = new Text("[Error al conectar WS]: "+e);
					error.setStyle("-fx-font-weight: bold; ");
					
					consoleArea.getChildren().add(error);
					e.printStackTrace();
				}
			}else{
				Text errorCommand = new Text("[Actualmente esta conectado a: \""+client.getSession().getRequestURI().toString()+"\"]\n");
				errorCommand.setStyle("-fx-font-weight: bold;");
				
				consoleArea.getChildren().add(errorCommand);
			}*/
			
		}else if(command.equalsIgnoreCase("/disconnect") || command.equalsIgnoreCase("/dis")){
			onDisconnect();
		}else if(command.startsWith("/pm".toLowerCase())){			
			String []data = command.split(" ");
			String usr = data[1];
			String msg = "";
			
			for(int x = 2 ; x < data.length ; x++){
				msg += data[x];
				if(data.length > x) msg += " ";
			}
			
			msg = msg.trim();
			
			if(msg == null || msg.isEmpty()){
				appendMessageText(1, "Falto especificar el mensaje");
				return;
			}
			
			//client.sendMessage(client.createPrivateMessage(msg, usr));
			client.sendMessage(MessageConstructor.createPrivateMessage(msg, new User(usr)));
			
			appendMessageText(-1, "[PM para \""+usr+"\"] "+client.getUser().getNombre()+": "+msg);
		}else if(command.equalsIgnoreCase("/online")){
			//TODO verificar que este conectado
			
			if(users.size() == 0){
				//client.sendMessage(client.creatRequestChanges());
			}
			
		}else{
			Text errorCommand = new Text("[Comando \""+command+"\" no es correcto]\n");			
			errorCommand.setStyle("-fx-font-weight: bold;");
			
			consoleArea.getChildren().add(errorCommand);
		}
	}
	
	
	
	public void appendMessageText(int special, String msg){
		Platform.runLater(() -> {
			Text text = new Text(msg);
			
			
			if(special == 1){ //error style
				//text.setStyle("-fx-font-weight: bold; -fx-text-stroke-color: #fff; -fx-fill: #cc0000"); 
				text.setStyle("-fx-font-weight: bold; -fx-text-stroke-color: #fff;");
			}
			
			consoleArea.getChildren().add(text);
			consoleArea.getChildren().add(new Text("\n"));
		});
	}
	
	public void appendMessageText(int special, TextMessage textMessage){
		Platform.runLater(() -> {			
			if(special == 1) //error style 
				textMessage.setStyle("-fx-font-weight: bold; -fx-text-stroke-color: #fff;");
			
			
			consoleArea.getChildren().add(textMessage);
			consoleArea.getChildren().add(new Text("\n"));
		});
	}
	
	
	
	private String getHelp(){
		String help = "";
		help = "IMPORTANTE: Solo es posible ingresar un comando a la vez y estos van seguidos de una diagonal. Ej: \"/conn Jorge\"\n"
				+ "\t[Comando] - [Descripci�n]\n"
				+ "\t/conn <nombre usuario> <ip:port> � /connect <nombre usuario> <ip:port> - Sirve para conectarse al websocket. Es necesario especificar el nombre de usuario.\n"
				+ "\t/online - Ver todos los usuarios conectados en el chat.\n"
				+ "\t/dis � /disconnect - Sirve para desconectarse del websocket.\n"
				+ "\t/clear - Limpia la terminal.\n"
				+ "\t/pm <nombre usuario> - Envia un mensaje privado a un usuario en especifico.\n"
				+ "\t/help - Ver esto.\n"
				+ "\t/exit - Salir del programa.\n";
		
		return help;
	}
	
	public ClientChat onConnect(String usrName, String url)throws ConnectException{
		if(url == null)
			url = this.URL;
		
		
		if(client == null || client.getSession() == null || !client.getSession().isOpen()){
			try{
				client = new ClientChat(new URI(url), new User(usrName));
				client.setConsole(this);
				
				this.appendMessageText(1, "[Ha sido conectado al servidor]");
				
				client.sendMessage(MessageConstructor.registerNewUser());
				stage.setTitle(CHAT_TITLE+" | "+client.getUser().getNombre());
				
				this.initListeners();
				
				mngMenuItem("conectar", false); //Desactivar menu item "Conectar"
				mngMenuItem("desconectar", true); //Activar menu item "Desconectar"
				mngMenuItem("gesti�n cifrado", true); //Activar menu item "Desconectar"
				
				return client;
				
			}catch(ConnectException ce){
				this.appendMessageText(1, "[Error al intentarse conectar con el servidor]: Verifica que el equipo alcance al servidor.");
				//ce.printStackTrace();
				
				throw ce;
			}catch(Exception e){
				Text error = new Text("[Error al conectar WS]: "+e);
				error.setStyle("-fx-font-weight: bold; ");
				
				consoleArea.getChildren().add(error);
				e.printStackTrace();
			}
		}else{
			Text errorCommand = new Text("[Actualmente esta conectado a: \""+client.getSession().getRequestURI().toString()+"\"]\n");
			errorCommand.setStyle("-fx-font-weight: bold;");
			
			consoleArea.getChildren().add(errorCommand);
		}
		
		return null;
		
	}
	
	@FXML
	public void onDisconnect(){
		if(client == null || client.getSession() == null){
			Text errorCommand = new Text("[Actualmente no esta conectado a ningun WS]\n");
			errorCommand.setStyle("-fx-font-weight: bold;");
			
			consoleArea.getChildren().add(errorCommand);
		}else{
			Text ok = new Text("[Desconectado del servidor]\n");
			
			ok.setStyle("-fx-font-weight: bold; "
					+ "-fx-fill: rgb(81, 48, 45);");
			
			consoleArea.getChildren().add(ok);
			client.disconnect();
			this.client = null;
			stage.setTitle(CHAT_TITLE);
			
			mngMenuItem("conectar", true); //Conectar menu item "Conectar"
			mngMenuItem("desconectar", false); //Desactivar menu item "Desconectar"
			mngMenuItem("gesti�n cifrado", false);
		}
	}
	
	public boolean mngMenuItem(String menuItemName, boolean enable){
		Optional<MenuItem> m = menuArchivo.getItems().filtered((item) -> item.getText().equalsIgnoreCase(menuItemName)).stream().findFirst();
		
		if(m.isPresent()){
			m.get().setDisable(!enable);
			
			return true;
		}else{
			return false;
		}
	}
	
	@FXML
	public void onOpenCipherMng(){
		System.out.println("[*] onOpenCipherMng");
		
		openCipherMng(null);
	}
	
	public void openCipherMng(String cipherName){
		this.listenEnableEncryption = false;
		
		FXMLLoader loader = new FXMLLoader();
		AnchorPane anchor = null;
		
		loader.setLocation(this.getClass().getResource("/CipherMng.fxml"));
		
		try{
			anchor = (AnchorPane) loader.load();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		Scene scene = new Scene(anchor);
		scene.getStylesheets().add(this.getClass().getResource("/CipherMngStyle.css").toExternalForm());
		
		Stage stage = new Stage();

		((CipherMngController)loader.getController()).init(stage, this, cipherName);
		
		stage.setTitle("Gesti�n Cifrado");
		stage.setResizable(false);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		stage.showAndWait();
		
		this.listenEnableEncryption = true;
	}
	
	@FXML
	public void onOpenConnect(){
		System.out.println("[*] onOpenConnect");
		
		FXMLLoader loader = new FXMLLoader();
		AnchorPane anchor = null;
		
		loader.setLocation(this.getClass().getResource("/ConnectView.fxml"));
		
		try{
			anchor = (AnchorPane) loader.load();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		Scene scene = new Scene(anchor);
		scene.getStylesheets().add(this.getClass().getResource("/CipherMngStyle.css").toExternalForm());
		
		Stage stage = new Stage();

		ConnectController connController = (ConnectController)loader.getController();
		connController.init(this, stage);
		
		stage.setTitle("Conectar con el servidor.");
		stage.setResizable(false);

		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		stage.showAndWait();
		if(connController.getClientChat() != null){
			this.client = connController.getClientChat();
			
			//Initialize
		}
	}
	
	public void initListeners(){
		if(this.client == null)
			return;
		
		this.listenEnableEncryption = true;
		
		this.client.addListener((message) -> {
			if(message.getAction().equals(MessageHelper.ENABLE_ENCRYPTION) && listenEnableEncryption){
				listenEnableEncryption = false;
				
				this.appendMessageText(-1, "[INFO] El servidor utiliza un algoritmo de cifrado. Ve a la gesti�n de cifrado y activalo o no podras ver los mensajes.");
				
				Alert alerta = new Alert(AlertType.INFORMATION);
				alerta.setTitle("Notificaci�n de cifrado.");
				alerta.setHeaderText(null);
				alerta.setContentText("Se ha habilitado en el servidor un metodo de cifrado.\nConfigura el metodo de cifrado y sus llaves para poder ver y enviar mensajes.");
				alerta.showAndWait();
				
				openCipherMng(message.getMessage());
			}
		});
	}
	
	public Set<User> getUsers(){
		return users;
	}
	
	public ClientChat getClientChat(){
		return this.client;
	}
	
}
