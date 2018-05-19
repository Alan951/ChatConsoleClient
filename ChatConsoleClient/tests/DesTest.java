import org.junit.Test;

import mx.jalan.Security.CipherFactory;
import mx.jalan.Security.EncryptionAlgorithms;
import mx.jalan.Security.Algorithms.CipherBase;

public class DesTest {

	@Test
	public void test(){
		CipherBase cipher = new CipherFactory<String, String>()
				.getCipher(EncryptionAlgorithms.DES);
		
		String mensaje = "sueloasdasd12312345678poposuelo12312345678poposuelo12312345678poposuelo12312345678poposuelo12312345678popo";
		String key = "Hola";
		String mensajeCifrado = null;
		String mensajeDescifrado = null;
		
		cipher.setKey(key);
		
		mensajeCifrado = cipher.encode(mensaje);
		mensajeDescifrado = cipher.decode(mensajeCifrado);
		
		System.out.println("Mensaje: "+ mensaje + "\nMensaje Cifrado: "+ mensajeCifrado + "\nMensaje Descifrado: "+ mensajeDescifrado);
	}
	
}
