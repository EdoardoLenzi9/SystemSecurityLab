package com.gieseckedevrient.applets.myfirst;

import javacard.framework.ISO7816;
import javacard.framework.ISOException;

/*
 * Custom implementation of DateTime type
 */
public class DateTime {

    private short day;
    private short month;
    private short year;

    public short getDay(){ return this.day; }
    public short getMonth(){ return this.month; }
    public short getYear(){ return this.year; }
    
    
    public DateTime(short day, short month, short year){
        this.day = day;
        this.month = month;
        this.year = year;
    }
    
    
    public DateTime(short length, short offset, byte[] date){
        if(length != 8){
        	ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
        }

        // deserialize the data (encoded as byte array) to base 10
        this.day = (short)(toDec(date, offset) * 10 + toDec(date, (short)(offset + 1)));
        this.month = (short)(toDec(date, (short)(offset + 2)) * 10 + toDec(date, (short)(offset + 3)));
        this.year = (short)(toDec(date, (short)(offset + 4)) * 1000 + toDec(date, (short)(offset + 5)) * 100 + toDec(date, (short)(offset + 6)) * 10 + toDec(date, (short)(offset + 7)));
    
        if(this.day > 31 || this.month > 12 || this.year < 1900 || this.year > 2100){
        	ISOException.throwIt(ISO7816.SW_DATA_INVALID);
        }
    }
       
    
    /*
     * returns the age of the user from now
     * @param from is the user birthday
     */
    public short getYearsFrom(DateTime from){
    	short year = (short)(this.year - from.getYear());
    	
    	if(this.month < from.getMonth()){
    		year--;
    	}
    	
    	if(this.month == from.getMonth() && this.day < from.getDay()){
    		year--;
    	}
    	
    	return year;
    }
    
    // convert an ASCII serialized number to a short number
    private short toDec(byte[] source, short index){
    	return (short)(source[index] - (short)48);
    }
    
    
}
