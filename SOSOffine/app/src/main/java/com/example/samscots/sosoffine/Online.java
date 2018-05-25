package com.example.samscots.sosoffine;


import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.example.samscots.sosoffine.MainActivity.mChannel;
import static com.example.samscots.sosoffine.MainActivity.mManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class Online extends Fragment {

    View rootView;
    static final int SERVER_PORT = 4545;
    private static WifiP2pDnsSdServiceRequest serviceRequest;
    static final HashMap<String, String> buddies = new HashMap<String, String>();

    private Button search;
    static String DisplayName;
    static String imgURI = "";
    private RecyclerView recyclerView;
    public static RecyclerView.Adapter addapter;
    public static List<ListOnline> listOnlines;
    private static ListOnline listOnline2;
    public static String device_add = "";
    static int count;
    public static SQLDB sqldbb;
    public static Cursor sqldbb_cursor;
    public static ProgressBar onlineprogressbar;
    public static final String TAG = "Online DEB";
    public static List<WifiP2pDevice> refreshedPeers= new ArrayList<WifiP2pDevice>();
    public static int items;



    public Online() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_online, container, false);

        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        search = (Button) rootView.findViewById(R.id.search);
        onlineprogressbar = (ProgressBar) rootView.findViewById(R.id.online_progress);
        onlineprogressbar.setVisibility(View.GONE);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        DisplayName = preferences.getString("DisplayName", "");
        recyclerView = (RecyclerView) rootView.findViewById(R.id.onine_recycler_frag);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listOnlines = new ArrayList<>();
        addapter = new OnlineAdapter(listOnlines, getContext());
        recyclerView.setAdapter(addapter);


        sqldbb = new SQLDB(getContext());

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
              //  Log.d(TAG, "Successful in adding Discovery Request");
                mManager.requestPeers(mChannel,peerListListener);
            }

            @Override
            public void onFailure(int reasonCode) {
              //  Log.d(TAG, "Failed in adding Discovery Request "+reasonCode);
            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listOnlines != null) {
                    listOnlines.clear();
                    addapter.notifyDataSetChanged();
                    onlineprogressbar.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Searching...", Toast.LENGTH_SHORT).show();
                }

                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                   //     Log.d(TAG, "Successful in adding Discovery Request");
                        mManager.requestPeers(mChannel,peerListListener);
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                     //   Log.d(TAG, "Failed in adding Discovery Request "+reasonCode);
                    }
                });

            }
        });

    }

   /* public static void startRegistration() {
        //  Create a string map containing information about your service.
        Map record = new HashMap();
        record.put("listenport", String.valueOf(SERVER_PORT));
        record.put("buddyname", DisplayName);
        record.put("available", "visible");

        WifiP2pDnsSdServiceInfo serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance("_test", "_presence._tcp", record);

        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
        mManager.addLocalService(mChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Successful in adding LocalService");
            }

            @Override
            public void onFailure(int arg0) {
                Log.d(TAG, "Failed in adding LocalService");
            }
        });
    }

    public static void discoverService() {
        WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(
                    String fullDomain, Map<String, String> record, WifiP2pDevice device) {
                Log.d(TAG, "DnsSdTxtRecord available -" + record.toString());
                buddies.put(device.deviceAddress, record.get("buddyname"));
                Log.d(TAG, "DeviceAddress -" + device.deviceAddress);
                count = 0;
                if (!listOnlines.isEmpty()) {
                    Log.d(TAG, "DeviceAddress -" + "Inside IF");
                    for (int i = 0; i < listOnlines.size(); i++) {
                        ListOnline lo = listOnlines.get(i);
                        if (!lo.device_address.equals(device.deviceAddress)) {
                            count++;
                        } else
                            break;
                    }
                    if (count == listOnlines.size()) {
                        sqldbb_cursor = sqldbb.getAllData();
                        try {
                            if (sqldbb_cursor.getCount() > 0)
                                while (sqldbb_cursor.moveToNext()) {
                                    if (("`" + (device.deviceAddress).substring(3) + "`").equals(sqldbb_cursor.getString(1))) {
                                        imgURI = sqldbb_cursor.getString(4);
                                    }
                                }
                        } catch (Exception e) {
                            //   Toast.makeText(getContext(), "Error Loading Data", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        listOnline2 = new ListOnline(record.get("buddyname"), device.deviceAddress, true, true, imgURI);
                        onlineprogressbar.setVisibility(View.GONE);
                        listOnlines.add(listOnline2);
                        addapter.notifyDataSetChanged();
                    }
                } else {
                    sqldbb_cursor = sqldbb.getAllData();
                    try {
                        if (sqldbb_cursor.getCount() > 0)
                            while (sqldbb_cursor.moveToNext()) {
                                if (("`" + (device.deviceAddress).substring(3) + "`").equals(sqldbb_cursor.getString(1))) {
                                    imgURI = sqldbb_cursor.getString(4);
                                }
                            }
                    } catch (Exception e) {
                        //  Toast.makeText(getContext(), "Error Loading Data", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    Log.d(TAG, "DeviceAddress -" + "Inside ELSE");
                    listOnline2 = new ListOnline(record.get("buddyname"), device.deviceAddress, true, true, imgURI);
                    onlineprogressbar.setVisibility(View.GONE);
                    listOnlines.add(listOnline2);
                    addapter.notifyDataSetChanged();
                }
                record.clear();

                Log.d(TAG, "AfterClear -" + record.toString());
            }
        };

        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType,
                                                WifiP2pDevice resourceType) {

                resourceType.deviceName = buddies
                        .containsKey(resourceType.deviceAddress) ? buddies
                        .get(resourceType.deviceAddress) : resourceType.deviceName;

                // Add to the custom adapter defined specifically for showing
                // wifi devices.

                Log.d(TAG, "onBonjourServiceAvailable " + instanceName);
            }
        };

        mManager.setDnsSdResponseListeners(mChannel, servListener, txtListener);

        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mManager.addServiceRequest(mChannel,
                serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Successful in adding DnsSdServiceRequest");
                    }

                    @Override
                    public void onFailure(int code) {
                        Log.d(TAG, "Failed in adding DnsSdServiceRequest");
                    }
                });

        mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "Successful in adding Discovery Request");
                mManager.requestPeers(mChannel,peerListListener);
            }

            @Override
            public void onFailure(int code) {
                Log.d(TAG, "Failed in adding Discovery Request");
            }
        });

    }*/

    public  static WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            refreshedPeers.clear();
            refreshedPeers.addAll(peerList.getDeviceList());
           // Log.d(TAG, "Refreshed List "+refreshedPeers);
         //   Log.d(TAG, "Peer List "+peers);
         //   Log.d(TAG, "P2P DeviceList "+peerList);
               // peers.addAll(refreshedPeers);


           //     Log.d(TAG, peerList.getDeviceList().size() + " Device Found");

                if(refreshedPeers.size()!=0) {
                   // Toast.makeText(getContext(), peerList.getDeviceList().size() + " Device Found", Toast.LENGTH_SHORT).show();
                    for (int u = 0; u < refreshedPeers.size(); u++) {
                      //  Log.d(TAG,"--------------------------------------------------------------------------------------------------");
                      //  Log.d(TAG,"--------------------------------------------------------------------------------------------------");
                      //  Log.d(TAG,"--------------------------------------------------------------------------------------------------");
                      //  Log.d(TAG,"Primary "+refreshedPeers.get(u).primaryDeviceType );
                      //  Log.d(TAG,"Secondary "+refreshedPeers.get(u).secondaryDeviceType );
                      //  if(refreshedPeers.get(u).isGroupOwner())
                       // Log.d(TAG, refreshedPeers.get(u).deviceName+" is Group Owner");
                      //  else if(!refreshedPeers.get(u).isGroupOwner())
                      //      Log.d(TAG, refreshedPeers.get(u).deviceName+" is Not Group Owner");
                     //   else
                       //     Log.d(TAG, "Dont Know");
                      //  Log.d(TAG, " Contents "+String.valueOf(refreshedPeers.get(u).describeContents()));
                       // Log.d(TAG, "Status "+refreshedPeers.get(u).status);
                      //  Log.d(TAG, "DiscoveryCapable "+refreshedPeers.get(u).isServiceDiscoveryCapable());

                      //  Log.d(TAG,"--------------------------------------------------------------------------------------------------");
                      //  Log.d(TAG,"--------------------------------------------------------------------------------------------------");
                      //  Log.d(TAG,"--------------------------------------------------------------------------------------------------");
                        if (listOnlines.size() != 0){
                            for (int j = 0; j < listOnlines.size(); j++) {
                          //      Log.d(TAG,"Comparing "+listOnlines.get(j).device_name +" With "+refreshedPeers.get(u).deviceName );
                                if (refreshedPeers.get(u).deviceAddress.equals(listOnlines.get(j).device_address)) {

                                   /* String name = refreshedPeers.get(u).deviceName;
                                    String addres = refreshedPeers.get(u).deviceAddress;
                                    sqldbb_cursor = sqldbb.getAllData();

                                    if (sqldbb_cursor.getCount() > 0)
                                        while (sqldbb_cursor.moveToNext()) {
                                            if (("`" + (addres).substring(3) + "`").equals(sqldbb_cursor.getString(1))) {
                                                imgURI = sqldbb_cursor.getString(4);
                                            }
                                        }
                                    listOnline2 = new ListOnline(name, addres, true, true, imgURI);
                                    onlineprogressbar.setVisibility(View.GONE);
                                    listOnlines.add(listOnline2);
                                    addapter.notifyDataSetChanged();*/
                                   break;
                                }
                                else
                                    count++;

                            }
                    }else {
                        //    Log.d(TAG,"Else Just Add" );
                            String name = refreshedPeers.get(u).deviceName;
                            String addres = refreshedPeers.get(u).deviceAddress;
                            sqldbb_cursor = sqldbb.getAllData();

                            Log.d("DeviceInfo", refreshedPeers.get(u).deviceName+"IS GROUP OWNER -"+String.valueOf(refreshedPeers.get(u).isGroupOwner()));
                            Log.d("DeviceInfo", refreshedPeers.get(u).deviceName+"Status -"+refreshedPeers.get(u).status);
                            Log.d("DeviceInfo", refreshedPeers.get(u).deviceName+"DESC -"+refreshedPeers.get(u).describeContents());


                            if (sqldbb_cursor.getCount() > 0)
                                while (sqldbb_cursor.moveToNext()) {
                                    if (("`" + (addres).substring(3) + "`").equals(sqldbb_cursor.getString(1))) {
                                        imgURI = sqldbb_cursor.getString(4);
                                    }
                                }
                            listOnline2 = new ListOnline(name, addres, true, true, imgURI);
                            onlineprogressbar.setVisibility(View.GONE);
                            listOnlines.add(listOnline2);
                            addapter.notifyDataSetChanged();
                        }
                        if(count==listOnlines.size()){
                       //     Log.d(TAG,"Adding coz checked all device" );
                            String name = refreshedPeers.get(u).deviceName;
                            String addres = refreshedPeers.get(u).deviceAddress;
                            sqldbb_cursor = sqldbb.getAllData();

                            if (sqldbb_cursor.getCount() > 0)
                                while (sqldbb_cursor.moveToNext()) {
                                    if (("`" + (addres).substring(3) + "`").equals(sqldbb_cursor.getString(1))) {
                                        imgURI = sqldbb_cursor.getString(4);
                                    }
                                }
                            Log.d("DeviceInfo", refreshedPeers.get(u).deviceName+"IS GROUP OWNER -"+String.valueOf(refreshedPeers.get(u).isGroupOwner()));
                            Log.d("DeviceInfo", refreshedPeers.get(u).deviceName+"Status -"+refreshedPeers.get(u).status);
                            Log.d("DeviceInfo", refreshedPeers.get(u).deviceName+"DESC -"+refreshedPeers.get(u).describeContents());

                            listOnline2 = new ListOnline(name, addres, true, true, imgURI);
                            onlineprogressbar.setVisibility(View.GONE);
                            listOnlines.add(listOnline2);
                            addapter.notifyDataSetChanged();
                        }
                        count=0;

                    }

                }else {
                  //  Toast.makeText(getContext(), "No Device Found!!", Toast.LENGTH_SHORT).show();
                }
            refreshedPeers.clear();



            }

    };




    }
