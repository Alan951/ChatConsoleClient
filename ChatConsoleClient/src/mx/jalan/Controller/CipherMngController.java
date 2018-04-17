package mx.jalan.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import mx.jalan.Model.EncryptionAlgorithm;

public class CipherMngController {

	@FXML private HBox cipherButtonsContainer;
	@FXML private Label lblCiradoActual;
	@FXML private GridPane gridPaneEncProps;
	
	private ToggleGroup toggleGroup;
	
	private ConsoleController cc;
	
	@FXML
	public void initialize(){
		System.out.println("Initialize called");
		this.toggleGroup = new ToggleGroup();
		this.gridPaneEncProps.setHgap(10);
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
		this.gridPaneEncProps.getChildren().clear();
		if(this.toggleGroup.getSelectedToggle() == null)
			return;

		EncryptionAlgorithm encSel = (EncryptionAlgorithm)this.toggleGroup.getSelectedToggle().getUserData();
		System.out.println(this.toggleGroup.getSelectedToggle().getUserData());
		
		int rowIndx = 0;
		
		encSel.getProperties().forEach((k, v) -> {
			Label l = new Label(k);
			l.setAlignment(Pos.CENTER_RIGHT);
			this.gridPaneEncProps.add(l, 0, rowIndx);
			//GridPane.setHalignment(l, HPos.RIGHT);
			
			this.gridPaneEncProps.add(new TextField(v), 1, rowIndx);
		});
		
		
	}
	
}
