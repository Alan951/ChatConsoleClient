package mx.jalan.Model;

import javafx.scene.text.Text;

public class TextMessage extends Text{

	private String plainText;
	private String cipherText;
	private static String INFO_TEXT_CIPHER = "* Texto cifrado... *";
	
	public TextMessage(String text, boolean isCipher){
		super(text);
		
		if(isCipher)
			cipherText = text;
		else
			plainText = text;
	}
	
	public void showPlainText(){
		if(plainText != null)
			this.setText(plainText);
		else
			this.setText(INFO_TEXT_CIPHER);
	}
	
	public void showCiperText(){
		if(cipherText != null)
			this.setText(cipherText);
		else
			this.setText(plainText);
	}
	
}
