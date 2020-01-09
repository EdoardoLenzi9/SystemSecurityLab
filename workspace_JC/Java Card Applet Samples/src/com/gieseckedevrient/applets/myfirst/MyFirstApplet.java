package com.gieseckedevrient.applets.myfirst;

import javacard.framework.*;

public class MyFirstApplet extends Applet {
	
	// Services

	private static AuthService auth;
	private static SessionService session;
	private static UserDataManager userData;
	private static WalletManager wallet;
	
	
	public static void install(byte[] buffer, short offset, byte length) {
   	   new MyFirstApplet(buffer, offset, length); // performs initialization
   	}
	  

   	public MyFirstApplet(byte[] bArray, short bOffset, byte bLength) {
   		// Services init
   		auth = new AuthService();
   		session = new SessionService();
   		userData = new UserDataManager();
   		wallet = new WalletManager();
   		   		
   		// register the applet to the Java Card VM
      	register(bArray, (short) (bOffset + 1), bArray[bOffset]); 
  	}


   	public void process(APDU apdu) throws ISOException {

      	if (selectingApplet()) { 
      		session.setRole((short) 0);  //kill existing sessions
         	return;
      	}

      	byte[] apduBuffer = apdu.getBuffer();

      	short offset = -10;
      	short commandLength = -30;
      	
      	switch (apduBuffer[ISO7816.OFFSET_INS]) {

	      	case Settings.INS_VERIFY:		// verify pin mode
	          	commandLength = receive(apdu);
	          	verifyMode(apduBuffer, (byte)commandLength);
	          	break;

	        case Settings.INS_CHANGEPIN: 	// change pin mode
	          	commandLength = receive(apdu);
	          	changePINMode(apduBuffer, (byte)commandLength);
	         	break;

	      	case Settings.INS_AGE:			// check age
	    	  	commandLength = receive(apdu);
	    	  	ageMode(apdu, apduBuffer, commandLength);
		        break;
	    	  	         
	     	case Settings.INS_READ:			// read user name
	    	  	readMode(apdu);
	         	break;
	
	      	case Settings.INS_WRITE:		// overwrite user date
	    	  	commandLength = receive(apdu);
	    	  	writeMode(apduBuffer, offset, commandLength);
	    	 	break;
	    	  
	      	case Settings.INS_CREDIT:		// crediting the wallet
	    	  	commandLength = receive(apdu);
	    	  	creditMode(apduBuffer, commandLength);
	    	 	break;
	    	 	
	      	case Settings.INS_DEBIT:		// debiting the wallet
	    	  	commandLength = receive(apdu);
	    	  	debitMode(apduBuffer, commandLength);
	    	 	break;
	    	 	
	      	case Settings.INS_BALANCE:		// show wallet balance
	    	  	balanceMode(apdu);
	    	 	break;
	    	 	
	      	case Settings.INS_STATUS:		// show session status, return the current user role (0 if undefined)
	    	  	statusMode(apdu);
	    	 	break;
	    	 	
   			default:						// unknown command
   				ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED); // Exception: 0x6E00
      	}
    }
   	
   	
   	private void statusMode(APDU apdu){
   		short role = session.getRole();
   		
   		byte[] reply = new byte[1];
   		reply[0] = (byte) role;
   		    
     	apdu.setOutgoing();
     	apdu.setOutgoingLength((short) 80);
     	apdu.sendBytesLong(reply, (short) 0, (short) 1);
   	}
   	
   	  	
   	private void verifyMode(byte[] buffer, byte length){
   		// verify pin and get correspondent role
	  	short role = auth.verifyPIN(getIndex(buffer), buffer, ISO7816.OFFSET_CDATA, length);	
	  	
	  	// setup a new session with the role
	  	session.setRole(role);
   	}
   	
   	
   	private void changePINMode(byte[] buffer, short length){
   		session.checkSession();	
   		short index = getIndex(buffer);
   		
   		session.checkRole(index);
   		
   		if(auth.updatePINWraper(index, buffer, ISO7816.OFFSET_CDATA, (byte) length)){
   			// wallet is enabled only after every pin is updated
   			session.setWalletEnabled(true);
   		}
   	}
   	
   	
   	private void creditMode(byte[] buffer, short length){
   		session.checkSession();
   		
   		// only user 2 is allowed
   		session.allowOnly2();
   		
   		// wallet must be enabled
   		session.checkWalletEnabled();
   		
   		// serialize credit input on 4 byte array
   		byte[] credit = new byte[4];
   		credit[0] = (byte) 0x00;
   		credit[1] = (byte) 0x00;
   		credit[2] = buffer[ISO7816.OFFSET_CDATA];
   		credit[3] = buffer[ISO7816.OFFSET_CDATA + 1];
   		
   		wallet.credit(credit); 
   	}
   	
   	
   	private void debitMode(byte[] buffer, short length){
   		session.checkSession();
   		
   		// only user 3 is enabled
   		session.allowOnly3();
   		
   		// wallet must be enabled
   		session.checkWalletEnabled();
   		
   		// serialize debit input on 4 byte array
   		byte[] debit = new byte[4];
   		debit[0] = (byte) 0x00;
   		debit[1] = (byte) 0x00;
   		debit[2] = buffer[ISO7816.OFFSET_CDATA];
   		debit[3] = buffer[ISO7816.OFFSET_CDATA + 1];
   		
   		wallet.debit(debit); 
   	}
   	
   	
   	private void ageMode(APDU apdu, byte[] buffer, short length){
   		session.checkSession();
   		
   		// read the input date (today)
        DateTime today = new DateTime(length, ISO7816.OFFSET_CDATA, buffer);
        
        // get the years of the user
        short time = today.getYearsFrom(userData.dateOfbirth);
        
        byte[] reply = new byte[(short) 1];
        
        if(time >= 18){
        	reply[0] = (byte) 1;	// if the user age >= 18 then return 01
        } else {
        	reply[0] = (byte) 0;	// else return 00
        }
                
     	apdu.setOutgoing();
     	apdu.setOutgoingLength((short) 80);
     	apdu.sendBytesLong(reply, (short) 0, (short) 1);
   	}
   	
   	
   	private void balanceMode(APDU apdu){

   		session.checkSession();

     	apdu.setOutgoing();
     	apdu.setOutgoingLength((short) 80);
     	apdu.sendBytesLong(wallet.getBalance(), (short) 0, (short) 4);
   	}
   	
   	   	
   	private void readMode(APDU apdu){

   		session.checkSession();
   		session.allowOnly1and2();

     	apdu.setOutgoing();
     	apdu.setOutgoingLength((short) 80);
     	apdu.sendBytesLong(userData.firstName, (short) 0, (short) 30);
   	}
   	
   	
  	private void writeMode(byte[] buffer, short offset, short length){
  		
  		session.checkSession();
  		session.allowOnly1();
  		
  		byte compareResultWrite = (byte) 0x11;
  		byte compareResultRead = (byte) 0x11;
  		byte escape = (byte) 0xFF;
        compareResultWrite = 0x00;
        offset = ISO7816.OFFSET_CDATA;
        
        // set first name
        while (buffer[offset] != escape) {
           	userData.firstName[compareResultWrite] = buffer[offset];
           	compareResultWrite++;
           	offset++;
        } // and fill with ASCII-Spaces
        while (compareResultWrite < (short) 30) {
           	userData.firstName[compareResultWrite] = (byte) 0x20;
           	compareResultWrite++;
        }
        compareResultWrite = 0x00;
        offset++;
        // set last name
        while (buffer[offset] != escape) {
           	userData.lastName[compareResultWrite] = buffer[offset];
           	compareResultWrite++;
           	offset++;
        } // and fill with ASCII-Spaces
        while (compareResultWrite < (short) 30) {
       	 	userData.lastName[compareResultWrite] = (byte) 0x20;
       	 	compareResultWrite++;
        }
        compareResultWrite = 0x00;
        offset++;
        // set birthday
        userData.dateOfbirth = new DateTime((short)8, offset, buffer);
  	}

  	
   	public short receive(APDU apdu) throws ISOException {

      	byte[] apduBuffer = apdu.getBuffer();

      	// LC indicates the incoming APDU command length.
      	short commandLength = (short) (apduBuffer[ISO7816.OFFSET_LC] & 0x00FF);

      	if (commandLength != apdu.setIncomingAndReceive()){
	        ISOException.throwIt(ISO7816.SW_WRONG_LENGTH); // Exception : 0x6700
		}
     	return commandLength;
   }
   	
   	
   	/*
   	 * returns the user index correspondent of a certain session
   	 */
   	private short getIndex(byte[] buffer){   		
   		short index = (short) 0;
   		
   		switch (buffer[ISO7816.OFFSET_P2]) {
   			case Settings.P2_PIN1:
   				index = (short) 0; break;
   			case Settings.P2_PIN2:
   				index = (short) 1; break;  
   			case Settings.P2_PIN3:
   				index = (short) 2; break;
   			default:
   				ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
   		}

   		return index;
   	 }
}