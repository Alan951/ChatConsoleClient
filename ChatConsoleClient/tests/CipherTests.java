import static org.junit.Assert.*;

import org.junit.Test;

import mx.jalan.Security.CipherFactory;
import mx.jalan.Security.EncryptionAlgorithms;
import mx.jalan.Security.Algorithms.CaesarCipher;
import mx.jalan.Security.Algorithms.CipherBase;

public class CipherTests {

	@Test
	public void test() {
		CipherBase<String, Long> cipher;
		CipherFactory<String, Long> factory = new CipherFactory<>();
		
		cipher = factory.getCipher(EncryptionAlgorithms.CAESAR);
		cipher.setKey(1L);
		
		String text = "ABCDEFGHIJQLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz1234567890!#$%&()=?¡+---AABB";
		String textCipher;
		String textDecrypter;
		
		textCipher = cipher.encode(text);
		textDecrypter = cipher.decode(textCipher);
		
		System.out.println("Texto  : "+ text);
		System.out.println("Cifrado: "+ textCipher);
		System.out.println("Descifr: "+ textDecrypter);
		
		//System.out.println(cipher.encode("ABCDEFGHIJQLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz1234567890!#$%&()=?¡+-"));
		//System.out.println(cipher.decode("fzQkc3SmJkpxMDQiZ4Sqc35jPjQud3djMDQuaYÑ1ZXemJkpjbH0tZTFjMDQ2d3WzV302dnÑmJkq8Jn6wcXQzaTJ7JlqwdnemJñ!tJñSqcXW1eHGudDJ7fzQlZYSmJkq8JñmmZYJjPkJxNUhtJn2wcñSpJkp!MDQlZYljPki0MDQ!bX2mJkq8JniweYJjPkF4MDQubX62eHVjPkN5MDQ1aXÑwcnLjPkL1MDQvZX6wJkp4PElxNEBxNEC0gY!?"));
	}

}

