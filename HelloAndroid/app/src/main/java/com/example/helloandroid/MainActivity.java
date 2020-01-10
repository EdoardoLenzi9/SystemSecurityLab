package com.example.helloandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity {

    private Button generateBtn;
    private TextView messageField;
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
        messageField = (TextView)findViewById(R.id.message);
        uidField = (EditText)findViewById(R.id.uid);
        lengthField = (EditText)findViewById(R.id.lenght);
        validityField = (EditText)findViewById(R.id.validity);
        modeField = (EditText)findViewById(R.id.mode);

        messageField.setText("Fill the fields!");
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

        Generator generator = new Generator(readParameters());
        long time = System.currentTimeMillis() / 1000;
        String pin = generator.generate(time);

        messageField.setText(pin);
    }


    private PinParameters readParameters() throws Exception {
        int uid = Integer.parseInt(uidField.getText().toString());
        int length = Integer.parseInt(lengthField.getText().toString());
        int validity = Integer.parseInt(validityField.getText().toString());
        return new PinParameters(uid, length, validity);
    }


    public void unidirectionalMode() throws Exception {
        messageField.setText("Waiting for server response...");
        new Thread() {
            @Override
            public void run() {
                try {
                    PinParameters parameters = readParameters();
                    Generator generator = new Generator(parameters);
                    long time = System.currentTimeMillis() / 1000;
                    String pin = generator.generate(time);

                    PinDto dto = new PinDto(parameters, pin);

                    // Create Socket instance
                    Socket socket = new Socket(HOST, PORT);

                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(dto);

                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                    String message = (String) ois.readObject();
                    messageField.setText(message);

                    ois.close();
                    oos.close();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void bidirectionalMode(){

    }

}

