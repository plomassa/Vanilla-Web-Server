package com.codeforall.online.webserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Server {
    
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private int port;
    private DataInputStream in;
    private DataOutputStream out;
    
    public Server(int port) {
        this.port = port;
        try {
            serverSocket = new ServerSocket(port);
            listen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void listen() {
        System.out.println("Listen Method");
        try {
            System.out.println("Server waiting requests on port: " + port);
            clientSocket = serverSocket.accept();
            System.out.println("Client connected: "+ clientSocket.getInetAddress());
            hendleRequest(clientSocket);
            System.out.println("Leaved listen Method");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources();
        }
    }

    private void hendleRequest(Socket clientSocket) throws IOException {
        openResources(clientSocket);
        String request = "";
        String str;
        String[] requestLine;
        int bytesread;
        byte[] buffer = new byte[1024];
        System.out.println("HandleRequest Method");

        while((bytesread = in.read(buffer))!= -1){

            str = new String(buffer, StandardCharsets.UTF_8); // for UTF-8 encoding
            request += str;
            requestLine = str.split(" ");
            System.out.println(request);
            System.out.println("0  "+ requestLine[0]+" 1 "+ requestLine[1]+" 2 "+ requestLine[2]);

            if (requestLine[0].equals("GET")){
                System.out.println("the requeste was a GET");
                getRequest(requestLine[1]);
            }
        }
        closeResources();
        listen();
    }

    private void getRequest(String file) {
        File filePath = new File("src/main/www"+file);
        String[] extentionFinder;   ls
        if (!filePath.exists() || filePath.isDirectory()) {
            System.out.println(" - src/main/www"+file+" - File does not exist");
            fileNotFound();
        }else {
            System.out.println(file + " : File exist and is being sent");
            extentionFinder =  file.split("\\.");
            System.out.println(Arrays.toString(extentionFinder));
            System.out.println(extentionFinder[0] +"   1: "+ extentionFinder[1]);
            if (extentionFinder[1]!= null){
                if (extentionFinder[1].equals("html")){
                    String textResponse = "HTTP/1.0 200 Document Follows\r\n" +
                            "Content-Type: text/html; charset=UTF-8\r\n" +
                            "Content-Length:" +filePath.length()+ "\r\n" +
                            "\r\n";

                    sendResponse(textResponse);
                    sendFile(filePath);

                }else {
                    String imageResponse = "HTTP/1.0 200 Document Follows\r\n" +
                            "Content-Type: image/"+extentionFinder[1]+ "\r\n" +
                            "Content-Length:"+ filePath.length() +"\r\n" +
                            "\r\n";

                    sendResponse(imageResponse);
                    sendFile(filePath);
                }

            }else fileNotFound();

        }
    }

    private void fileNotFound() {
        File fileNotFound = new File("src/main/www/404.html");//src/main/www/
        String htmlResponse = "HTTP/1.0 404 Not Found" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "Content-Length: "+ fileNotFound.length() +"\r\n" +
                "\r\n";
        sendResponse(htmlResponse);
        sendFile(fileNotFound);

    }

    private void sendFile(File file) {
        byte[] buffer = new byte[1024];
        int num = 0;

        try {
            FileInputStream inputStream = new FileInputStream(file);

            while ((num = inputStream.read(buffer))!= -1){
                out.write(buffer, 0, num);
            }
            out.flush();
            inputStream.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendResponse(String htmlResponse) {

        try {
            out.writeBytes(htmlResponse);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void openResources(Socket clientSocket) {
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("could not open streams");
            throw new RuntimeException(e);
        }
        System.out.println("Open Resources Method");
    }

    private void closeResources() {
        System.out.println("Close resources Method");
        try {
            clientSocket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
