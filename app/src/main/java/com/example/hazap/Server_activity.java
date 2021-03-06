package com.example.hazap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;
import jp.co.yahoo.android.maps.CircleOverlay;
import jp.co.yahoo.android.maps.GeoPoint;
import jp.co.yahoo.android.maps.MapView;


public class Server_activity extends Activity{
    private String dangerplaces;
    private String[] disasterinfo;
    @SuppressLint("StaticFieldLeak")
    public void Connect(final String sendMessage, final Game_activity player, final MapView mapView, final Organizer organizer){ //サーバとのソケット通信を行う関数
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
                  connect = new Socket("192.168.0.18", 4000); //サーバに接続する
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
              String[] orderMes=receiveMessage.split(":",2);//サーバからの情報を':'で2分割
              switch (orderMes[0]){ //サーバからの文字列で処理を分岐
                  case "number"://number:Mynumber
                      player.myId=orderMes[1];//サーバから割り振られてIDを設定
                      player.connectEnd=true;
                      break;
                  case "Around"://Around:aroundPeople,N:AlljoinPeople:Run?
                      String[] str=orderMes[1].split(",",0);
                      player.aroundpeople=Integer.parseInt(str[0]);//周囲にいる人、全体の人数を格納
                      player.allpeople=Integer.parseInt(str[1].split(":",0)[1]);
                      if(Integer.parseInt(str[1].split(":",0)[2])==1 || player.aroundpeople/(50.0*50.0*Math.PI)>=1.8){
                          player.hp-=5;
                      }
                      player.connectEnd=true;
                      break;
                  case "Start"://Start:ByteSize:disaster:disasterScale
                          dangerplaces="";
                          disasterinfo=orderMes[1].split(":",0);
                          player.disaster=disasterinfo[1];
                          player.disastersize=disasterinfo[2];
                          int byteSize=Integer.parseInt(disasterinfo[0]);
                          int receiveSize=0;
                          while(true){//jsonファイルが送られるのでこれを取得
                              byte[] receiveBytes=new byte[131072];
                              try{
                                  int currentSize=reader.read(receiveBytes);
                                  dangerplaces+= new String(receiveBytes,0,currentSize,"UTF-8");
                                  receiveSize+=currentSize;
                                  if(receiveSize>=byteSize) break;
                              }catch(Exception e){
                                  System.out.println("CausedException!");
                              }
                          }
                          if(player!=null) player.connectEnd=true;
                          if(player!=null){
                            player.startFlag=true;
                          }
                          else{
                            organizer.startFlag=true;
                          }
                          break;
                  case "Allpeople"://Allpeople:N
                      organizer.allPlayers=Integer.parseInt(orderMes[1]);//全体の人数を取得(主催者用)
                      break;
                  case "DisasterStart": //Disaster:
                      System.out.println("OK!Start");
                      break;
                  case "Waiting...": //Waiting...
                      player.connectEnd=true;
                      break;
                  case "Result"://Result:Aliverate:ImageSize:OrganizerMessage:Place:HP:Route:Time
                      player.startFlag=false;
                      String[] resultInfo=orderMes[1].split(":",0);
                      player.aliveRate=Integer.parseInt(resultInfo[0]);
                      int imgSize=Integer.parseInt(resultInfo[1]);
                      int receiveimgSize=0;
                      player.organizerMessage=resultInfo[2];
                      for(int i=0;i<4;i++){
                          player.evacuParams[i]=(int)Float.parseFloat(resultInfo[i+3]);
                      }
                      ByteBuffer buffer=ByteBuffer.allocate(imgSize);
                      while(true){//jsonファイルが送られるのでこれを取得
                          byte[] receiveBytes=new byte[131072];
                          try{
                              int currentSize=reader.read(receiveBytes);
                              byte[] addByte= Arrays.copyOf(receiveBytes,currentSize);
                              buffer.put(addByte);
                              receiveimgSize+=currentSize;
                              if(receiveimgSize>=imgSize) break;
                          }catch(Exception e){
                              System.out.println("CausedException!");
                          }
                      }
                      if(buffer.array()!=null){
                          player.routeMap= BitmapFactory.decodeByteArray(buffer.array(),0,imgSize);
                          player.connectEnd=true;
                      }
                      break;
                  case "Coordinates":
                      String[] coordinates=orderMes[1].split(":",0);
                      organizer.playerCoordinates=new ArrayList<GeoPoint>();
                      for(int i=0;i<coordinates.length;i++){
                          String[] playerCoord=coordinates[i].split(",",0);
                          organizer.playerCoordinates.add(new GeoPoint((int)(Float.parseFloat(playerCoord[0])*10E5),(int)(Float.parseFloat(playerCoord[1])*10E5)));
                      }
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
                  if((player.startFlag||organizer.startFlag)&& mapView!=null) {
                      try {
                          ObjectMapper mapper = new ObjectMapper(); //受け取った文字列をjson文字列にパースする
                          JsonNode jsonNode = mapper.readTree(dangerplaces);
                          Iterator<String> fieldName=jsonNode.fieldNames();
                          if(disasterinfo[1].equals("地震")){
                              Pattern pattern=Pattern.compile("(0406[0-9]{2})|(0305007)|(0425[0-9]{2})|(0412021)");
                              String arv=fieldName.next();
                              String[] minARV_str=jsonNode.get(arv).asText().split(",",0);
                              float[] ARV=new float[3];
                              for(int i=0;i<3;i++) {
                                  ARV[i]=Float.parseFloat(minARV_str[i]);
                              }
                              double MinARV=9999;
                              switch (player.disastersize){
                                  case "震度5強":
                                      MinARV=ARV[2];
                                      break;
                                  case "震度6":
                                      MinARV=ARV[1];
                                      break;
                                  case "震度7":
                                      MinARV=ARV[0];
                                      break;
                              }
                              while(fieldName.hasNext()) {//まだデータがあれば取得する
                                  String stringJson=fieldName.next();
                                  JsonNode node=jsonNode.get(stringJson);
                                  if(stringJson=="MinARV")continue;
                                  String[] coordinates = node.get("Coordinates").asText().split(",", 0);
                                  if(!pattern.matcher(node.get("Code").asText()).find()) {
                                      int lon = (int) (Float.parseFloat(coordinates[0]) * 10E5);//緯度、経度、階数を格納
                                      int lat = (int) (Float.parseFloat(coordinates[1]) * 10E5);
                                      int step = (int)(Integer.parseInt(node.get("Step").asText()) * 5*Float.parseFloat(node.get("ARV").asText()));
                                      GeoPoint mid = new GeoPoint(lat, lon);
                                      CircleOverlay circleOverlay = new CircleOverlay(mid, step, step) {
                                          @Override
                                          protected boolean onTap() {
                                              //円をタッチした際の処理
                                              return true;
                                          }
                                      };
                                      //色の変更
                                      //if(MinARV<Float.parseFloat(node.get("ARV").asText())){
                                          circleOverlay.setFillColor(Color.argb(127, 255, 40, 40));
                                          circleOverlay.setStrokeColor(Color.argb(127, 255, 50, 50));
                                          mapView.getOverlays().add(circleOverlay);
                                          mapView.invalidate();
                                          //リストに各円の緯度、経度、半径をpush
                                          ArrayList info = new ArrayList();
                                          info.add(Float.parseFloat(coordinates[1]));
                                          info.add(Float.parseFloat(coordinates[0]));
                                          info.add(step);
                                          if(player!=null) player.earthquakeInfo.add(info);
                                      //}
                                  }
                              }
                          }else if(disasterinfo[1].equals("津波")) {
                              if(player!=null){
                                  System.out.println(dangerplaces);
                                  player.tsunamiNode=mapper.readTree(dangerplaces);;
                              }
                              else{
                                  organizer.tsunamiNode=mapper.readTree(dangerplaces);;
                              }

                          }
                  } catch (IOException e) { }
              }
          }
        }.execute();
    }
}