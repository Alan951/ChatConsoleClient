import static org.junit.Assert.*;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import org.junit.Test;

import mx.jalan.Security.CipherFactory;
import mx.jalan.Security.EncryptionAlgorithms;
import mx.jalan.Security.Algorithms.CaesarCipher;
import mx.jalan.Security.Algorithms.CipherBase;

public class CipherTests {

	@Test
	public void test() {
		PrivateKey privateKey;
		//String privateKeyString = "AJOnAeTfeU4K+do5QdBM2BQUhfrRI2rYf/Gk4";
		PublicKey publicKey;
		//String publicKeyString = "HolaHolaHolaHolaHolaHolaHola";
		
		String textToCipher = "{\"code\":0,\"action\":\"msg\",\"message\":\"¿como andamos?\",\"userSource\":{\"nombre\":\"Jorge\"},\"timestamp\":{\"date\":{\"year\":2018,\"month\":5,\"day\":4},\"time\":{\"hour\":19,\"minute\":20,\"second\":7,\"nano\":913000000}}}{\"code\":0,\"action\":\"msg\",\"message\":\"¿como andamos?\",\"userSource\":{\"nombre\":\"Jorge\"},\"timestamp\":{\"date\":{\"year\":2018,\"month\":5,\"day\":4},\"time\":{\"hour\":19,\"minute\":20,\"second\":7,\"nano\":913000000}}}";
		String textEncrypted = null;
		String textDecrypted = null;
		
		KeyFactory keyFactory;
		
		try{
			keyFactory = KeyFactory.getInstance("RSA");
			
			byte privateKeyData[] = Files.readAllBytes(Paths.get("C:\\Users\\Ck\\Desktop\\keys\\private_key.der"));
			
			PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyData);
			
			privateKey = keyFactory.generatePrivate(privateKeySpec);
			
			byte publicKeyData[] = Files.readAllBytes(Paths.get("C:\\Users\\Ck\\Desktop\\keys\\public_key.der"));
			
			
			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyData);
			
			publicKey = keyFactory.generatePublic(publicKeySpec);
			
			Cipher cipherEncrypt = Cipher.getInstance("RSA");
			cipherEncrypt.init(Cipher.ENCRYPT_MODE, privateKey);
			byte encryptedData[] = cipherEncrypt.doFinal(textToCipher.getBytes());
			//textEncrypted = new String(cipherEncrypt.doFinal(textToCipher.getBytes()));
			textEncrypted = new String(encryptedData);
			
			Cipher cipherDecrypt = Cipher.getInstance("RSA");
			cipherDecrypt.init(Cipher.DECRYPT_MODE, publicKey);
			byte decryptedData[] = cipherDecrypt.doFinal(encryptedData);
			textDecrypted = new String(decryptedData);
			//textDecrypted = new String(cipherDecrypt.doFinal(textEncrypted.getBytes()));
			
			System.out.println("TextToCipher: "+textToCipher);
			System.out.println("TextEncrypted: "+textEncrypted);
			System.out.println("TextDecrypted: "+textDecrypted);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}

