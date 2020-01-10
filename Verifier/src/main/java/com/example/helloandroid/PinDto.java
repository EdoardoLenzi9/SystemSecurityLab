package com.example.helloandroid;
import java.io.Serializable;

public class PinDto implements Serializable {

    public String pin;
    public int uid;
    public int length;
    public int validity;


    public PinDto(PinParameters params, String p) throws Exception{
        this.uid = params.uid;
        this.length = params.length;
        this.validity = params.validity;
        this.pin = p;
    }

}
