import static org.junit.Assert.*;

import org.junit.Test;

import mx.jalan.Security.Algorithms.CaesarCipher;

public class CipherTests {

	@Test
	public void test() {
		CaesarCipher c = new CaesarCipher(142);
		System.out.println(c.validate("Hola123214_!"));
	}

}
