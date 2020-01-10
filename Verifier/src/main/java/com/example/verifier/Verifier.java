package com.example.verifier;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Verifier {

    public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, Exception {
        while(true){
            int mode = Scanner.readInt("Choose mode (1 - offline verifier, 2 - unidirectional verifier, 3 - bidirectional verifier)");
            switch(mode){
                case 1:
                    new OfflineVerifier();
                    break;
                case 2:
                    new UnidirectionalVerifier();
                    break;
                case 3:
                    new BidirectionalVerifier();
                    break;
                default:
                    System.err.println("Unsupported mode!");
            }
        }
    }
    
}
