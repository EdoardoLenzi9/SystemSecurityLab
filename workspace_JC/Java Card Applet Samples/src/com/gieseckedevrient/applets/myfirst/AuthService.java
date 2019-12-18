package com.gieseckedevrient.applets.myfirst;

import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.OwnerPIN;

public class AuthService {
	
	// user pins
	private static OwnerPIN[] ownerPINS;
	
	// a certain pin has changed
	private static boolean[] changed;

	
	public AuthService(){
		initPINOwners();
		changed = new boolean[3];
		changed[0] = changed[1] = changed[2] = false; 
	}
	
	
	/*
	 * return a default pin value
	 */
   	public byte[] defaultPin(byte maxPinSize, byte defaultValue){
		   byte[] defaultPIN = new byte[maxPinSize];
		   defaultPIN[0] = defaultValue;
		   defaultPIN[1] = defaultValue;
		   defaultPIN[2] = defaultValue;
		   defaultPIN[3] = defaultValue; 
		   return defaultPIN;
	}
   	
   	
   	private void initPINOwners(){
		ownerPINS = new OwnerPIN[(short) 3];
	   
	  	ownerPINS[0]  = new OwnerPIN(Settings.tryLimit , Settings.maxPINSize);
	  	ownerPINS[1] = new OwnerPIN(Settings.tryLimit , Settings.maxPINSize);
	  	ownerPINS[2] = new OwnerPIN(Settings.tryLimit , Settings.maxPINSize);

	  	updatePIN((short) 0, defaultPin(Settings.maxPINSize, (byte) '0'), (short) 0, (byte) 4);
	  	updatePIN((short) 1, defaultPin(Settings.maxPINSize, (byte) '0'), (short) 0, (byte) 4);
	  	updatePIN((short) 2, defaultPin(Settings.maxPINSize, (byte) '0'), (short) 0, (byte) 4);
   }
   	
   	
   	public short verifyPIN(short index, byte[] apduBuffer, short offset, byte length){
	   	if ( ownerPINS[index].check(apduBuffer, offset, length )== false ){
		   	ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
	   	}

	   	// return the role for the session setup
	   	return (short) (index + 1);
   	}
   	
   	
   	public boolean updatePINWraper(short index, byte[] pin, short offset, byte length){
   		if(!changed[index]){ 	// block the pin update multiple times
   			if(index > 0){		// force order PIN1 before PIN2 before PIN3
   				if(changed[(short)(index - 1)]){
	   	   			updatePIN(index, pin, offset, length);
	   	   			changed[index] = true;
   				} else {
   					ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
   				}
   			} else {
   	   			updatePIN(index, pin, offset, length);
   	   			changed[index] = true;
   			}
   		} else {
   			ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
   		}
   		
   		if(changed[0] && changed[1] && changed[2]){
   			return true; // if each user have updated his/her pin
   		}
   		return false;
   	}
   	
   	
   	private void updatePIN(short index, byte[] pin, short offset, byte length){
   		ownerPINS[index].update(pin, offset, length);
   	}
	
}
