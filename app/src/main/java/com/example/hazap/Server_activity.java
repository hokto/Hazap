package com.example.hazap;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.telephony.data.ApnSetting;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.GenericArrayType;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;

public class Server_activity extends Activity{
    public void Connect(final String sendMessage, final Game_activity instance){
        new AsyncTask<Void,Void,String>(){
          @Override
          protected String doInBackground(Void ... voids){
              String receiveMessage = "";
              Socket connect = null;
              BufferedReader reader = null;
              BufferedWriter writer = null;
              byte[] w = new byte[1024];
              int size = 0;
              String ss;
              try {
                  //ソケット通信
                  connect = new Socket("192.168.11.133", 4000);
                  reader = new BufferedReader(new InputStreamReader(connect.getInputStream()));
                  writer = new BufferedWriter(new OutputStreamWriter(connect.getOutputStream()));
                  writer.write(sendMessage);
                  writer.flush();
                  String result;
                  //connect.setSoTimeout(1500);
                  //サーバから送られてきた内容がnullでなければmessageに追加
                  size = connect.getInputStream().read(w);
                  receiveMessage = new String(w,0,size,"UTF-8");
                  //String test=reader.readLine();

              } catch (IOException e) {
                  e.printStackTrace();
              } catch (Exception e) {
                  e.printStackTrace();
              }
              try {
                  reader.close();
                  writer.close();
                  connect.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
              System.out.println("ReceiveMessage:"+receiveMessage);
              String[] id=receiveMessage.split(":",0);
              switch (id[0]){
                  case "number"://number:Mynumber
                      instance.myId=id[1];
                      break;
                  case "Around"://Around:aroundPeople,N:AlljoinPeople
                      String[] str=id[1].split(",",0);
                      instance.aroundpeople=Integer.parseInt(str[0]);
                      instance.allpeople=Integer.parseInt(id[2]);
                      break;
                  case "Wait":
                      if(id[1].equals("True")){
                          instance.startFlag=true;
                      }
                      break;
                  default:
                      break;
              }

              return receiveMessage;
          }
          @Override
            protected void onPostExecute(String result){
          }
        }.execute();
    }
}