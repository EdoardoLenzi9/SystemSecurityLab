package com.example.verifier;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class VerifierUnidirectional {

    private static final int PORT = 9999;
    private static final String IP = "192.168.43.249";
    
    public static void main(String[] args) {
 
        try {
            // Create ServerSocket instance and bind it to port 9999
            ServerSocket server = new ServerSocket(PORT);
            while (true) {
                Socket socket = server.accept();
                // Get output buffer
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream()));
                // Write output
                writer.write("Hello sdf");
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
 
    }
 
    }
