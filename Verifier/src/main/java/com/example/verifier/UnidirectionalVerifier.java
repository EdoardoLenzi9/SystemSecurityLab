package com.example.verifier;

import com.example.helloandroid.PinDto;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class UnidirectionalVerifier {
    
    private static final int PORT = 9999;
    private static final String IP = "192.168.43.249";
    
    public UnidirectionalVerifier() throws ClassNotFoundException, Exception {
 
        try {
            ServerSocket server = new ServerSocket(PORT);
            while (true) {
                System.out.println("Server: Waiting for request");
                Socket socket = server.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                
                PinDto dto =  (PinDto) ois.readObject();
                System.out.println("Message Received: PIN:" + dto.pin);
                
                boolean isAuthenticated = Validator.validate(dto);
                
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());  
                
                if(isAuthenticated){
                    oos.writeObject("Server: pin verified, user authenticated");
                } else {
                    oos.writeObject("Server: wrong pin!");
                }
                ois.close();
                oos.close();
                socket.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
 
    }
 
    }
