package com.example.verifier;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class UnidirectionalVerifier {
    
    private static final int PORT = 9999;
    private static final String IP = "192.168.43.249";
    
    public UnidirectionalVerifier() {
 
        try {
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
