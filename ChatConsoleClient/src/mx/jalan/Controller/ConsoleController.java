package mx.jalan.Controller;

import java.net.ConnectException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import mx.jalan.Model.User;
import mx.jalan.WebSocket.ClientChat;
import javafx.scene.control.ScrollPane;


public class ConsoleController {

	@FXML TextFlow consoleArea;
	@FXML TextField txtLine;
	
	@FXML ScrollPane scrollPane;
	
	private ClientChat client;
	
	private Set<User> users = new HashSet<User>();
	
	private static final String URL = "ws://192.168.0.6:8080/ChatWebSocket/chat";
	
	@FXML
	public void initialize(){
		scrollPane.setFitToWidth(true);
		
		for(int x = 0 ; x < 100 ; x++){
			consoleArea.getChildren().add(new Text("Hola"));
		}
		
		consoleArea.getChildren().clear();
	}
	
	public void init(){}
	
	public ConsoleController(){}
	
	@FXML
	private void onEnter(ActionEvent e){
		String input = txtLine.getText();
		
		txtLine.setText("");
		
		if(input.startsWith("/")){ //Chaca que el input sea un comando
			command(input);
		}else{
			String content = input;
			Text texto = new Text(client.getUser().getNombre()+": "+content+"\n");
			
			consoleArea.getChildren().add(texto);
			
			//TODO verificar que el usuario este logueado
			client.sendMessage(client.createMessage(content));
		}
	}
	
	private void command(String command){
		if(command.equalsIgnoreCase("/clear")){
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
			
			
			if(client == null || client.getSession() == null || !client.getSession().isOpen()){
				try{
					client = new ClientChat(new URI(URL), new User(usr));
					client.setConsole(this);
					Text ok = new Text("[Ha sido conectado al WS]\n");
					
					ok.setStyle("-fx-font-weight: bold; "
							+ "-fx-fill: rgb(81, 48, 45);");
					consoleArea.getChildren().add(ok);
					
					client.sendMessage(client.createNewUsr());
					
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
			}
			
		}else if(command.equalsIgnoreCase("/disconnect") || command.equalsIgnoreCase("/dis")){
			if(client == null || client.getSession() == null){
				Text errorCommand = new Text("[Actualmente no esta conectado a ningun WS]\n");
				errorCommand.setStyle("-fx-font-weight: bold;");
				
				consoleArea.getChildren().add(errorCommand);
			}else{
				Text ok = new Text("[Desconectado del WS]\n");
				
				ok.setStyle("-fx-font-weight: bold; "
						+ "-fx-fill: rgb(81, 48, 45);");
				
				consoleArea.getChildren().add(ok);
				client.disconnect();
			}
		}else if(command.startsWith("/pm".toLowerCase())){
			//TODO verificar que este conectado
			
			String []data = command.split(" ");
			String usr = data[1];
			String msg = "";
			
			for(int x = 2 ; x < data.length ; x++){
				msg += data[x];
				if(data.length > x) msg += " ";
			}
			
			msg = msg.trim();
			
			client.sendMessage(client.createPrivateMessage(msg, usr));
			
			appendMessageText(-1, "[PM para \""+usr+"\"] "+client.getUser().getNombre()+": "+msg+"\n");
		}else if(command.equalsIgnoreCase("/online")){
			//TODO verificar que este conectado
			
			if(users.size() == 0){
				client.sendMessage(client.creatRequestChanges());
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
				text.setStyle("-fx-font-weight: bold; -fx-text-stroke-color: #fff; -fx-fill: red"); 
			}
			
			consoleArea.getChildren().add(text);
		});
	}
	
	private String getHelp(){
		String help = "";
		help = "IMPORTANTE: Solo es posible ingresar un comando a la vez y estos van seguidos de una diagonal. Ej: \"/conn Jorge\"\n"
				+ "\t[Comando] - [Descripción]\n"
				+ "\t/conn <nombre usuario> ó /connect <nombre usuario> - Sirve para conectarse al websocket. Es necesario especificar el nombre de usuario.\n"
				+ "\t/online - Ver todos los usuarios conectados en el chat.\n"
				+ "\t/dis ó /disconnect - Sirve para desconectarse del websocket.\n"
				+ "\t/clear - Limpia la terminal.\n"
				+ "\t/pm <nombre usuario> - Envia un mensaje privado a un usuario en especifico.\n"
				+ "\t/help - Ver lista de todos los comandos.\n"
				+ "\t/exit - Salir del programa.\n";
		
		return help;
	}
	
	public Set<User> getUsers(){
		return users;
	}
	
}
