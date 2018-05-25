package com.example.samscots.sosoffine;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Sam Scots on 11/2/2017.
 */

public class OnlineAdapter extends RecyclerView.Adapter<OnlineAdapter.ViewHolder> {

    private List<ListOnline> listOnlines;
    private Context context;
    private WifiP2pDnsSdServiceRequest serviceRequest;
    public static String connected_device_name;




    public OnlineAdapter(List<ListOnline> listOnlines, Context context) {
        this.listOnlines = listOnlines;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.online_frag_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ListOnline listOnline=listOnlines.get(position);

        holder.Display_Name.setText(listOnline.getDevice_name());
        holder.Display_Address.setText(listOnline.getDevice_address());
        if(!listOnline.isWifi_online())
            holder.wifi_online.setVisibility(View.INVISIBLE);

        if(!listOnline.getPath_Uri().equals("")){
            Uri uriFromPath = Uri.fromFile(new File(listOnline.getPath_Uri()));
            holder.friend_image.setImageURI(uriFromPath);
        }

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, listOnline.getDevice_name(), Toast.LENGTH_SHORT).show();

                CharSequence option[] = new CharSequence[] {"Connect", "Disconnect"};

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Choose Option");
                builder.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){

                            WifiP2pConfig config = new WifiP2pConfig();
                            config.deviceAddress = listOnline.getDevice_address();
                            config.wps.setup = WpsInfo.PBC;
                            if (serviceRequest != null)
                                MainActivity.mManager.removeServiceRequest(MainActivity.mChannel, serviceRequest,
                                        new WifiP2pManager.ActionListener() {

                                            @Override
                                            public void onSuccess() {
                                            }

                                            @Override
                                            public void onFailure(int arg0) {
                                            }
                                        });


                            MainActivity.mManager.connect(MainActivity.mChannel, config, new WifiP2pManager.ActionListener() {

                                @Override
                                public void onSuccess() {
                                    Toast.makeText(context, "Creating a secure connection", Toast.LENGTH_SHORT).show();
                                    connected_device_name=listOnline.getDevice_name();
                                }

                                @Override
                                public void onFailure(int reason) {
                                    String str;
                                    if(reason==0)
                                        str="operation failed due to an internal error.";
                                    else
                                        str="operation failed because the framework is busy and unable to service the request";
                                    Toast.makeText(context, "Connect failed. Retry "+str, Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        if(which==1){

                            if(WiFiDirectBroadcastReceiver.networkInfo.isConnected())

                                try {
                                    if(ServerThread.sockett!=null && !ServerThread.sockett.isClosed()) {
                                        ServerThread.sockett.close();
                                        Toast.makeText(context, "Closed Server Socket", Toast.LENGTH_SHORT).show();
                                    }
                                    if(ClientThread.accept_server!=null && !ClientThread.accept_server.isClosed()) {
                                        ClientThread.accept_server.close();
                                        Toast.makeText(context, "Closed Client Socket", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (IOException e) {
                                    Toast.makeText(context, "Failed to Closed Server Socket", Toast.LENGTH_SHORT).show();
                                }

                            MainActivity.mManager.removeGroup(MainActivity.mChannel, new WifiP2pManager.ActionListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(context, "Group Removed", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int i) {
                                }
                            });

                            if(Online.listOnlines!=null) {
                                Online.listOnlines.clear();
                                Online.addapter.notifyDataSetChanged();
                            }


                        }
                        if(which==2) {
                            WifiP2pConfig config = new WifiP2pConfig();
                            config.deviceAddress = listOnline.getDevice_address();
                            config.wps.setup = WpsInfo.PBC;
                            MainActivity.mManager.requestConnectionInfo(MainActivity.mChannel, new WifiP2pManager.ConnectionInfoListener() {
                                @Override
                                public void onConnectionInfoAvailable(WifiP2pInfo info) {
                                    Log.d("INFOOOO Conn",info.toString());
                                }
                            });
                            MainActivity.mManager.requestGroupInfo(MainActivity.mChannel, new WifiP2pManager.GroupInfoListener() {
                                @Override
                                public void onGroupInfoAvailable(WifiP2pGroup group) {
                                    //Log.d("INFOOOO GRP",group.toString());
                                }
                            });


                        }

                    }
                });
                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listOnlines.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView Display_Name;
        public TextView Display_Address;
        public ImageView wifi_online;
        public ImageView friend_image;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            Display_Name=(TextView)itemView.findViewById(R.id.default_online_display_layout);
            Display_Address=(TextView)itemView.findViewById(R.id.default_onlinedeviceaddress_layout);
            wifi_online=(ImageView)itemView.findViewById(R.id.default_online_wifi_layout);
            relativeLayout=(RelativeLayout)itemView.findViewById(R.id.relativelayout_items);
            friend_image=(ImageView)itemView.findViewById(R.id.default_online_profile_layout);
        }
    }
}
