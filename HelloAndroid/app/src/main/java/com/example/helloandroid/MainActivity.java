package com.example.helloandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
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

public class MainActivity extends AppCompatActivity {

    private Button generateBtn;
    private TextView pinField;
    private EditText uidField;
    private EditText lengthField;
    private EditText validityField;
    private EditText modeField;

    private static final int PORT = 9999;
    private static final String HOST = "192.168.43.249";
    private static final String privateKey = "00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        generateBtn = (Button)findViewById(R.id.generate);
        pinField = (TextView)findViewById(R.id.pin);
        uidField = (EditText)findViewById(R.id.uid);
        lengthField = (EditText)findViewById(R.id.lenght);
        validityField = (EditText)findViewById(R.id.validity);
        modeField = (EditText)findViewById(R.id.mode);

    }


    public void GenerateListener(View view) throws Exception, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException {


        String mode = modeField.getText().toString();
        System.out.println("\n\n mode: " + mode);

        switch (mode){
            case "1": mode1(); break;
            case "2": mode2(); break;
            case "3": mode3(); break;
            default: throw new Exception("Unsupported Mode");
        }
    }

    private String generatePin() throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        String uid = uidField.getText().toString();
        String lenght = lengthField.getText().toString();
        String validity = validityField.getText().toString();
        long time = System.currentTimeMillis() / 1000;

        byte[] secret = join(new ArrayList<>( Arrays.asList(uid.getBytes(), lenght.getBytes(), validity.getBytes(), longToBytes(time) /* PAD */)));

        byte[] key = fromHexString(privateKey);
        Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec aesKey = new SecretKeySpec(key, "AES");
        aes.init(Cipher.ENCRYPT_MODE, aesKey);

        byte[] encrypted = aes.doFinal(secret);
        String pin = encrypted.toString();
        return pin;
    }

    public void mode1() throws NoSuchAlgorithmException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeyException {
       pinField.setText(generatePin());
    }

    public void mode2(){
        new Thread() {
            @Override
            public void run() {
                try {
                    // Create Socket instance
                    Socket socket = new Socket(HOST, PORT);
                    // Get input buffer
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
                    String line = br.readLine();
                    br.close();
                    System.out.println(line);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void mode3(){

    }

    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    public static byte[] fromHexString(String src) {
        byte[] biBytes = new BigInteger("10" + src.replaceAll("\\s", ""), 16).toByteArray();
        return Arrays.copyOfRange(biBytes, 1, biBytes.length);
    }

    public byte[] join (ArrayList<byte[]> items){

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

