package mx.jalan.Security;

public interface CipherBase<T> {
	public String encode(T textToCipher);
	
	public String decode(T textToDecipher);
	
	public String getCipherName();
}
