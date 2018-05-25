package com.example.samscots.sosoffine;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity implements WifiP2pManager.ChannelListener {

    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private DrawerLayout dl;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView tv;
    public static WifiManager wifiManager;
    private IntentFilter mIntentFilter;
    public static Context context;
    public static WifiP2pManager mManager;
    public static WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private Switch swch;
    public static Handler server_handler;
    public static String DisplayName,loc="",add="",name="";
    public static int c;
    public static String no;
    public static String realpath;
    int count;
    public static byte[] if_error;
    public SQLiteHelper sqLiteHelper;
    public Cursor sqlcursor;
    public static Thread threead;




    //Notification
    private Notification.Builder main_notification_builder;
    private NotificationManager main_notification_manager;
    //Notification




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Notification
        main_notification_manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        //main_remote_viewes=new RemoteViews(getPackageName(),R.layout.notification);
        //Notification


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);



        //TAb
        mViewPager=(ViewPager)findViewById(R.id.tab_pager);
        mPagerAdapter=new PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        mTabLayout=(TabLayout)findViewById(R.id.main_tab);
        mTabLayout.setupWithViewPager(mViewPager);
        dl=(DrawerLayout)findViewById(R.id.drawer_layout);
        actionBarDrawerToggle=new ActionBarDrawerToggle(this,dl,R.string.open,R.string.close);
        dl.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Wi-Fi");


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header=navigationView.getHeaderView(0);
        tv=(TextView)header.findViewById(R.id.displayname);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        DisplayName = preferences.getString("DisplayName", "");

        tv.setText(DisplayName);
        Log.d("Display Name",DisplayName);
        realpath= preferences.getString("RealPath","");
        Log.d("The Real Path MAin ",realpath);

        //Floating
        ImageView icon = new ImageView(MainActivity.this); // Create an icon
        icon.setImageResource(R.drawable.alarmsiren);
        FloatingActionButton actionButton = new FloatingActionButton.Builder(MainActivity.this)
                .setContentView(icon)
                .build();
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

        ImageView sos = new ImageView(this);
        sos.setImageResource(R.drawable.emergencysms);
        final SubActionButton emergency = itemBuilder.setContentView(sos)
                .build();

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(emergency)
                .attachTo(actionButton)
                .build();

        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(MainActivity.this);
                myAlertDialog.setTitle("Attention");
                myAlertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                myAlertDialog.setMessage("This button is only meant for emergency purposes and should never be used in other situations. " +
                        "So please do not click here without valid reasons for doing so.");
                myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(MainActivity.this, "Please wait, it will take some time", Toast.LENGTH_SHORT).show();


                        if(Online.listOnlines.size()!=0){
                            emergency.setEnabled(false);


                            AsyncSoS asyncSoS = new AsyncSoS(Online.listOnlines,MainActivity.this);
                            Thread th = new Thread(asyncSoS);
                            th.start();

                        }

                    }});
                myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(MainActivity.this, "Operation Canceled", Toast.LENGTH_SHORT).show();
                    }});
                myAlertDialog.show();



            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.profile:
                        Intent go_start=new Intent(MainActivity.this,StartActivity.class);
                        startActivity(go_start);
                        break;
                    case R.id.settings:
                        Toast.makeText(MainActivity.this, "Settings Selected", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.about:
                        Toast.makeText(MainActivity.this, "About Selected", Toast.LENGTH_SHORT).show();
                        break;
                }


                return false;
            }
        });





        //Intents
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mIntentFilter.addAction(String.valueOf(WifiP2pDevice.AVAILABLE));
        mIntentFilter.addAction(String.valueOf(WifiP2pDevice.UNAVAILABLE));
        mIntentFilter.addAction(String.valueOf(WifiP2pDevice.CONNECTED));
        mIntentFilter.addAction(String.valueOf(WifiP2pDevice.INVITED));
        mIntentFilter.addAction(String.valueOf(WifiP2pDevice.FAILED));
        //Intents

        created();
        //Change Wifi Direct Name
        setDeviceName(DisplayName);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, MainActivity.this);

        server_handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //Toast.makeText(MainActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
                //main_remote_viewes.setTextViewText(R.id.notification_text_view,(String)msg.obj);
                //do_notify((String)msg.obj);
                if(msg.obj!=null) {
                    String type = ((String) msg.obj).substring(0, Math.min(((String) msg.obj).length(), 6));
                    if (type.equals("PROFIL")) {
                        byte[] a = ("PROFILImage Bytes").getBytes();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (WiFiDirectBroadcastReceiver.check_hoast == 1)
                                    WiFiDirectBroadcastReceiver.serverThread.send_profile_image();
                            }
                        });

                        //UsersChat uc=new UsersChat("Add","Name","This Will Be The Last Message","no");
                        add_data(((String) msg.obj).substring(6));
                    } else if (type.equals("EMERGE")) {
                       // created();
                        emer(((String) msg.obj).substring(6));
                    } else if (type.equals("STRING")) {
                     //   update_others("STRING",(String) msg.obj);
                      //  do_notify(((String) msg.obj).substring(6));

                    }else if (type.equals("CALLIN")){

                    }else{
                        update_others("BTRING",(String) msg.obj);
                        do_notify("Image Received");
                    }
                }

            }
        };

        if_error=getbyte();

        DiscoveryUpdater du = new DiscoveryUpdater();
        threead = new Thread(du);
        threead.start();
    }

    public void do_notify(String mmmess){

        if(!mmmess.equals("")) {
            //  Intent notification_main = new Intent(this, MainActivity.class);
            // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notification_main, 0);
            Notification main_noti = new Notification.Builder(MainActivity.this)
                    .setTicker(MainActivity.name).setContentTitle("Pebble Talk")
                    .setContentText(MainActivity.name+": "+mmmess)
                    .setSmallIcon(R.drawable.notificationicn)
                    .setAutoCancel(true)
                    //.setContentIntent(pendingIntent)
                    .getNotification();
            main_noti.flags |= Notification.FLAG_AUTO_CANCEL;
            main_notification_manager.notify(0, main_noti);
        }
    }

    void emer(String mmmess){

        String msg="",adres="";
        char ch;
        int i=0;
        ch=mmmess.charAt(i);
        while(ch!='#'){
            adres+=ch;
            i++;
            ch=mmmess.charAt(i);
        }
        msg=mmmess.substring(i+1,mmmess.length());
        SQLiteHelper sqLiteHelper=new SQLiteHelper(this,adres);
        sqLiteHelper.addData("STRING"+msg,0);

        Notification main_noti = new Notification.Builder(MainActivity.this)
                .setTicker("Emergency Message ").setContentTitle("Pebble Talk")
                .setContentText(MainActivity.name+": "+msg)
                .setSmallIcon(R.drawable.notificationicn)
                .setAutoCancel(true)
                //.setContentIntent(pendingIntent)
                .getNotification();
        main_noti.flags |= Notification.FLAG_AUTO_CANCEL;
        main_notification_manager.notify(0, main_noti);


        AsyncSoS2 asyncSoS2 = new AsyncSoS2(adres);
        Thread th = new Thread(asyncSoS2);
        th.start();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String DisplayName = preferences.getString("DisplayName", "");
        if(DisplayName.equals("")){
            Intent go_start=new Intent(MainActivity.this,StartActivity.class);
            startActivity(go_start);
        }

        registerReceiver(mReceiver, mIntentFilter);


        created();

        Toast.makeText(MainActivity.this, "Reinitialized", Toast.LENGTH_SHORT).show();


        // if(WiFiDirectBroadcastReceiver.networkInfo.
        if(WiFiDirectBroadcastReceiver.networkInfo!=null)
            if(WiFiDirectBroadcastReceiver.networkInfo.isConnected())
                MainActivity.mManager.removeGroup(MainActivity.mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "Disconnecting", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i) {
                    }
                });

        if(Online.listOnlines!=null) {
            Online.listOnlines.clear();
            Online.addapter.notifyDataSetChanged();
        }

        try {
            if(ServerThread.sockett!=null && !ServerThread.sockett.isClosed()) {
                ServerThread.sockett.close();
                Toast.makeText(this, "Closed Server Socket", Toast.LENGTH_SHORT).show();
            }
            if(ClientThread.accept_server!=null && !ClientThread.accept_server.isClosed()) {
                ClientThread.accept_server.close();
                Toast.makeText(this, "Closed Client Socket", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            Toast.makeText(this, "Failed to Closed Server Socket", Toast.LENGTH_SHORT).show();
        }

       // if(!DisplayName.equals("") && threead!=null) {

        //}else{
          //  Toast.makeText(context, "Display Name Required For Searching", Toast.LENGTH_SHORT).show();
       // }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mReceiver!=null)
            unregisterReceiver(mReceiver);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_in_action,menu);

        MenuItem menuItem=menu.findItem(R.id.myswitch);
        menuItem.setActionView(R.layout.switch_layout);
        swch=(Switch)menu.findItem(R.id.myswitch).getActionView().findViewById(R.id.switch1);

        //Switch
        if (wifiManager.isWifiEnabled()) {
            swch.setChecked(true);
        }

        swch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    if (!wifiManager.isWifiEnabled())
                        wifiManager.setWifiEnabled(true);
                    Toast.makeText(MainActivity.this, "Turning On Wi-Fi", Toast.LENGTH_SHORT).show();
                }else {
                    if (wifiManager.isWifiEnabled())
                        wifiManager.setWifiEnabled(false);
                    Toast.makeText(MainActivity.this, "Turning Off Wi-Fi", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return true;
    }


    public  void created(){
        //Ini Wifi Manager
        context=getApplicationContext();
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //Ini Wifi Manager
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mManager.initialize(this, getMainLooper(), this);
        mChannel = mManager.initialize(MainActivity.this, getMainLooper(), null);
        setDeviceName(DisplayName);

    }


    @Override
    public void onChannelDisconnected() {
        Toast.makeText(this, "Channel Was Disconnected", Toast.LENGTH_SHORT).show();
        mManager.initialize(this, getMainLooper(), this);
    }

    @Override
    protected void onStop() {
        if(Online.listOnlines!=null) {
            Online.listOnlines.clear();
            Online.addapter.notifyDataSetChanged();
        }

        super.onStop();
    }

    public byte[] getbyte(){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitMapData = stream.toByteArray();
        return bitMapData;

    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    c=(b & 0xFF);
                    no=String.format("%02d", c);
                    if(Integer.parseInt(no)<10)
                        res1.append(no + ":");
                    else
                        res1.append(Integer.toHexString(b & 0xFF) + ":");

                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return ("`"+res1.toString().substring(3)+"`");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "`00:00:00:00:00`";
    }

    public void add_data(String data){

        name="";
        int l=data.length();
        int i=0;
        char ch;
        ch=data.charAt(i);
        add="";
        while(ch!='#'){
            add+=ch;
            i++;
            ch=data.charAt(i);
        }
        i++;
        ch=data.charAt(i);
        while(ch!='#'){
            name+=ch;
            i++;
            ch=data.charAt(i);
        }
        i++;
        ch=data.charAt(i);
        loc="";
        while(ch!='#'){
            loc+=ch;
            i++;
            ch=data.charAt(i);
        }
        // Log.d("Main Activity","Address -"+add);
        //  Log.d("Main Activity","Name -"+name);
        //  Log.d("Main Activity","LOCATION -"+loc);

        count=0;
        for(int j=0;j<Chats.userschatList.size();j++) {
            UsersChat uuc = Chats.userschatList.get(j);
            if(uuc.getIdenty().equals(add)) {
                String last_msg=uuc.getLast_message();
                Chats.userschatList.remove(j);
                UsersChat uc = new UsersChat(add, name, last_msg, loc);
                Chats.userschatList.add(uc);
                Chats.adapt.notifyDataSetChanged();
                break;
            }
            else
                count++;
        }
        if(count==Chats.userschatList.size()) {
            UsersChat uc = new UsersChat(add, name, "This Will Be The Last Message", loc);
            Chats.userschatList.add(uc);
            Chats.adapt.notifyDataSetChanged();
        }

    }

    public void setDeviceName(String devName) {
        try {
            Class[] paramTypes = new Class[3];
            paramTypes[0] = WifiP2pManager.Channel.class;
            paramTypes[1] = String.class;
            paramTypes[2] = WifiP2pManager.ActionListener.class;
            Method setDeviceName = MainActivity.mManager.getClass().getMethod(
                    "setDeviceName", paramTypes);
            setDeviceName.setAccessible(true);

            Object arglist[] = new Object[3];
            arglist[0] = MainActivity.mChannel;
            arglist[1] = devName;
            arglist[2] = new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    Log.d("MainActivity","setDeviceName succeeded");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d("StartActivity","setDeviceName failed");
                }
            };

            setDeviceName.invoke(MainActivity.mManager, arglist);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    public void update_others(String type,String msg){
        sqLiteHelper=new SQLiteHelper(this,MainActivity.add);
        sqlcursor=sqLiteHelper.getData();
        if (type.equals("STRING")) {
            sqLiteHelper.addData(msg, 0);
            do_notify(msg.substring(6));
        }
        else if(!type.equals("PROFIL") && !type.equals("CALLIN")) {
            sqLiteHelper.addData(msg, 2);
            do_notify("Image Received");
        }

    }
}