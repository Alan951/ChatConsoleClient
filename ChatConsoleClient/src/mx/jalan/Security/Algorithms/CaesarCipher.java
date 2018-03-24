package mx.jalan.Security.Algorithms;

import mx.jalan.Security.CipherBase;

public class CaesarCipher implements CipherBase<String>{

	private long key;
	
	private static final String LETTERS = "ABCDEFGHIJQLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz1234567890!#$%&()=?¡+-";
	
	public CaesarCipher(long key){
		this.key = key;
	}
	
	public int getPosLetterInLetters(int letterToSearch){
		for(int x = 0 ; x < LETTERS.length() ; x++)
			if(LETTERS.charAt(x) == LETTERS.charAt(letterToSearch))	return x;
		
		return -1;
	}
	
	public int searchChar(char letter){
		for(int x = 0 ; x < LETTERS.length() ; x++)
			if(LETTERS.charAt(x) == letter)	return x;
		
		return -1;
	}
	
	public boolean validate(String text){
		for(char c : text.toCharArray()){
			if(!LETTERS.contains(Character.toString(c))){
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public String encode(String textToCipher) {
		char text[] = textToCipher.toCharArray();
		
		for(int x = 0 ; x < text.length ; x++){ //Recorrer cada letra del texto
			char n = text[x];
			int pos = getPosLetterInLetters(searchChar(n));
			
			if((pos + (int)key) >= LETTERS.length()){
				int u = (int)key + pos;

				while(u >= LETTERS.length())	
					u -= LETTERS.length();
				
				n = LETTERS.charAt(u);
			}else{
				pos += (int)key; 
				n = LETTERS.charAt(pos);
			}
			
			text[x] = n;
		}
		
		return new String(text);
	}

	@Override
	public String decode(String textToDecipher) {
		char text[] = textToDecipher.toCharArray();
		
		for(int x = 0 ; x < text.length ; x++){
			char n = text[x];
			int pos = getPosLetterInLetters(searchChar(n));
			
			if((int)key - pos >= 0){
				int u = pos - (int)key;
				
				while(u <= 0)
					u += LETTERS.length();
				
				n = LETTERS.charAt(u);
			}else{
				pos -= (int)key;
				n = LETTERS.charAt(pos);
			}
		
			text[x] = n;
		}
		
		return new String(text);
	}

	@Override
	public String getCipherName() {
		return "Caesar Cipher";
	}
	
	public void setKey(long key){
		this.key = key;
	}
	
	public long getKey(){
		return this.key;
	}

}
