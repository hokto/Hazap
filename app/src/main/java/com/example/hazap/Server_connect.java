package com.example.hazap;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
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
    public void Articulate()
    {
        handler=new Handler();
        if ("sdk".equals(Build.PRODUCT)) {
            java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
            java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
        }
        try{
            URI uri=new URI("ws://192.168.11.133:4000");
            client=new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d(TAG,"onOpen");
                }

                @Override
                public void onMessage(final String message) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(message);
                        }
                    });
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d(TAG,"onClose");
                }

                @Override
                public void onError(Exception ex) {
                    Log.d(TAG,"onError");
                }
            };
            client.connect();

        }
        catch (URISyntaxException e){
            e.printStackTrace();
        }
    }
}