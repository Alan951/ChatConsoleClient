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
		
		String text = "YlpuTWNvSzBXb3pMY28vNVZHUE1jSG0xV3BEa09sVHJWNG5NZklPMGE0bk9TMnIyWW5qUWdublpiSXpMY28vclluZkhjV3pGVWxIbE9uYjdYVks3T1lQdlZvekxjcEM1VVkyM2ZvTFpVcG5jVzJtNFVZdnlmb1B2Ykl6UFMxVDhWWnJMT0cvR1lablBUR1g1VldUSFBHNzhYVkhRUzJxN1ZHUE1PSUhaT1l6bE8zTHFhbmpEY1cvd2U0bmNUMmF5WW5QTFBvWDdVbFhjWDJiN1VZcnhnVzNHVFZUT1MycjJhbEszT0lIRlVWYlFTNWZyWW1mSVBXbnNkNW5QVUZEMVVZN1VlSUxaWFluUmRwUHJhV2U3T1lQclVWYlBjbVgxVVkyemVJTHdYbERjVzJtNFZuVFhlMm53Vm96Yk9sbjNZbVBMUG0zOFhaUExkVlhxYW8yNmNXL3NjNXJQZ21INVZXVERmMjNLT1Zub1dWMi8=";
		String textCipher;
		String textDecrypter;
		
		//textCipher = cipher.encode(text);
		textDecrypter = cipher.decode(text);
		
		System.out.println("Texto  : "+ text);
		//System.out.println("Cifrado: "+ textCipher);
		System.out.println("Descifr: "+ textDecrypter);
		
		//System.out.println(cipher.encode("ABCDEFGHIJQLMN—OPQRSTUVWXYZabcdefghijklmnÒopqrstuvwxyz1234567890!#$%&()=?°+-"));
		//System.out.println(cipher.decode("fzQkc3SmJkpxMDQiZ4Sqc35jPjQud3djMDQuaY—1ZXemJkpjbH0tZTFjMDQ2d3WzV302dn—mJkq8Jn6wcXQzaTJ7JlqwdnemJÒ!tJÒSqcXW1eHGudDJ7fzQlZYSmJkq8JÒmmZYJjPkJxNUhtJn2wcÒSpJkp!MDQlZYljPki0MDQ!bX2mJkq8JniweYJjPkF4MDQubX62eHVjPkN5MDQ1aX—wcnLjPkL1MDQvZX6wJkp4PElxNEBxNEC0gY!?"));
	}

}

