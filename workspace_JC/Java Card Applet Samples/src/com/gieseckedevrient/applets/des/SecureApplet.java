/**********************************************************
 Project:  Java Card Applet Samples
 File:     SecureApplet.java

 This code serves as an example for testing the 
 Java Card Suite. 
 
 Any use beyond that, especially the redistribution of
 binaries or source code of this sample is prohibited.

 ********* (C) Copyright Giesecke & Devrient 2010 *********/

package com.gieseckedevrient.applets.des;

import javacard.framework.*;
import javacard.security.*;
import javacardx.crypto.*;

/**
 * Java Card Applet that demonstrates the use of certain security and
 * cryptographic functions for signing, decrypting and encrypting data. Use
 * case: - User requests access rights to the data sending a PIN code to the
 * card that is encrypted with a secret key. - The card decrypts the PIN with
 * the secret key and checks if it matches the PIN code stored in the applet
 * ('1234') - The user either reads the decrypted data signed with the secret
 * key (INS_READ_SIGNED) or reads the data encrypted with the secret key
 * (INS_READ_ENCRYPTED).
 */
public class SecureApplet extends Applet {

   /**
    * ISO Class Byte.
    */
   private static final byte CLA = (byte) 0x00;

   /**
    * Instruction Byte for 'user verification'.
    */
   private static final byte INS_VERIFY = (byte) 0xC0;

   /**
    * Instruction Byte for 'read data signed'.
    */
   private static final byte INS_READ_SIGNED = (byte) 0xB6;

   /**
    * Instruction Byte for 'read data encrypted'.
    */
   private static final byte INS_READ_ENCRYPTED = (byte) 0xB7;

   /**
    * Data to be retrieved from the card.
    */
   private static byte[] userData;

   /**
    * PIN code. '1234'
    */
   private static byte[] pin;

   /**
    * Key for encryption.
    */
   private static byte[] secretKey;

   /**
    * Indicates that verification was successful.
    */
   private static boolean isVerified;

   /**
    * Reference to the signature object.
    */
   private static Signature signature;

   /**
    * Reference to the cipher object.
    */
   private static Cipher cipher;

   /**
    * Reference to the DES key object.
    */
   private static DESKey desKey;

   /**
    * The install method is the entry point method of an applet similar to the
    * main method in a Java application.
    * 
    * @see Applet#install(byte[], short, byte)
    */
   public static void install(byte[] buffer, short offset, byte length) {
      new SecureApplet(buffer, offset, length); // performs initialization
   }

   /**
    * Constructor. The applet performs any necessary initializations and memory
    * allocation within the constructor.
    * @param bArray the array containing installation parameters.
    * @param bOffset the starting offset in bArray.
    * @param bLength the length in bytes of the parameter data in bArray.
    * The maximum value of length is 32.
    */
   public SecureApplet(byte[] bArray, short bOffset, byte bLength) {

      // Data to be encrypted/decrypted
      userData = new byte[11];
      userData[0] = (byte) 'H';
      userData[1] = (byte) 'e';
      userData[2] = (byte) 'l';
      userData[3] = (byte) 'l';
      userData[4] = (byte) 'o';
      userData[5] = (byte) ' ';
      userData[6] = (byte) 'W';
      userData[7] = (byte) 'o';
      userData[8] = (byte) 'r';
      userData[9] = (byte) 'l';
      userData[10] = (byte) 'd';

      // PIN code
      pin = new byte[4];
      pin[0] = (byte) '1';
      pin[1] = (byte) '2';
      pin[2] = (byte) '3';
      pin[3] = (byte) '4';

      // Key for encryption
      secretKey = new byte[8];
      secretKey[0] = (byte) 0x24;
      secretKey[1] = (byte) 0x52;
      secretKey[2] = (byte) 0xA0;
      secretKey[3] = (byte) 0x6F;
      secretKey[4] = (byte) 0x77;
      secretKey[5] = (byte) 0x6F;
      secretKey[6] = (byte) 0xF0;
      secretKey[7] = (byte) 0x52;

      isVerified = false;

      // initialize key, signature and cipher object
      desKey = (DESKey) KeyBuilder.buildKey(
            KeyBuilder.TYPE_DES_TRANSIENT_DESELECT, KeyBuilder.LENGTH_DES,
            false);

      signature = Signature.getInstance(Signature.ALG_DES_MAC8_ISO9797_M1,
            false);

      cipher = Cipher.getInstance(Cipher.ALG_DES_CBC_ISO9797_M1, false);

      // register the applet to the Java Card VM
      register(bArray, (short) (bOffset + 1), bArray[bOffset]); 
   }

   /**
    * The JCRE calls this method to instruct the applet to process an incoming
    * APDU command.
    * 
    * @see Applet#process(APDU)
    */
   public void process(APDU adpu) throws ISOException {

      if (selectingApplet()) { // applet is selected

         isVerified = false; // reset verification flag

         // initialize DES key
         desKey.setKey(secretKey, (short) 0);

         return;
      }

      short commandLength = (short) 0x0000;

      byte[] apduBuffer = adpu.getBuffer();

      // Class Byte Switch.
      // Mask the logical channel info of CLASS
      switch (apduBuffer[ISO7816.OFFSET_CLA] & (byte) 0xFC) {
      case CLA:

         // Instruction Byte Switch.
         switch (apduBuffer[ISO7816.OFFSET_INS]) {

         // Verify access rights with encrypted key
         case INS_VERIFY: // 0xC0

            commandLength = receive(adpu);

            // initialize cipher object with 'our' key for decryption
            cipher.init(desKey, Cipher.MODE_DECRYPT);

            // decrypt data from APDU buffer
            cipher.doFinal(apduBuffer, (short) (ISO7816.OFFSET_CDATA & 0x00FF),
                  commandLength, apduBuffer, (short) 0);

            // compare decrypted data with PIN
            commandLength = Util.arrayCompare(apduBuffer, (short) 0, pin,
                  (short) 0, (short) 4);

            if (commandLength == (byte) 0x00)
               isVerified = true;
            else
               ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);

            break;

         // Read plain data and signature
         case INS_READ_SIGNED: // 0xB6

            if (!isVerified)
               ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);

            // sign 'Hello World' ...
            signature.init(desKey, Signature.MODE_SIGN);
            commandLength = signature.sign(userData, (short) 0, (short) 11,
                  apduBuffer, (short) 11);

            Util.arrayCopy(userData, (short) 0, apduBuffer, (short) 0,
                  (short) 11);

            // ... and send
            adpu.setOutgoing();
            adpu.setOutgoingLength((short) (commandLength + (short) 11));
            adpu.sendBytesLong(apduBuffer, (short) 0,
                  (short) (commandLength + (short) 11));

            break;

         // Get encrypted data
         case INS_READ_ENCRYPTED:

            if (!isVerified)
               ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);

            // encrypt 'Hello World' ...
            cipher.init(desKey, Cipher.MODE_ENCRYPT);
            commandLength = cipher.doFinal(userData, (short) 0, (short) 11,
                  apduBuffer, (short) 0);

            // ... and send
            adpu.setOutgoing();
            adpu.setOutgoingLength((short) commandLength);
            adpu.sendBytesLong(apduBuffer, (short) 0, (short) commandLength);

            break;

         default:
            // Exception: 0x6D00
            ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
         }
         break;

      default:
         // Exception: 0x6E00
         ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
      }
   }

   /**
    * Reads incoming data into the APDU buffer.
    * @param apdu the APDU buffer.
    * @return the incoming APDU command length.
    * @throws ISOException '67 00' if the received data do not match
    *         the number in the LC field.
    */
   public short receive(APDU apdu) throws ISOException {

      byte[] apduBuffer = apdu.getBuffer();

      // LC indicates the incoming APDU command length.
      short commandLength = (short) (apduBuffer[ISO7816.OFFSET_LC] & 0x00FF);

      if (commandLength != apdu.setIncomingAndReceive())
         // Exception : 0x6700
         ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);

      return commandLength;

   }

}
