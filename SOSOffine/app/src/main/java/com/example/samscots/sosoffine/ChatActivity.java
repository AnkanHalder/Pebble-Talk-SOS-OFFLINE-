package com.example.samscots.sosoffine;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;


public class ChatActivity extends AppCompatActivity {

    private static RecyclerView recyclerView;
    private static RecyclerView.Adapter adapter;
    private static List<ListChat> listChatsItems;
    public EditText get_the_message;
    private Button send_the_message;
    public InetAddress hoastAddress;
    public IntentFilter ChatIntent;
    public BroadcastReceiver ChatReceiver;
    public WifiP2pManager ChatManager;
    public WifiP2pManager.Channel ChatChannel;
    public static int ChatActive=0;
    public static Handler Chat_Handler;
    public SQLiteHelper sqLiteHelper;
    String sql_msg;
    int sql_from;
    ListChat listCha;
    public ImageView send_image;
    private static final int Gallery_Pick=10;
    Cursor sqlcursor=null;
    Cursor sqldb_cursor=null;
    Bitmap bitmap;
    public static String image_path;
    String addres,load_image_path,ImageDecode;
    String name;
    public static byte[] arrimg;
    boolean is_present=false;
    public static Context chatActivitycpntext;
    private static final int CAMERA_REQUEST = 357;
    //Notification
    private Notification.Builder main_notification_builder;
    private NotificationManager main_notification_manager;
    //Notification








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        send_image=(ImageView)findViewById(R.id.send_image);
        chatActivitycpntext=ChatActivity.this;
        main_notification_manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        image_path=preferences.getString("RealPath","");

        name=getIntent().getStringExtra("DisplayName");
        if(name!=null)
            setTitle(name);
        /*addres=getIntent().getStringExtra("DisplayADDRESS");
        if(addres!=null){
            addres=addres.substring(3);
            addres="`"+addres+"`";
        }
        else*/
        addres=getIntent().getStringExtra("DisplayUCName");


        Log.d("ChatActivity TYPE- ","The Table Name "+addres);


        load_image_path=getIntent().getStringExtra("DisplayUID");
       // if(MainActivity.loc.equals("")||MainActivity.loc==null)
          //  MainActivity.loc=load_image_path;
        //----------------------------------------------------------------------------------------------------------------------------
        //SQL
        try {
            sqLiteHelper = new SQLiteHelper(this, addres);
            sqLiteHelper.getWritableDatabase();
            sqlcursor = sqLiteHelper.getData();
        }catch (Exception e){
            Log.d("ChatActivityTYPE",e.getMessage());
        }
        //SQL

        //Recycler View
        recyclerView=(RecyclerView)findViewById(R.id.Chat_Activity_RecyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listChatsItems=new ArrayList<>();
        //Linking Xml with Java
        get_the_message=(EditText)findViewById(R.id.ChatActivity_editText);
        send_the_message=(Button)findViewById(R.id.ChatActivityButton);

        //Intents
        ChatManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        ChatChannel = ChatManager.initialize(this, getMainLooper(), null);

        ChatIntent = new IntentFilter();
        ChatIntent.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        ChatReceiver = new ChatBroadcast(ChatManager, ChatChannel, ChatActivity.this);
        //Intents




        send_the_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String the_message;
                the_message=get_the_message.getText().toString();
                if(ChatBroadcast.Info.isConnected()) {
                    if (!the_message.equals("")) {
                        Thread t = new Thread(new Runnable() {
                            public void run() {
                                if(WiFiDirectBroadcastReceiver.check_hoast==1){
                                    WiFiDirectBroadcastReceiver.serverThread.send(the_message);
                                    listCha = new ListChat(the_message, 1,load_image_path);
                                    listChatsItems.add(listCha);
                                    sqlcursor.moveToLast();
                                    addData("STRING"+the_message,1);
                                }else{
                                    WiFiDirectBroadcastReceiver.clientThread.send(the_message);
                                    listCha = new ListChat(the_message, 1,load_image_path);
                                    listChatsItems.add(listCha);
                                    sqlcursor.moveToLast();
                                    addData("STRING"+the_message,1);
                                }
                            }
                        });

                        t.start();

                        get_the_message.setText("");
                        adapter.notifyDataSetChanged();
                        recyclerView.getLayoutManager().scrollToPosition(adapter.getItemCount()-1);
                    }
                }else
                    Toast.makeText(ChatActivity.this, "You are Not Connected", Toast.LENGTH_SHORT).show();
            }
        });

        send_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ChatBroadcast.Info.isConnected()) {
                    CharSequence option[] = new CharSequence[]{"Camera", "Gallery"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setTitle("Choose Option");
                    builder.setItems(option, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, CAMERA_REQUEST);

                            }
                            if (which == 1) {

                                Intent Gallery_Intent = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(Intent.createChooser(Gallery_Intent,"Select Image"),Gallery_Pick);

                            }
                        }
                    });
                    builder.show();
                }
            }
        });

        adapter=new ListChatAdapter(listChatsItems,ChatActivity.this);
        recyclerView.setAdapter(adapter);


        Chat_Handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {


                String type = ((String) msg.obj).substring(0, Math.min(((String) msg.obj).length(), 6));
                Log.d("ChatActivity TYPE- ", type);
           //     if(addres.equals(MainActivity.add)){
                if (type.equals("STRING")) {
                    check_and_update(((String) msg.obj).substring(6));
                } else if (type.equals("PROFIL")) {
                    //  byte[] a=("PROFILImage Bytes").getBytes();
                   // if (WiFiDirectBroadcastReceiver.check_hoast == 1)
                   //     WiFiDirectBroadcastReceiver.serverThread.send_profile_image();
                   // add_data(((String) msg.obj).substring(6));

                } else if (type.equals("CALLIN")) {
                    Intent go_call = new Intent(ChatActivity.this, Call.class);
                    go_call.putExtra("Type_Call", "RCll");
                    go_call.putExtra("UID", addres);
                    go_call.putExtra("UserName", name);
                    go_call.putExtra("Frnd_Image", MainActivity.loc);
                    startActivity(go_call);
                    ChatActivity.this.finish();

                } else {
                    listCha = new ListChat(((String) msg.obj), 2,load_image_path);
                    listChatsItems.add(listCha);
                    adapter.notifyDataSetChanged();
                    recyclerView.getLayoutManager().scrollToPosition(adapter.getItemCount() - 1);
                    sqlcursor.moveToLast();
                    addData((String) msg.obj, 2);
                    Log.d("ChatActivity Message- ", (String) msg.obj);
                }
 //           }else{
          //          update_others(type,(String) msg.obj);
          //      }

               // Toast.makeText(ChatActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
            }
        };


        update_fromDB();

    }

    private void check_and_update(String st) {

        String stringadd="",strmsg;
        int l=st.length();
        int i=0;
        char ch=st.charAt(i);
        while(ch!='#'){
            stringadd+=ch;
            i++;
            ch=st.charAt(i);
        }
        i++;
        strmsg=st.substring(i,l);
        Log.d("ChatActivity Message- ", "Address"+stringadd);
        Log.d("ChatActivity Message- ", "Msg"+strmsg);

        if(addres.equals(stringadd)){
            Log.d("ChatActivity Message- ", "Equal And Adding");
            listCha = new ListChat(strmsg, 0, load_image_path);
            listChatsItems.add(listCha);
            adapter.notifyDataSetChanged();
            recyclerView.getLayoutManager().scrollToPosition(adapter.getItemCount() - 1);
            sqlcursor.moveToLast();
            //addData((String) msg.obj, 0);
           // Log.d("ChatActivity Message- ", ((String) msg.obj).substring(6));
        }else
            do_notify(strmsg);
        update_others("STRING","STRING"+strmsg,stringadd);

    }

    public void addData(String msag,int frmm){
        boolean insertdata=sqLiteHelper.addData(msag,frmm);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();

        if(id==R.id.call_menu){
            if(ChatBroadcast.Info.isConnected()) {
                Toast.makeText(this, "Calling", Toast.LENGTH_SHORT).show();
                if(WiFiDirectBroadcastReceiver.check_hoast==1){
                    WiFiDirectBroadcastReceiver.serverThread.call_me();
                }else
                    WiFiDirectBroadcastReceiver.clientThread.you_call();

                Intent go_call = new Intent(ChatActivity.this, Call.class);
                go_call.putExtra("Type_Call", "YCll");
                go_call.putExtra("Frnd_Image", MainActivity.loc);
                go_call.putExtra("UID",addres);
                go_call.putExtra("UserName",name);
                startActivity(go_call);
                ChatActivity.this.finish();

            }else
                Toast.makeText(chatActivitycpntext, "You are Not Connected", Toast.LENGTH_SHORT).show();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {

        ChatActive=0;
        if(listChatsItems.size()>0) {
            for (int i = 0; i < Chats.userschatList.size(); i++) {
                UsersChat uc = Chats.userschatList.get(i);
                if (uc.getIdenty().equals(addres)) {
                    String id, display_name, profile_uri;
                    id = uc.getIdenty();
                    display_name = uc.getDisplay_name();
                    profile_uri = uc.getProfile_uri();

                    ListChat lc = listChatsItems.get(listChatsItems.size() - 1);
                    if (lc.getMine() == 2 || lc.getMine() == 3) {
                        Chats.userschatList.remove(i);
                        if (lc.getMine() == 3) {
                            UsersChat uco = new UsersChat(id, display_name, "You: Image", profile_uri);
                            Chats.userschatList.add(uco);
                        } else {
                            UsersChat uco = new UsersChat(id, display_name, display_name + ": Image", profile_uri);
                            Chats.userschatList.add(uco);
                        }

                    } else {
                        Chats.userschatList.remove(i);
                        if (lc.getMine() == 1) {
                            UsersChat uco = new UsersChat(id, display_name, "You: " + lc.getMesage(), profile_uri);
                            Chats.userschatList.add(uco);
                        } else {
                            UsersChat uco = new UsersChat(id, display_name, display_name + ": " + lc.getMesage(), profile_uri);
                            Chats.userschatList.add(uco);
                        }

                    }
                    Chats.adapt.notifyDataSetChanged();
                }

            }
        }

        super.onStop();
        }

    @Override
    protected void onResume() {
        super.onResume();


        registerReceiver(ChatReceiver, ChatIntent);
        hoastAddress=WiFiDirectBroadcastReceiver.groupOwnerAddress;
        if(hoastAddress!=null){

            Toast.makeText(this, "Chat Activity "+hoastAddress, Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onStart() {
        ChatActive=1;
        super.onStart();
    }

    @Override
    protected void onPause() {

        unregisterReceiver(ChatReceiver);
        super.onPause();

    }



    void update_fromDB(){
       Thread t = new Thread(new Runnable() {
            public void run() {
                sqLiteHelper=new SQLiteHelper(ChatActivity.this,addres);
                sqLiteHelper.getWritableDatabase();

                //Sql
                //if(sqlcursor!=null){
                    sqlcursor=sqLiteHelper.getData();
                    try {
                        while (sqlcursor.moveToNext()) {
                            sql_msg = sqlcursor.getString(1);
                            sql_from = sqlcursor.getInt(2);
                            String type = sql_msg.substring(0, Math.min((sql_msg).length(), 6));
                            if (!type.equals(""))
                            {
                                if (type.equals("STRING")) {
                                    if (sql_from == 0) {
                                        listCha = new ListChat(sql_msg.substring(6), 0,load_image_path);
                                        listChatsItems.add(listCha);
                                    } else {
                                        listCha = new ListChat(sql_msg.substring(6), 1,load_image_path);
                                        listChatsItems.add(listCha);
                                    }
                                } else {
                                    if (sql_from == 2) {
                                        listCha = new ListChat(sql_msg, 2,load_image_path);
                                        listChatsItems.add(listCha);
                                    } else {
                                        listCha = new ListChat(sql_msg, 3,load_image_path);
                                        listChatsItems.add(listCha);
                                    }
                                }
                        }

                            adapter.notifyDataSetChanged();
                            recyclerView.getLayoutManager().scrollToPosition(adapter.getItemCount() - 1);
                        }
                    }catch (IllegalStateException e){
                      e.printStackTrace();
                    }

            //    }
                //SQl
           }
        });

        t.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == Activity.RESULT_OK && data!=null) {

            Uri URI = data.getData();
            String[] FILE = { MediaStore.Images.Media.DATA };


            Cursor cursor = getContentResolver().query(URI,
                    FILE, null, null, null);

            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(FILE[0]);
            ImageDecode = cursor.getString(columnIndex);
            cursor.close();

            try {

                File compressedImageFile = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(40)
                        .compressToFile(new File(ImageDecode));

                FileInputStream fis = new FileInputStream(compressedImageFile);
                final byte[] buffer = new byte[fis.available()];
                fis.read(buffer);

                File folder = new File(Environment.getExternalStorageDirectory() + "/SOSOffline");
                boolean success = true;
                if (!folder.exists())
                    success = folder.mkdirs();
                if(success){
                    ImageDecode = Environment.getExternalStorageDirectory() + "/SOSOffline/" + System.currentTimeMillis() + ".jpg";
                    FileOutputStream fos = new FileOutputStream(ImageDecode);
                    fos.write(buffer);
                }

                if (ChatBroadcast.Info.isConnected()) {
                    send_imagee(buffer);
                    listCha = new ListChat(ImageDecode, 3, MainActivity.loc);
                    listChatsItems.add(listCha);
                    adapter.notifyDataSetChanged();
                    recyclerView.getLayoutManager().scrollToPosition(adapter.getItemCount() - 1);
                    sqlcursor.moveToLast();
                    addData(ImageDecode, 3);

                }else
                    Toast.makeText(chatActivitycpntext, "You are Not Connected", Toast.LENGTH_SHORT).show();
            }catch (FileNotFoundException e){
                Toast.makeText(this, "File Not Found ", Toast.LENGTH_SHORT).show();
            }catch (IOException e){
                Toast.makeText(this, "Error "+e, Toast.LENGTH_SHORT).show();
            }

        }
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            try {
                cap_and_save(photo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void cap_and_save(Bitmap bm)throws IOException{

        File folder = new File(Environment.getExternalStorageDirectory() + "/SOSOffline");
        boolean success = true;
        if (!folder.exists())
            success = folder.mkdirs();
        if(success){
            String f_name=System.currentTimeMillis() + ".jpg";
            File f  = new File(Environment.getExternalStorageDirectory() + "/SOSOffline/",f_name);
            String uri_pos=Environment.getExternalStorageDirectory() + "/SOSOffline/"+f_name;

            //Convert bitmap to byte array
            OutputStream os;
            os = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();

            File compressedImageFile = new Compressor(this)
                    .setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(40)
                    .compressToFile(f);
            FileInputStream fis = new FileInputStream(compressedImageFile);
            final byte[] buffer = new byte[fis.available()];
            fis.read(buffer);

            if (ChatBroadcast.Info.isConnected()) {
                send_imagee(buffer);
                listCha = new ListChat(uri_pos, 3, MainActivity.loc);
                listChatsItems.add(listCha);
                adapter.notifyDataSetChanged();
                recyclerView.getLayoutManager().scrollToPosition(adapter.getItemCount() - 1);
                sqlcursor.moveToLast();
                addData(f_name, 3);

            }else
                Toast.makeText(chatActivitycpntext, "You are Not Connected", Toast.LENGTH_SHORT).show();
        }

    }




    void send_imagee(final byte[] buffer){

        Thread send=new Thread(new Runnable() {
            @Override
            public void run() {
                if (WiFiDirectBroadcastReceiver.check_hoast != 1)
                    WiFiDirectBroadcastReceiver.clientThread.send_image(buffer);
                    else
                        WiFiDirectBroadcastReceiver.serverThread.send_image(buffer);
            }
        });

        send.start();

    }

    public void update_others(String type,String msg,String address){
        sqLiteHelper=new SQLiteHelper(this,address);
        sqlcursor=sqLiteHelper.getData();
        if (type.equals("STRING")) {
            addData(msg, 0);
           // do_notify(msg.substring(6));
        }
        else if(!type.equals("PROFIL") && !type.equals("CALLIN")) {
            addData(msg, 2);
            //do_notify("Image Received");
        }

    }
    public void do_notify(String mmmess){

        if(!mmmess.equals("")) {

            //Intent notification_main = new Intent(this, ChatActivity.class);
           // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notification_main, 0);
            Notification main_noti = new Notification.Builder(ChatActivity.this)
                    .setTicker("Pebble Talk").setContentTitle("New Message")
                    .setContentText(mmmess)
                    .setSmallIcon(R.drawable.notificationicn)
                    .setAutoCancel(true)
                    //.setContentIntent(pendingIntent)
                    .getNotification();
            main_noti.flags |= Notification.FLAG_AUTO_CANCEL;
            main_notification_manager.notify(0, main_noti);
        }
    }

}
