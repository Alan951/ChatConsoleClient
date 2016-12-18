package mx.jalan.App;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mx.jalan.Controller.ConsoleController;

public class App extends Application{
	
	private static ConsoleController controlador;
	
	@Override
	public void start(Stage stage){
		AnchorPane anchorP = null;
		FXMLLoader loader = new FXMLLoader();
		
		
		loader.setLocation(this.getClass().getResource("../Controller/Console.fxml"));
		
		try{
			anchorP = (AnchorPane)loader.load();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		Scene scene = new Scene(anchorP);
		
		scene.getStylesheets().add(this.getClass().getResource("../Controller/Style.css").toString());
		
		stage.setTitle("Console Chat with WebSocket");
		stage.setScene(scene);
		
		controlador = loader.getController();
		controlador.init();
		stage.show();
	}

	public static void main(String []args){
		launch(args);
	}
	
}
