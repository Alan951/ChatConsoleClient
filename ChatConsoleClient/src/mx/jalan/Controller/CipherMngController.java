package mx.jalan.Controller;

import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.Gson;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import mx.jalan.Model.EncryptionAlgorithm;
import mx.jalan.Model.Message;
import mx.jalan.Model.MessageHelper;
import mx.jalan.WebSocket.MessageConstructor;
import mx.jalan.WebSocket.MessageListener;

public class CipherMngController {

	@FXML private HBox cipherButtonsContainer;
	@FXML private Label lblCifAct;
	@FXML private GridPane gridPaneEncProps;
	
	private ToggleGroup toggleGroup;
	
	private ConsoleController cc;
	
	private Stage stage;
	
	@FXML
	public void initialize(){
		this.toggleGroup = new ToggleGroup();
		this.gridPaneEncProps.setHgap(10);
	}
	
	public CipherMngController(){}
	
	public void init(Stage stage, ConsoleController cc, String cifradoActual){
		this.cc = cc;
		this.stage = stage;
		
		this.cipherButtonsContainer.getChildren().forEach((item) -> {
			ToggleButton btn = (ToggleButton)item;
			btn.setToggleGroup(this.toggleGroup);
			
			for(int x = 0 ; x < this.cc.getClientChat().getEncryptionSupport().size() ; x++){
				if(this.cc.getClientChat().getEncryptionSupport().get(x).getAlgorithm().equals(btn.getText())){
					btn.setUserData(this.cc.getClientChat().getEncryptionSupport().get(x));
					break;
				}
			} 
		});
		
		MessageListener onEnableEncryptionListener;
		onEnableEncryptionListener = this.cc.getClientChat().addListener((msg) -> {
			if(msg.getAction().equals(MessageHelper.ENABLE_ENCRYPTION)){
				
				this.lblCifAct.setText("Cifrado actual: " + msg.getMessage() + ".");
				this.cc.getClientChat().enableEncryptionAlgorithm();
			}
			
			if(msg.getAction().equals(MessageHelper.DISABLE_ENCRYPTION)){
				this.lblCifAct.setText("Sin cifrado.");
			}
		});
		
		stage.setOnCloseRequest((event) -> {
			System.out.println("[*] onCloseRequest invoked");
			this.cc.getClientChat().removeListener(onEnableEncryptionListener);
		});
		
		
	}
	
	@FXML
	public void onSelectEncryption(ActionEvent event){
		this.gridPaneEncProps.getChildren().clear();
		if(this.toggleGroup.getSelectedToggle() == null || this.toggleGroup.getSelectedToggle().getUserData() == null)
			return;

		EncryptionAlgorithm encSel = (EncryptionAlgorithm)this.toggleGroup.getSelectedToggle().getUserData();
		System.out.println(this.toggleGroup.getSelectedToggle().getUserData());
		
		int rowIndx = 0;
		
		for(Map.Entry<String, String> t : encSel.getProperties().entrySet()){
			this.gridPaneEncProps.add(new Label(t.getKey()), 0, rowIndx);
			TextField textField = new TextField(t.getValue());
			textField.setUserData(t.getKey());
			this.gridPaneEncProps.add(textField, 1, rowIndx);
			
			rowIndx++;
		}
	}
	
	@FXML
	private void onAplicar(){
		if(this.toggleGroup.getSelectedToggle() == null)
			return;
		
		//TODO Verificar que exista un algoritmo activo.
		
		EncryptionAlgorithm encSel = (EncryptionAlgorithm)this.toggleGroup.getSelectedToggle().getUserData();
		
		if(isValidateKeys()){
			encSel = this.setKeys(encSel);
			
			this.cc.getClientChat().setEncryptionAlgorithmSelected(encSel);
			this.cc.getClientChat().enableEncryptionAlgorithm();
			System.out.println("EncryptionAlgorithm aplicado: " + this.cc.getClientChat().getEncryptionAlgorithmSelected());
		}
	}
	
	@FXML
	private void onDeshabilitar(){
		
	}
	
	@FXML
	private void onSolicitar(){
		if(this.toggleGroup.getSelectedToggle() == null)
			return;
		
		EncryptionAlgorithm encSel = (EncryptionAlgorithm)this.toggleGroup.getSelectedToggle().getUserData();
		
		if(isValidateKeys()){
			//Set key properties to object.
			encSel = this.setKeys(encSel);
			
			
			this.cc.getClientChat().setEncryptionAlgorithmSelected(encSel);
			this.cc.getClientChat().sendMessage(MessageConstructor.enableEncryption(encSel));
		}
	}
	
	/*
	 * Verifica que las llaves esten escritas correctamente.
	 * En caso de que un campo este incorrecto agrega la clase "errorBorder" y la función regresa false
	 * */
	private boolean isValidateKeys(){
		this.gridPaneEncProps.getChildren()
		.filtered(i -> i instanceof TextField)
		.forEach(i -> i.getStyleClass()
				.removeIf(className -> className.equals("errorBorder"))
		);
	
		//Si un campo no es valido agregarle la clase		
		this.gridPaneEncProps.getChildren()
			.filtered(i -> i instanceof TextField)
			.filtered(txt -> !isValidKey(((TextField)txt).getText()))
			.forEach((txtInvalid) -> txtInvalid.getStyleClass().add("errorBorder"));
		
		boolean anyTextFieldHaveClassError = this.gridPaneEncProps.getChildren()
											.stream()
											.anyMatch(i -> i.getStyleClass()
													.stream()
													.anyMatch(className -> className.equals("errorBorder")));
		
		return !anyTextFieldHaveClassError;
	}
	
	/*
	 * Rellena las propiedades de las llaves en el modelo EncryptionAlgorithm
	 * */
	private EncryptionAlgorithm setKeys(EncryptionAlgorithm encSelected){
		for(Map.Entry<String, String> props : encSelected.getProperties().entrySet()){
			Optional<Node> node = this.gridPaneEncProps.getChildren()
				.filtered(i -> i instanceof TextField && ((TextField)i).getUserData().equals(props.getKey()))
				.stream().findFirst();
			
			if(node.isPresent()){
				String value = ((TextField)node.get()).getText();
				encSelected.getProperties().put(props.getKey(), value);
			}
		}
		
		return encSelected;
	}
	
	//TODO ¿All keys types is long?
	private boolean isValidKey(String key){
		if(!key.matches("(?<=\\s|^)\\d+(?=\\s|$)"))
			return false;
		
		if(!NumberUtils.isCreatable(key))
			return false;
		
		return true;
	}
	
}
