package com.example.verifier;

public class PinParameters {
 
    public int uid;
    public int length;
    public int validity;

    public PinParameters(int u, int l, int v) throws Exception{
        checkRange(u, 0, 255);
        checkRange(l, 1, 16);
        checkRange(v, 1, 60);
        this.uid = u;
        this.length = l;
        this.validity = v;
    }
    
    private void checkRange(int v, int lowerBound, int upperBound) throws Exception{
        if(v < lowerBound){
            throw new Exception(v + " under the lowerBound " + lowerBound);
        } else if(v > upperBound){
            throw new Exception(v + " under the upperBound " + upperBound );
        }
    }
}
