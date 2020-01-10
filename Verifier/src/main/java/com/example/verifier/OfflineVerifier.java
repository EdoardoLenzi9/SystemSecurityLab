package com.example.verifier;

public class OfflineVerifier {
    
    private boolean isAuthenticated = false;
    
    public OfflineVerifier() throws Exception{
        int uid = Scanner.readInt("Insert user Id (0..255)");
        int length = Scanner.readInt("Insert pin length (1..16)");
        int validity = Scanner.readInt("Insert pin validity (1..60)");
        
        PinParameters params = new PinParameters(uid, length, validity);
        
        System.out.println("Verifier setup completed");
        
        String pin = Scanner.readLine("Insert pin");
        Validator.validate(pin, params);
    }
}

