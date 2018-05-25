package com.example.samscots.sosoffine;

import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.samscots.sosoffine.MainActivity.mChannel;
import static com.example.samscots.sosoffine.MainActivity.mManager;

/**
 * Created by Sam Scots on 2/2/2018.
 */

public class AsyncSoS2 implements Runnable{
    List<ListOnline> list;
    String sender_add;
    private WifiP2pDnsSdServiceRequest serviceRequest;
    public static final String TAG="AsyncSoS22222";
    public String emer="";
    public int i=0;
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    ListOnline[] lo;


    public AsyncSoS2(String sender_add){
        this.sender_add=sender_add;
    }

    @Override
    public void run() {

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        lo=new ListOnline[Online.listOnlines.size()];
        for(ListOnline listOnline:Online.listOnlines) {
            String check="`"+(listOnline.device_address).substring(3)+"`";
            Log.d(TAG,"Address Provided "+sender_add);
            Log.d(TAG,"Checking for "+check);
            if(!check.equals(sender_add)) {
                lo[i] = listOnline;
                Log.d(TAG, "User " + i + ":" + listOnline.device_name);
                i++;
            }
        }

        for(int j=0;j<i;j++){

            Log.d(TAG,"Sleeping for 4 sec");
            Log.d(TAG,"Array Size "+lo.length);
           // for(int j=0;j<lo.length;j++)
          //  Log.d(TAG,"Consists "+lo[j].device_name);

            ListOnline listOnline=lo[j];
            Log.d(TAG,"Current Operation "+lo[j].device_name);

            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

           // ListOnline listOnline=list.get(i);
            Log.d(TAG,"User "+listOnline.device_name);

            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = listOnline.getDevice_address();
            config.wps.setup = WpsInfo.PBC;
            Log.d(TAG,listOnline.device_name+" : "+listOnline.getDevice_address());
            if (serviceRequest != null)
                mManager.removeServiceRequest(mChannel, serviceRequest,
                        new WifiP2pManager.ActionListener() {

                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onFailure(int arg0) {
                            }
                        });
            Log.d(TAG,"Trying to Connect ");

            mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    //    Toast.makeText(context, "Creating a secure connection", Toast.LENGTH_SHORT).show();
                    //  connected_device_name=listOnline.getDevice_name()

                }

                @Override
                public void onFailure(int reason) {
                    String str;
                    if(reason==0)
                        str="operation failed due to an internal error.";
                    else
                        str="operation failed because the framework is busy and unable to service the request";
                    //Toast.makeText(context, "Connect failed. Retry "+str, Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"Connect failed. Retry "+str);
                }
            });

            Log.d(TAG,"Sleeping for 4 sec");
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while(!WiFiDirectBroadcastReceiver.networkInfo.isConnected()){
                Log.d(TAG,"Not Connected");
                Log.d(TAG,"Sleeping for 4 sec");
                try {
                    Thread.sleep(8000);

                    Log.d(TAG, "Trying To Discover");
                    mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFailure(int reasonCode) {
                        }
                    });



                    Log.d(TAG,"Sleeping for 8 seconds ");
                    Thread.sleep(8000);

                    Log.d(TAG,"Trying To Connect Again");
                    //Try again
                    mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

                        @Override
                        public void onSuccess() {
                            //    Toast.makeText(context, "Creating a secure connection", Toast.LENGTH_SHORT).show();
                            //  connected_device_name=listOnline.getDevice_name()

                        }

                        @Override
                        public void onFailure(int reason) {
                            String str;
                            if(reason==0)
                                str="operation failed due to an internal error.";
                            else
                                str="operation failed because the framework is busy and unable to service the request";
                            //Toast.makeText(context, "Connect failed. Retry "+str, Toast.LENGTH_SHORT).show();
                            Log.d(TAG,"Connect failed. Retry "+reason+" "+str);
                        }
                    });




                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Log.d(TAG,"Connected");



            if(WiFiDirectBroadcastReceiver.networkInfo.isConnected())
            {
                Log.d(TAG,"Sleeping for 4 sec");
                try {
                    Thread.sleep(8000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                Log.d(TAG,"Connection Success");
                emer="EMERGE"+MainActivity.getMacAddr()+"#This is a Emergency Message.";
                if(WiFiDirectBroadcastReceiver.check_hoast==1) {
                    WiFiDirectBroadcastReceiver.serverThread.emer(emer);
                    Log.d(TAG,"Sending Via Server");
                }
                else {
                    WiFiDirectBroadcastReceiver.clientThread.send_emerge(emer);
                    Log.d(TAG,"Sending Via Client");
                }

                Log.d(TAG,"Sleeping for 4 sec");
                try {
                    Thread.sleep(8000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    if(ServerThread.sockett!=null && !ServerThread.sockett.isClosed()) {
                        ServerThread.sockett.close();
                        // Toast.makeText(context, "Closed Server Socket", Toast.LENGTH_SHORT).show();
                    }
                    if(ClientThread.accept_server!=null && !ClientThread.accept_server.isClosed()) {
                        ClientThread.accept_server.close();
                        //   Toast.makeText(context, "Closed Client Socket", Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    // Toast.makeText(context, "Failed to Closed Server Socket", Toast.LENGTH_SHORT).show();
                }

                mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        //   Toast.makeText(context, "Group Removed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i) {
                    }
                });
            }

            Log.d(TAG,"Done Sending Emergency Message ");
            Log.d(TAG,"Sleeping for 4 sec");
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

    }
}