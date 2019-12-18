package com.gieseckedevrient.applets.myfirst;

import javacard.framework.ISO7816;
import javacard.framework.ISOException;

public class SessionService {
   	private static short role;				// user role
   	private static boolean walletEnabled;	// true if wallet is enabled
	
	public short getRole(){ return this.role; }
	
  	public void setRole(short role){
	   	this.role = role;
   	}
  	
  	public void setWalletEnabled(boolean value){
	   	this.walletEnabled = value;
   	}
  	  	
	public SessionService(){
	   	role = 0;
	   	walletEnabled = false;
	}
	
	
	public void checkRole(short index){
		if (this.role != (short)(index + 1)){ 
			ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED); // Exception: 0x6982
		}
	}
	
	public void checkSession(){
		// by default the role is 0 (un-authenticated)
		if (this.role <= (short)0){ 
			ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED); // Exception: 0x6982
		}
    }
	
	
	public void checkWalletEnabled(){
		if (!this.walletEnabled){ 
			ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED); // Exception: 0x6982
		}
    }
	
	
	public void allowOnly1(){
		if (this.role != (short)1){ 
			ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED); // Exception: 0x6982
		}
	}
	
	
	public void allowOnly2(){
		if (this.role != (short)2){ 
			ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED); // Exception: 0x6982
		}
	}
	
	
	public void allowOnly3(){
		if (this.role != (short)3){ 
			ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED); // Exception: 0x6982
		}
	}
	
	public void allowOnly1and2(){
		if (this.role != (short)1 && this.role != (short)2){ 
			ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED); // Exception: 0x6982
		}
	}
}
