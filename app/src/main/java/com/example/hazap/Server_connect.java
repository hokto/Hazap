package com.example.hazap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.client.WebSocketClient;

public class Server_connect extends AppCompatActivity {

    public Handler handler;
    public WebSocketClient client;
    private  static final String TAG="Server_connect";
    public void SocketClient() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                Socket connect = null;
                BufferedReader reader = null;
                BufferedWriter writer = null;
                String message = "result:";
                try {
                    //ソケット通信
                    connect= new Socket("192.168.11.133", 4000);
                    reader = new BufferedReader(new InputStreamReader(connect.getInputStream()));
                    writer = new BufferedWriter(new OutputStreamWriter(connect.getOutputStream()));
                    writer.write("Hello,world!!");
                    writer.flush();
                    String result;
                    //サーバから送られてきた内容がnullでなければmessageに追加
                    while ((result = reader.readLine()) != null) {
                        message += result;
                    }
                } catch (IOException e) {
                    message = "IOException error: " + e.getMessage();
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    message = "Exception: " + e.getMessage();
                } finally {
                    try {
                        reader.close();
                        connect.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(message);
                return message;
            }
            //doInBackGroundの結果を受け取る
            @Override
            protected void onPostExecute(String result) {
                System.out.println(result);
            }
        }.execute();
    }
}
