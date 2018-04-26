package mx.jalan.Controller;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import mx.jalan.Model.MessageHelper;
import mx.jalan.Model.User;
import mx.jalan.WebSocket.ClientChat;
import mx.jalan.WebSocket.MessageConstructor;
import mx.jalan.WebSocket.MessageListener;

public class ConnectController {

	@FXML
	private TextField txtIp;
	
	@FXML
	private TextField txtUsername;
	
	private ConsoleController cc;
	
	private ClientChat client;
	
	private Stage stage;
	
	public ConnectController(){}
	
	@FXML
	public void initialize(){
		this.txtIp.textProperty().addListener((ov, old, value) -> {			
			if(!validateIp(value)){
				if(this.txtIp.getStyleClass().indexOf("errorBorder") == -1)
					this.txtIp.getStyleClass().add("errorBorder");
			}else{
				if(this.txtIp.getStyleClass().indexOf("errorBorder") != -1)
					this.txtIp.getStyleClass().remove("errorBorder");
			}
		});
	}
	
	public void init(ConsoleController cc, Stage stage){
		this.cc = cc;
		
		this.stage = stage;
	}
	
	public boolean validateIp(String text){
		String pattern = "[0-9]+(?:\\.[0-9]+){3}(:[0-9])?";
		
		return text.matches(pattern);
	}
	
	@FXML
	public void onAccept(){
		if(!validateIp(txtIp.getText()))
			return;
		
		if(txtUsername.getText().trim().isEmpty())
			return;
		
		connect(txtIp.getText(), txtUsername.getText());
	}
	
	public void connect(String url, String username){
		String _url = null;
		String port = "8080";
		
		if(!url.contains(":")){
			_url = url+":"+port;
		}
		
		_url = "ws://"+ _url +"/ChatWebSocket/chat";
		
		try{
			client = new ClientChat(new URI(_url), new User(username));
			
			client.setConsole(this.cc);
			
			
			MessageListener msgListener = client.addListener((msg) -> {
							
				if(msg.getAction().equals(MessageHelper.ERROR_MESSAGE) 
						&& msg.getCode() == MessageHelper.USERNAME_UNAVAILABLE_CODE){
					
					
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Usuario no disponible");
					alert.setHeaderText(null);
					alert.setContentText("El usuario que escogiste ya esta siendo usado por otra persona.");
					alert.show();
					
					this.client = null;
				}else if(msg.getAction().equals(MessageHelper.WELCOME_MESSAGE)){
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Conectado");
					alert.setHeaderText(null);
					alert.setContentText("Haz sido conectado con el servidor correctamente.");
					alert.showAndWait();
					
					this.stage.close();
				}
			});
			
			this.stage.setOnHiding((event) -> {
				System.out.println("[*] onClose ConnectView");
				Platform.runLater(() -> this.client.removeListener(msgListener));
			
			});
			
			client.sendMessage(MessageConstructor.registerNewUser());
		}catch(ConnectException e){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error al conectarse");
			alert.setHeaderText(null);
			alert.setContentText("No se ha podido conectarse con el servidor.");
			alert.showAndWait();
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	public ClientChat getClientChat(){
		
		
		return this.client;
	}
	
}
