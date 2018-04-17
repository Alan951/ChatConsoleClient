package mx.jalan.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

public class CipherMngController {

	@FXML private HBox cipherButtonsContainer;
	@FXML private Label lblCiradoActual;
	
	private ToggleGroup toggleGroup;
	
	private ConsoleController cc;
	
	@FXML
	public void initialize(){
		System.out.println("Initialize called");
		this.toggleGroup = new ToggleGroup();
	}
	
	public CipherMngController(){}
	
	public void init(ConsoleController cc, String cifradoActual){
		System.out.println("init called");
		this.cc = cc;
		
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
	}
	
	@FXML
	public void onSelectEncryption(ActionEvent event){
		if(this.toggleGroup.getSelectedToggle().getUserData() == null)
			return;

		System.out.println(this.toggleGroup.getSelectedToggle().getUserData());
		
		
		
	}
	
}
