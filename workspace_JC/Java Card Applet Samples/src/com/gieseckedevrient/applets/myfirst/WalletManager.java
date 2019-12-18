package com.gieseckedevrient.applets.myfirst;

import javacard.framework.ISO7816;
import javacard.framework.ISOException;

public class WalletManager {
	
	private byte[] balance;							// current amount of money
	public byte[] getBalance(){ return balance; }
	
	
	public WalletManager(){
		// init wallet balance with 0
		this.balance = new byte[(short)4];
		this.balance[0] = (byte) 0x00;
		this.balance[1] = (byte) 0x00;
		this.balance[2] = (byte) 0x00;
		this.balance[3] = (byte) 0x00;
	}
	
	
	/*
	 * Load a certain amount
	 */
	public void credit(byte[] credit){

        byte[] result = new byte[(short)4];
    	
        // custom implementation of addition algorithm
    	short carry = (short) 0;
    	for(short i = 0; i < 4; i++) {
   		   		
    		short sum = add(balance[i], credit[i], carry);
    		
        	if(sum > (short) 256) {
        		sum -= (short) 256;
        		carry = (short) 1;
        	} else {
        		carry = (short) 0;
        	}
        	
         	result[i] = (byte) sum;
    	}
    	
    	// overflow
    	if (carry == (short)1){
    		ISOException.throwIt(ISO7816.SW_DATA_INVALID);
    	} else {
    		this.balance = result;
    	}
    }
	
    /*
     * helper method for the credit method
     */
	private short add(byte a, byte b, short carry) {
		short c = (short)((short)(a & 0xff) + (short)(b & 0xff) + carry);
		return c;
	}

	
	/*
	 * Get a certain amount
	 */
	public void debit(byte[] debit){
		byte[] result = new byte[(short)4];
		
		// custom implementation of addition algorithm
    	short borrow = (short) 0;
    	for(short i = 0; i < 4; i++) {
   		   		
    		short sub = sub(balance[i], debit[i], borrow);
    		
        	if(sub < (short) 0) {
        		sub += (short) 256;
        		borrow = (short) -1;
        	} else {
        		borrow = (short) 0;
        	}
        	
         	result[i] = (byte) sub;
    	}

    	//balance isn't enough
    	if (borrow == (short)-1){
    		ISOException.throwIt(ISO7816.SW_DATA_INVALID);
    	} else {
    		this.balance = result;
    	}
    }
	
	
    /*
     * helper method for the debit method
     */
    public static short sub(byte a, byte b, short borrow) {
    	short c = (short)(((short)(a & 0xff) - (short)(b & 0xff)) + borrow);
    	return c;
    }
}
