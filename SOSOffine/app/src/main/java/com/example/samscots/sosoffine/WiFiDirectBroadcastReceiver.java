package com.example.samscots.sosoffine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by Sam Scots on 28/10/2017.
 */

class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    public WifiP2pManager mManager;
    public WifiP2pManager.Channel mChannel;
    public MainActivity mActivity;
    public static InetAddress groupOwnerAddress;
    public static NetworkInfo networkInfo;
    public static int state;
    public static int check_hoast;
    public static final int port=8080;
    public  static ClientThread clientThread;
    public static  ServerThread serverThread;
    public static Thread st;
    public static Thread ct;
    public static String s;
    public String TAG="WifiBroadCast";
    public static int connections=0;




    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       MainActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

             state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);



            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Toast.makeText(mActivity, "P2P Enabled", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "P2P Enabled");
             //   Online.startRegistration();
              //  Online.discoverService();
            }

            if (state == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED){
                Toast.makeText(mActivity, "Discovery Started ", Toast.LENGTH_SHORT).show();
             //   Online.startRegistration();
             //   Online.discoverService();
                Log.d(TAG,"Discovery Started ");
            }

            if (state == WifiP2pManager.WIFI_P2P_STATE_DISABLED)
               // Toast.makeText(mActivity, "P2P Disabled", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"P2P Disabled");

            if (state == WifiP2pManager.BUSY)
               // Toast.makeText(mActivity, "P2P Busy", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"P2P Busy");

            if (state == WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED)
               // Toast.makeText(mActivity, "P2P Discovery Stopped", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"P2P Discovery Stopped");

            if (state == WifiP2pManager.ERROR)
              //  Toast.makeText(mActivity, "P2P Error", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"P2P Error");

            if (state == WifiP2pDevice.CONNECTED)
              //  Toast.makeText(mActivity, "P2P CONNECTED", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"P2P Connected");

            if (state == WifiP2pDevice.FAILED)
              //  Toast.makeText(mActivity, "P2P FAILED", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"P2P Failed");

            if (state == WifiP2pDevice.AVAILABLE)
              //  Toast.makeText(mActivity, "P2P AVAILABLE", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"P2P Available");

            if (state == WifiP2pDevice.INVITED)
              //  Toast.makeText(mActivity, "P2P INVITED", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"P2P Invited");



        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
           // Toast.makeText(mActivity, "Wi-Fi WIFI_P2P_PEERS_CHANGED", Toast.LENGTH_SHORT).show();

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
           // Toast.makeText(mActivity, "Wi-Fi WIFI_P2P_THIS_DEVICE_CHANGED Action", Toast.LENGTH_SHORT).show();


        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            Log.d("Wifi-Brodcast","Peer has Changed Action");

            if (mManager == null) {
                return;
            }
            networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);



            if (networkInfo.isConnected()) {
                Toast.makeText(mActivity, "Connected Successfully", Toast.LENGTH_SHORT).show();
                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        groupOwnerAddress = info.groupOwnerAddress;

                        if (groupOwnerAddress != null)
                        {
                        s = groupOwnerAddress.getHostAddress();

                        if (info.isGroupOwner) {

                            Toast.makeText(mActivity, "Server ", Toast.LENGTH_SHORT).show();
                            check_hoast=1;

                            if(ServerThread.sockett!=null && !ServerThread.sockett.isClosed()) {
                                try {
                                    ServerThread.sockett.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            try {

                                serverThread=new ServerThread(port);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            st=new Thread(serverThread);
                            st.start();

                        } else {


                            Toast.makeText(mActivity, "Client", Toast.LENGTH_SHORT).show();
                            check_hoast=0;

                            if(ClientThread.accept_server!=null && !ClientThread.accept_server.isClosed()) {
                                try {
                                    ClientThread.accept_server.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            try {
                                clientThread=new ClientThread(groupOwnerAddress,port);
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                            ct=new Thread(clientThread);
                            ct.start();



                        }

                    }
                    }
                });


            }else {

                if(Online.listOnlines!=null) {
                    Online.listOnlines.clear();
                    Online.addapter.notifyDataSetChanged();
                   // Online.discoverService();
                  //  Online.startRegistration();
                }
                ServerThread.arr_client_handler.clear();
                Toast.makeText(mActivity, "Disconnected", Toast.LENGTH_SHORT).show();
                try {
                    if(ServerThread.sockett!=null && !ServerThread.sockett.isClosed()) {
                        ServerThread.sockett.close();
                        Toast.makeText(mActivity, "Socket Disconnected", Toast.LENGTH_SHORT).show();
                        if(st.isAlive()) {
                            st.interrupt();
                            Toast.makeText(mActivity, "Server Thread Interrupted", Toast.LENGTH_SHORT).show();
                        }

                    }
                    if(ClientThread.accept_server!=null && !ClientThread.accept_server.isClosed()){
                        ClientThread.accept_server.close();
                        Toast.makeText(mActivity, "Closed Clinet Socket", Toast.LENGTH_SHORT).show();
                        if(ct.isAlive()) {
                            ct.interrupt();
                            Toast.makeText(mActivity, "Client Thread Interrupted", Toast.LENGTH_SHORT).show();
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(mActivity, "Error Closing Server/Client Socket", Toast.LENGTH_SHORT).show();
                }
                Log.d("WIFI_BROADCAST","Connections Bef2  "+connections);

                connections--;

                Log.d("WIFI_BROADCAST","Connections Aft2  "+connections);

            }

           // Online.discoverService();
        }
    }


}