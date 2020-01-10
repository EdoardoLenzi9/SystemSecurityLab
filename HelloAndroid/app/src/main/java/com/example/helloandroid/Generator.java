package com.example.helloandroid;

import java.math.BigInteger;
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

import static java.lang.Math.abs;

public class Generator {
    private PinParameters parameters;

    private final byte[] key = fromHexString("00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15");
    // private final String aesCypher = "AES/CBC/PKCS5Padding";
    private final String aesCypher = "AES/ECB/PKCS5Padding";


    public Generator(PinParameters params) throws Exception{
        this.parameters = params;
    }

    public String generate(long time) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException{

        byte[] secret = join(new ArrayList<>( Arrays.asList( toBytes(parameters.uid),
                toBytes(parameters.length),
                toBytes(parameters.validity),
                toBytes(time)
                /* PAD */ )));

        Cipher aes = Cipher.getInstance(aesCypher);
        SecretKeySpec aesKey = new SecretKeySpec(key, "AES");
        aes.init(Cipher.ENCRYPT_MODE, aesKey);

        byte[] encrypted = aes.doFinal(secret);
        String pin = new String(toReadableString(encrypted));
        return pin;
    }

    private byte[] toReadableString(byte[] pin){
        for(int i = 0; i < pin.length; i++){
            int tmp = (int) pin[i];
            tmp = abs(tmp)% 42;
            tmp = tmp + 48;
            pin[i] = (byte)tmp;
        }
        return Arrays.copyOfRange(pin, pin.length - parameters.length, pin.length);
    }

    private static byte[] toBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    private static byte[] toBytes(int x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putInt(x);
        return buffer.array();
    }

    private static byte[] fromHexString(String src) {
        byte[] biBytes = new BigInteger("10" + src.replaceAll("\\s", ""), 16).toByteArray();
        return Arrays.copyOfRange(biBytes, 1, biBytes.length);
    }

    private static byte[] join (ArrayList<byte[]> items){

        byte[] result = items.get(0);

        for(int i = 0; i < items.size(); i++){
            byte[] next = items.get(i);
            byte[] tmp = new byte[result.length + next.length];
            System.arraycopy(result,0,tmp,0,result.length);
            System.arraycopy(next,0,tmp,result.length,next.length);

            result = tmp;
        }

        return result;
    }
}

