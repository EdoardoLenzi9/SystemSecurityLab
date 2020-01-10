package com.example.verifier;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class VerifierOffline {

    private static final int PORT = 9999;
    private static final String IP = "192.168.43.249";
    
    public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        
        String uid = "1";
        String lenght = "2";
        String validity = "60";
        
        long time = System.currentTimeMillis() / 1000;

        byte[] secret = join(new ArrayList<>( Arrays.asList(uid.getBytes(), lenght.getBytes(), validity.getBytes(), longToBytes(time) /* PAD */)));


        byte[] key = fromHexString("00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15");
        Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec aesKey = new SecretKeySpec(key, "AES");
        aes.init(Cipher.ENCRYPT_MODE, aesKey);

        byte[] encrypted = aes.doFinal(secret);
        String pin = encrypted.toString();

        //pin;
    }

    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    public static byte[] fromHexString(String src) {
        byte[] biBytes = new BigInteger("10" + src.replaceAll("\\s", ""), 16).toByteArray();
        return Arrays.copyOfRange(biBytes, 1, biBytes.length);
    }

    public static byte[] join (ArrayList<byte[]> items){

        byte[] result = items.get(0);

        for(int i = 0; i < items.size(); i++){
            byte[] next = items.get(i);
            byte[] tmp = new byte[result.length + next.length];
            System.arraycopy(result,0,tmp,0         ,result.length);
            System.arraycopy(next,0,tmp,result.length,next.length);

            result = tmp;
        }

        return result;
    }
}

