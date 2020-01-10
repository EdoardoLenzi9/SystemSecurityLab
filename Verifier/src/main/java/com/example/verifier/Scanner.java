package com.example.verifier;


public class Scanner {
    
    public static String readLine(String message){
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        System.out.print(message);
        return scanner.nextLine();
    }
    
    
    public static int readInt(String message){
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        System.out.print(message);
        return scanner.nextInt();
    }
    
}
