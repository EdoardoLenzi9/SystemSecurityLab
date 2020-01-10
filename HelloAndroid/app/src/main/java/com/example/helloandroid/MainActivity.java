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
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity {

    private Button generateBtn;
    private TextView pinField;
    private TextView timeField;
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
        timeField = (TextView)findViewById(R.id.time);
        uidField = (EditText)findViewById(R.id.uid);
        lengthField = (EditText)findViewById(R.id.lenght);
        validityField = (EditText)findViewById(R.id.validity);
        modeField = (EditText)findViewById(R.id.mode);
        pinField.setText("<PIN>");
        timeField.setText("<TIME>");
    }


    public void GenerateListener(View view) throws Exception {
        String mode = modeField.getText().toString();
        System.out.println("\n\n mode: " + mode);

        switch (mode){
            case "1": offlineMode(); break;
            case "2": unidirectionalMode(); break;
            case "3": bidirectionalMode(); break;
            default: throw new Exception("Unsupported Mode");
        }
    }



    public void offlineMode() throws Exception {

        int uid = Integer.parseInt(uidField.getText().toString());
        int length = Integer.parseInt(lengthField.getText().toString());
        int validity = Integer.parseInt(validityField.getText().toString());

        Generator generator = new Generator(uid, length, validity);

        long time = System.currentTimeMillis() / 1000;
        String pin = generator.generate(time);

        pinField.setText(pin);
        timeField.setText(time + "");
    }


    public void unidirectionalMode(){
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

    public void bidirectionalMode(){

    }

}

