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
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
    public void Connect(final String sendMessage, final Game_activity instance, final MapView mapView,final Organizer organizer){ //サーバとのソケット通信を行う関数
        new AsyncTask<Void,Void,String>(){//非同期処理を行う
          @Override
          protected String doInBackground(Void ... voids){
              String receiveMessage = "";//サーバから受け取った情報を格納する
              Socket connect = null;//サーバとの通信用
              InputStream reader = null;//サーバから送られる文字列を取得する
              BufferedWriter writer = null;//サーバに文字列を送る
              byte[] w = new byte[1024];
              int size = 0;
              try{
                  connect = new Socket("192.168.11.133", 4000); //サーバに接続する
                  reader = connect.getInputStream();
                  writer = new BufferedWriter(new OutputStreamWriter(connect.getOutputStream()));
                  writer.write(sendMessage);//サーバに文字列を送る
                  writer.flush();
                      try{
                          size = connect.getInputStream().read(w);//サーバから送られてきた文字列を取得
                          receiveMessage = new String(w, 0, size, "UTF-8");
                      }catch(SocketTimeoutException e){
                          System.out.println("CausedTimeOut!");
                      }
              } catch (IOException e) {
                  e.printStackTrace();
              } catch (Exception e) {
                  e.printStackTrace();
              }
              System.out.println("SendMessage:"+sendMessage);
              System.out.println("ReceiveMessage:"+receiveMessage);
              String[] id=receiveMessage.split(":",2);//サーバからの情報を':'で2分割
              switch (id[0]){ //サーバからの文字列で処理を分岐
                  case "number"://number:Mynumber
                      instance.myId=id[1];//サーバから割り振られてIDを設定
                      instance.connectEnd=true;
                      break;
                  case "Around"://Around:aroundPeople,N:AlljoinPeople
                      String[] str=id[1].split(",",0);
                      instance.aroundpeople=Integer.parseInt(str[0]);//周囲にいる人、全体の人数を格納
                      instance.allpeople=Integer.parseInt(str[1].split(":",0)[1]);
                      instance.connectEnd=true;
                      break;
                  case "Start"://Start:ByteSize:disaster:disasterScale
                          instance.startFlag=true;
                          String[] disasterinfo=id[1].split(":",0);
                          int byteSize=Integer.parseInt(disasterinfo[0]);
                          int receiveSize=0;
                          while(true){//jsonファイルが送られるのでこれを取得
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
                          instance.connectEnd=true;
                          break;
                  case "Allpeople"://Allpeople:N
                      organizer.allPlayers=Integer.parseInt(id[1]);//全体の人数を取得(主催者用)
                      break;
                  case "DisasterStart": //Disaster:
                      System.out.println("OK!Start");
                      break;
                  case "Waiting...": //Waiting...
                      instance.connectEnd=true;
                      break;
              }
              try {
                  reader.close();
                  writer.close();
                  connect.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
              return receiveMessage;
          }
          @Override
            protected void onPostExecute(String result){
                  if(instance.startFlag && mapView!=null) {
                      try {
                          ObjectMapper mapper = new ObjectMapper(); //受け取った文字列をjson文字列にパースする
                          JsonNode jsonNode = mapper.readTree(instance.dangerplaces);
                          Iterator<String> fieldName=jsonNode.fieldNames();
                          while(fieldName.hasNext()) {//まだデータがあれば取得する
                              String stringJson=fieldName.next();
                              JsonNode node=jsonNode.get(stringJson);
                              String[] coordinates = node.get("Coordinates").asText().split(",", 0);
                              int lon = (int) (Float.parseFloat(coordinates[0]) * 10E5);//緯度、経度、階数を格納
                              int lat = (int) (Float.parseFloat(coordinates[1]) * 10E5);
                              int step = Integer.parseInt(node.get("Step").asText())*5;
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
                              //リストに各円の緯度、経度、半径をpush
                              ArrayList info=new ArrayList();
                              info.add(Float.parseFloat(coordinates[1]));
                              info.add(Float.parseFloat(coordinates[0]));
                              info.add(step);
                              instance.earthquakeInfo.add(info);
                          }
                      } catch (IOException e) { }
                  }
              }
        }.execute();
    }
}