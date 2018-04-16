package mx.jalan.Security;

import java.io.Serializable;

import mx.jalan.Security.Algorithms.CaesarCipher;
import mx.jalan.Security.Algorithms.CipherBase;

public class CipherFactory<T, KT extends Serializable> {
    
    public CipherBase<T, KT> getCipher(String cipherName){
        switch(cipherName){
            case EncryptionAlgorithms.CAESAR:
                return new CaesarCipher<T, KT>();
        }
        
        return null;
    }
}
