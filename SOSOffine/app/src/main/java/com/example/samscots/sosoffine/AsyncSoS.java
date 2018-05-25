package com.example.samscots.sosoffine;

import android.app.ProgressDialog;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.samscots.sosoffine.MainActivity.mChannel;
import static com.example.samscots.sosoffine.MainActivity.mManager;

/**
 * Created by Sam Scots on 2/2/2018.
 */

public class AsyncSoS implements Runnable {
    List<ListOnline> list;
    private WifiP2pDnsSdServiceRequest serviceRequest;
    public static final String TAG="AsyncSoS";
    public String emer="";
    public int i=0;
    public int val=0;
    MainActivity mainActivity;
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    public ProgressDialog pd;
    public int attempts=1;

    ListOnline[] lo;


    public AsyncSoS(List<ListOnline> list,MainActivity mainActivity){
        this.list=list;
        this.mainActivity=mainActivity;
    }

    @Override
    public void run() {

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pd=new ProgressDialog(mainActivity);
                pd.setMax(100);
                pd.setMessage("Please Wait...");
                pd.setTitle("Emergency Message");
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.show();
            }
        });



        lo=new ListOnline[Online.listOnlines.size()];
        for(ListOnline listOnline:Online.listOnlines) {
            lo[i]=listOnline;
            Log.d(TAG, "User "+i+":"+ listOnline.device_name);
            i++;
        }
        val=(100/i)+2;



        for(i=0;i<lo.length;i++){

            Log.d(TAG,"Sleeping for 4 sec");
            Log.d(TAG,"Array Size "+lo.length);
            for(int j=0;j<lo.length;j++)
            Log.d(TAG,"Consists "+lo[j].device_name);

            final ListOnline listOnline=lo[i];

            Log.d(TAG,"Sleeping for 4 sec");

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


           // ListOnline listOnline=list.get(i);
            Log.d(TAG,"User "+listOnline.device_name);
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pd.setMessage("Connecting "+listOnline.device_name);
                }
            });

            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = listOnline.getDevice_address();
            config.wps.setup = WpsInfo.PBC;
            config.groupOwnerIntent = 15;
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
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

          while(!WiFiDirectBroadcastReceiver.networkInfo.isConnected() && attempts<4){

              mainActivity.runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      pd.setMessage("Reconnecting "+listOnline.device_name+" Attempts-"+attempts);
                  }
              });

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
                attempts++;
            }
            attempts=1;


            if(WiFiDirectBroadcastReceiver.networkInfo.isConnected())
            {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.setMessage("Connected to "+listOnline.device_name);
                    }
                });


                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.setProgress(val/2);
                    }
                });

                Log.d(TAG,"Sleeping for 4 sec");
                try {
                    Thread.sleep(4000);
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
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.setMessage("Sending Emergency Message to "+listOnline.device_name);
                    }
                });

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

                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.setMessage("Disconnecting "+listOnline.device_name);
                    }
                });
            }

            Log.d(TAG,"Done Sending Emergency Message ");
            Log.d(TAG,"Sleeping for 4 sec");

         //   Online.startRegistration();
        //    Online.discoverService();


            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG,"Value of val "+val);
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pd.setProgress(val);
                    if(pd.getProgress()>=pd.getMax()) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        pd.dismiss();
                    }

                }
            });

            val=val+val;
        }

       // Online.startRegistration();
     //   Online.discoverService();
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mainActivity, "Done Sending Emergency Message !!", Toast.LENGTH_SHORT).show();
            }
        });


    }
}