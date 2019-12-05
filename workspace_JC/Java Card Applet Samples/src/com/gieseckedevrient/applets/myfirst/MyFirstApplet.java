/**********************************************************
 Project:  Java Card Applet Samples
 File:     MyFirstApplet.java

 This code serves as an example for testing the 
 Java Card Suite. 
 
 Any use beyond that, especially the redistribution of
 binaries or source code of this sample is prohibited.

 ********* (C) Copyright Giesecke & Devrient 2010 *********/

package com.gieseckedevrient.applets.myfirst;

import javacard.framework.*;

/**
 * Simple Java Card Applet which reads and modifies user data via PIN access.
 */
public class MyFirstApplet extends Applet {

   /**
    * ISO Class Byte.
    */
   final static byte CLA = (byte) 0x05;

   /**
    * Instruction Byte for 'user verification'.
    */
   final static byte INS_VERIFY = (byte) 0x20;

   /**
    * Instruction Byte for 'read user data'.
    */
   final static byte INS_READ = (byte) 0xB6;

   /**
    * Instruction Byte for 'write (modify) user data'.
    */
   final static byte INS_WRITE = (byte) 0xD6;

   /**
    * User data (last name, first name, birthday, social security number).
    */
   static byte[] userData;

   /**
    * Pin for modifying user data: '1111' (hex: '31 31 31 31')
    */
   static byte[] writeAccessPIN;

   /**
    * Pin for reading user data: '0000' (hex: '30 30 30 30')
    */
   static byte[] ReadAccessPIN;

   /**
    * Indicates that verification for 'write access' was successful.
    */
   static boolean isVerifiedForWrite;

   /**
    * Indicates that verification for 'read access' was successful.
    */
   static boolean isVerifiedForRead;

   /**
    * The install method is the entry point method of an applet similar to the
    * main method in a Java application.
    * 
    * @see Applet#install(byte[], short, byte)
    */
   public static void install(byte[] buffer, short offset, byte length) {
      new MyFirstApplet(buffer, offset, length); // performs initialization
   }

   /**
    * Constructor. The applet performs any necessary initializations and memory
    * allocation within the constructor.
    * @param bArray the array containing installation parameters.
    * @param bOffset the starting offset in bArray.
    * @param bLength the length in bytes of the parameter data in bArray.
    * The maximum value of length is 32.
    */
   public MyFirstApplet(byte[] bArray, short bOffset, byte bLength) {

      // empty user data
      userData = new byte[(short) 80];

      // initialize PIN for write access
      writeAccessPIN = new byte[4];
      writeAccessPIN[0] = (byte) '2';
      writeAccessPIN[1] = (byte) '2';
      writeAccessPIN[2] = (byte) '2';
      writeAccessPIN[3] = (byte) '2';

      // initialize PIN for read access
      ReadAccessPIN = new byte[4];
      ReadAccessPIN[0] = (byte) '0';
      ReadAccessPIN[1] = (byte) '0';
      ReadAccessPIN[2] = (byte) '0';
      ReadAccessPIN[3] = (byte) '0';

      // initialize the access flags
      isVerifiedForWrite = false;
      isVerifiedForRead = false;

      register(bArray, (short) (bOffset + 1), bArray[bOffset]); // register the applet to the Java Card VM
   }

   /**
    * The JCRE calls this method to instruct the applet to process an incoming
    * APDU command.
    * 
    * @see Applet#process(APDU)
    */
   public void process(APDU apdu) throws ISOException {

      if (selectingApplet()) { // applet is selected

         isVerifiedForWrite = false; // reset verification flags
         isVerifiedForRead = false;

         return;
      }

      short offset = -10;
      short commandLength = -30;

      byte compareResultWrite = (byte) 0x11;
      byte compareResultRead = (byte) 0x11;

      byte[] apduBuffer = apdu.getBuffer();

      // Instruction Byte Switch.
      switch (apduBuffer[ISO7816.OFFSET_INS]) {

      // PIN Verification as defined in ISO 7816-4.
      case INS_VERIFY:

         commandLength = receive(apdu);

         compareResultWrite = Util.arrayCompare(apduBuffer,
               (short) (ISO7816.OFFSET_CDATA & 0x00FF), writeAccessPIN,
               (short) 0, commandLength);
         compareResultRead = Util.arrayCompare(apduBuffer,
               (short) (ISO7816.OFFSET_CDATA & 0x00FF), ReadAccessPIN,
               (short) 0, commandLength);

         if (compareResultRead == (byte) 0x00) {
            isVerifiedForRead = true;
            isVerifiedForWrite = false;
         } else if (compareResultWrite == (byte) 0x00) {
            isVerifiedForRead = false;
            isVerifiedForWrite = true;
         } else
            // Exception: 0x6982
            ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);

         break;

      // Read user data.
      case INS_READ:

         if (!isVerifiedForRead && !isVerifiedForWrite) // Exception: 0x6982
            ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);

         apdu.setOutgoing();
         apdu.setOutgoingLength((short) 80);
         apdu.sendBytesLong(userData, (short) 0, (short) 80);

         break;

      // Write/modify user data.
      case INS_WRITE:

         if (!isVerifiedForWrite) // Exception: 0x6982
            ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);

         compareResultWrite = 0x00;
         commandLength = receive(apdu);
         offset = ISO7816.OFFSET_CDATA;

         // set first name
         while (apduBuffer[offset] != (byte) 0xFF) {
            userData[compareResultWrite] = apduBuffer[offset];

            compareResultWrite++;
            offset++;
         } // and fill with ASCII-Spaces
         while (compareResultWrite < (short) 30) {
            userData[compareResultWrite] = (byte) 0x20;
            compareResultWrite++;
         }
         offset++;
         // set last name
         while (apduBuffer[offset] != (byte) 0xFF) {
            userData[compareResultWrite] = apduBuffer[offset];

            compareResultWrite++;
            offset++;
         } // and fill with ASCII-Spaces
         while (compareResultWrite < (short) 60) {
            userData[compareResultWrite] = (byte) 0x20;
            compareResultWrite++;
         }
         offset++;
         // set birthday
         while (apduBuffer[offset] != (byte) 0xFF) {
            userData[compareResultWrite] = apduBuffer[offset];

            compareResultWrite++;
            offset++;
         }
         offset++;
         // set social security number
         while (apduBuffer[offset] != (byte) 0xFF) {
            userData[compareResultWrite] = apduBuffer[offset];

            compareResultWrite++;
            offset++;
         }
         break;

      default:
         // Exception: 0x6E00
         ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
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
