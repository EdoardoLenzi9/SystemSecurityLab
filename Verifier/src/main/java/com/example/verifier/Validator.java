package com.example.verifier;

public class Validator {
    
    public Validator(){}
        
    public static boolean validate(String pin, PinParameters params) throws Exception{    
        
        long timestamp = System.currentTimeMillis() / 1000;
        boolean isAuthenticated = false;
        Generator generator = new Generator(params);
        
        for(long i = timestamp - params.validity; i < timestamp + params.validity; i++){
            String pin2 = generator.generate(i);
            if(pin.compareTo(pin2) == 0){
                System.out.println("Pin " + pin2 + " verified");
                isAuthenticated = true;
            }
        }
        
        if(!isAuthenticated){
            System.err.println("Wrong Pin");
        }
        
        return isAuthenticated;
    }
}