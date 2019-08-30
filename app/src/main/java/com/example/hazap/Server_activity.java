package com.example.hazap;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.telephony.data.ApnSetting;
import android.util.Base64;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.GenericArrayType;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import jp.co.yahoo.android.maps.CircleOverlay;
import jp.co.yahoo.android.maps.GeoPoint;
import jp.co.yahoo.android.maps.MapView;

public class Server_activity extends Activity{
    public void Connect(final String sendMessage, final Game_activity instance, final MapView mapView, final RelativeLayout relativeLayout){
        new AsyncTask<Void,Void,String>(){
          @Override
          protected String doInBackground(Void ... voids){
              String receiveMessage = "";
              Socket connect = null;
              InputStream reader = null;
              BufferedWriter writer = null;
              byte[] w = new byte[1024];
              int size = 0;
              String ss;
              try{
                  //ソケット通信
                  connect = new Socket("192.168.0.24", 4000);
                  //connect.setSoTimeout(1500);
                  reader = connect.getInputStream();
                  writer = new BufferedWriter(new OutputStreamWriter(connect.getOutputStream()));
                  writer.write(sendMessage);
                  writer.flush();
                  String result;
                      try{
                          size = connect.getInputStream().read(w);
                          receiveMessage = new String(w, 0, size, "UTF-8");
                      }catch(SocketTimeoutException e){
                          System.out.println("CausedTimeOut!");
                      }
              } catch (IOException e) {
                  e.printStackTrace();
              } catch (Exception e) {
                  e.printStackTrace();
              }
              System.out.println("ReceiveMessage:"+receiveMessage);
              String[] id=receiveMessage.split(":",2);
              System.out.println("ID:"+id[0]);
              switch (id[0]){
                  case "number"://number:Mynumber
                      instance.myId=id[1];
                      break;
                  case "Around"://Around:aroundPeople,N:AlljoinPeople
                      String[] str=id[1].split(",",0);
                      instance.aroundpeople=Integer.parseInt(str[0]);
                      instance.allpeople=Integer.parseInt(str[1].split(":",0)[1]);
                      break;
                  case "Start"://Start:ByteSize
                          instance.startFlag=true;
                          int byteSize=Integer.parseInt(id[1]);
                          System.out.println("maxSize:"+byteSize);
                          int receiveSize=0;
                          while(true){
                              byte[] receiveBytes=new byte[131072];
                              try{
                                  int currentSize=reader.read(receiveBytes);
                                  instance.dangerplaces+= new String(receiveBytes,0,currentSize,"UTF-8");
                                  receiveSize+=currentSize;
                                  if(receiveSize>=byteSize) break;
                              }catch(Exception e){
                                  System.out.println("CausedException!");
                              }
                          }
                          break;
                  default:
                      break;
              }
              try {
                  reader.close();
                  writer.close();
                  connect.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
              instance.connectEnd=true;
              return receiveMessage;
          }
          @Override
            protected void onPostExecute(String result){
                  if(instance.startFlag && mapView!=null) {
                      try {
                          System.out.println("OK!");
                          ObjectMapper mapper = new ObjectMapper();
                          JsonNode jsonNode = mapper.readTree(instance.dangerplaces);
                          Iterator<String> fieldName=jsonNode.fieldNames();
                          //ポリゴン精製、表示
                          while(fieldName.hasNext()) {
                              String stringJson=fieldName.next();
                              JsonNode node=jsonNode.get(stringJson);
                              String[] coordinates = node.get("Coordinates").asText().split(",", 0);
                              int lon = (int) (Float.parseFloat(coordinates[0]) * 10E5);
                              int lat = (int) (Float.parseFloat(coordinates[1]) * 10E5);
                              int step = Integer.parseInt(node.get("Step").asText());
                              GeoPoint mid = new GeoPoint(lat, lon);
                              CircleOverlay circleOverlay = new CircleOverlay(mid, step, step) {
                                  @Override
                                  protected boolean onTap() {
                                      //円をタッチした際の処理

                                      return true;
                                  }
                              };
                              //色の変更
                              circleOverlay.setFillColor(Color.argb(127, 255, 40, 40));
                              circleOverlay.setStrokeColor(Color.argb(127, 255, 50, 50));
                              mapView.getOverlays().add(circleOverlay);
                              mapView.invalidate();
                          }
                      } catch (IOException e) { }
                  }
              }
        }.execute();
    }
}