package com.example.samscots.sosoffine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;


/**
 * Created by Sam Scots on 12/7/2017.
 */

public class ChatBroadcast extends BroadcastReceiver {

    public WifiP2pManager mManager;
    public WifiP2pManager.Channel mChannel;
    public ChatActivity ChhatActivity;
    public static NetworkInfo Info;
    public InetAddress gpoadd;
    public int check_h;

    public ChatBroadcast(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       ChatActivity ChhatActivity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.ChhatActivity = ChhatActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

         if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
             Info = (NetworkInfo) intent
                     .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);


             if (Info.isConnected()) {
                 Toast.makeText(ChhatActivity, "Connected from Chat", Toast.LENGTH_SHORT).show();

                 mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                     @Override
                     public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                         gpoadd=wifiP2pInfo.groupOwnerAddress;
                         Log.d("ChatBroadcast","Address request -"+gpoadd);
                         if(wifiP2pInfo.isGroupOwner)
                             check_h=1;
                         else
                             check_h=0;

                         WiFiDirectBroadcastReceiver.check_hoast=check_h;

                         if(check_h==1){
                             Log.d("ChatBroadcast","Should Start Server");
                             if(WiFiDirectBroadcastReceiver.st==null){
                                 Log.d("ChatBroadcast","st null");
                                 if(ServerThread.sockett!=null && !ServerThread.sockett.isClosed()) {
                                     try {
                                         ServerThread.sockett.close();
                                     } catch (IOException e) {
                                         e.printStackTrace();
                                     }
                                 }

                                 try {

                                     WiFiDirectBroadcastReceiver.serverThread=new ServerThread(WiFiDirectBroadcastReceiver.port);
                                 } catch (IOException e) {
                                     e.printStackTrace();
                                 }
                                 //new Thread(serverThread).start();
                                 Log.d("ChatBroadcast","starting Thread server");
                                 WiFiDirectBroadcastReceiver.st=new Thread(WiFiDirectBroadcastReceiver.serverThread);
                                 WiFiDirectBroadcastReceiver.st.start();
                                 Log.d("ChatBroadcast","created Thread server");
                             }

                         }else{
                             Log.d("ChatBroadcast","Should Start Client");
                             if (WiFiDirectBroadcastReceiver.ct == null) {
                                 Log.d("ChatBroadcast","ct null");
                                 if (ClientThread.accept_server != null && !ClientThread.accept_server.isClosed()) {
                                     try {
                                         ClientThread.accept_server.close();
                                     } catch (IOException e) {
                                         e.printStackTrace();
                                     }
                                 }
                                 Log.d("ChatBroadcast","Address -"+gpoadd);
                                     try {
                                         WiFiDirectBroadcastReceiver.clientThread = new ClientThread(gpoadd, WiFiDirectBroadcastReceiver.port);
                                     } catch (Exception e) {
                                         e.printStackTrace();
                                     }
                                     Log.d("ChatBroadcast","starting Thread client");
                                     WiFiDirectBroadcastReceiver.ct = new Thread(WiFiDirectBroadcastReceiver.clientThread);
                                     WiFiDirectBroadcastReceiver.ct.start();
                                     Log.d("ChatBroadcast","created Thread client");

                             }
                         }
                     }
                 });




             }
             else {
                 Toast.makeText(ChhatActivity, "Disconnected fromn Chat", Toast.LENGTH_SHORT).show();
                 ServerThread.arr_client_handler.clear();
                 try {
                     if (ServerThread.sockett != null && !ServerThread.sockett.isClosed()){
                         ServerThread.sockett.close();
                     Toast.makeText(ChhatActivity, "Closed Server Socket", Toast.LENGTH_SHORT).show();
                         if(WiFiDirectBroadcastReceiver.st.isAlive()) {
                             WiFiDirectBroadcastReceiver.st.interrupt();
                             Toast.makeText(ChhatActivity, "Server Thread Interrupted", Toast.LENGTH_SHORT).show();
                         }
                    }

                     if(ClientThread.accept_server!=null && !ClientThread.accept_server.isClosed()){
                         ClientThread.accept_server.close();
                         Toast.makeText(ChhatActivity, "Closed Clinet Socket", Toast.LENGTH_SHORT).show();

                         if(WiFiDirectBroadcastReceiver.ct.isAlive()) {
                             WiFiDirectBroadcastReceiver.ct.interrupt();
                             Toast.makeText(ChhatActivity, "Client Thread Interrupted", Toast.LENGTH_SHORT).show();
                         }
                     }

                 } catch (IOException e) {
                     Toast.makeText(ChhatActivity, "Failed to Closed Server/Client Socket", Toast.LENGTH_SHORT).show();
                 }
             }
         }
    }
}