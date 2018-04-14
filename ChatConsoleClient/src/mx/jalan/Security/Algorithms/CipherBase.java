package mx.jalan.Security.Algorithms;

import java.io.Serializable;

public interface CipherBase<T, KT extends Serializable> {
	public String encode(T textToCipher);
	
	public String decode(T textToDecipher);
	
	public String getCipherName();
        
    public boolean isAsyncCipher();
    
    public void setKey(KT key);

    public KT getKey();
}

