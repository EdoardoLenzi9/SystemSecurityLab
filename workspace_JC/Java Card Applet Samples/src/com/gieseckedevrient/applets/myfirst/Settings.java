package com.gieseckedevrient.applets.myfirst;

import javacard.framework.ISO7816;

/*
 * Constant/static declarations
 */
public class Settings {
   	public final static byte CLA = (byte) 0x05;
    
   	public final static byte INS_VERIFY = (byte) 0x20;
   	public final static byte INS_CHANGEPIN = (byte) 0xA6;
   	public final static byte INS_READ = (byte) 0xB6;
   	public final static byte INS_AGE = (byte) 0xC6;
   	public final static byte INS_WRITE = (byte) 0xD6;
   	public final static byte INS_CREDIT = (byte) 0xE6;
   	public final static byte INS_DEBIT = (byte) 0xF6;
   	public final static byte INS_BALANCE = (byte) 0xA7;
   	public final static byte INS_STATUS = (byte) 0xB7;

    public final static byte P2_PIN1 = (byte) 0x01;
   	public final static byte P2_PIN2 = (byte) 0x02;
   	public final static byte P2_PIN3 = (byte) 0x03;
   	  	
   	public static byte tryLimit = (byte) 3;		// maximum number of trials for a pin
   	public static byte maxPINSize = (byte) 4;	// maximum pin size 
}
