package com.example.hazap;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server_activity {
    public static void main() {
        try {
            ServerSocket hazap_server = new ServerSocket(1);
            Socket sock = hazap_server.accept();
            DataInputStream in = new DataInputStream(sock.getInputStream());
            double latitude = in.readDouble();
            double longitude =in.readDouble();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        try{
            Socket sock = new Socket("hazap_client",2);
            OutputStream out = sock.getOutputStream();
            String sendData = "アドバイス";
            out.write(sendData.getBytes("UTF-8"));
            System.out.println("「"+sendData+"」を送信しました。");
            out.close();
            sock.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
