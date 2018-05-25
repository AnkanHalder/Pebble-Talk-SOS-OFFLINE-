package com.example.samscots.sosoffine;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


import de.hdodenhof.circleimageview.CircleImageView;


public class Call extends AppCompatActivity implements SensorEventListener{

    CircleImageView civ;
    Button call_call_left,call_end_mid,call_end_right;
    public static Handler Call_handler;
    public String locate,uuid,name;
    TextView uid,display;
    public static int CallActive;
    public static int conn;
    String LOG_TAG="CALL";
    String outputfile,outputfile2;
    File fp;
    SensorManager sm;
    Sensor proxysens;
    public static int accepted=0;
    private Context mContext;
    int present_bright;
    public static boolean ring;
    Vibrator v;










    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        mContext=getApplicationContext();
        conn=0;
        civ=(CircleImageView)findViewById(R.id.call_circle_image);
        call_call_left=(Button)findViewById(R.id.call_call_left);
        call_end_mid=(Button)findViewById(R.id.call_end_mid);
        call_end_right=(Button)findViewById(R.id.call_end_right);
        display=(TextView)findViewById(R.id.Call_display_name);
        uid=(TextView)findViewById(R.id.call_id);
        outputfile = Environment.getExternalStorageDirectory() + "/SOSOffline/callS.3gp";
        outputfile2 = Environment.getExternalStorageDirectory() + "/SOSOffline/callR.3gp";
        fp=new File(outputfile);


        sm=(SensorManager)getSystemService(SENSOR_SERVICE);
        proxysens=sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sm.registerListener(this,proxysens,SensorManager.SENSOR_DELAY_NORMAL);

        String type=getIntent().getStringExtra("Type_Call");
        uuid=getIntent().getStringExtra("UID");
        name=getIntent().getStringExtra("UserName");
        display.setText(name);
        uid.setText(uuid);

        //------------------------------------------------------------------------

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);



        //------------------------------------------------------------------------

        if(type.equals("YCll")){
            call_call_left.setVisibility(View.INVISIBLE);
            call_end_right.setVisibility(View.INVISIBLE);
        }
        if(type.equals("RCll")){
            call_end_mid.setVisibility(View.INVISIBLE);
            ring=true;
            call_me();
        }
        locate=getIntent().getStringExtra("Frnd_Image");
        Uri uriFromPath = Uri.fromFile(new File(locate));
        //civ.setImageURI(uriFromPath);
        Picasso.with(Call.this).load(uriFromPath)
                .error(R.drawable.profile)
                .fit()
                .into(civ);



        Call_handler =new Handler(){
            @Override
            public void handleMessage(Message msg) {

                Log.d("Call","The Msg was "+((String)msg.obj));
                if(((String)msg.obj).equals("YCll")){
                    call_call_left.setVisibility(View.INVISIBLE);
                    call_end_right.setVisibility(View.INVISIBLE);
                }else if(((String)msg.obj).equals("YDis")){
                    conn=0;
                    ring=false;
                    Toast.makeText(Call.this, "Disconnected", Toast.LENGTH_SHORT).show();
                    go_bck();

                }else if(((String)msg.obj).equals("RCll")){

                    call_end_mid.setVisibility(View.INVISIBLE);


                }else if(((String)msg.obj).equals("RDis")){
                    ring=false;
                    conn=0;
                    Toast.makeText(Call.this, "Disconnected", Toast.LENGTH_SHORT).show();
                    go_bck();
                }else if(((String)msg.obj).equals("ACCE")){
                    ring=false;
                    Toast.makeText(Call.this, "Connected", Toast.LENGTH_SHORT).show();
                    conn=1;
                  //  startMic();
                    if(WiFiDirectBroadcastReceiver.check_hoast==1)
                    {
                        UdpServer us=new UdpServer();
                        Thread th=new Thread(us);
                        th.start();
                    }else{
                        UdpClient uc=new UdpClient();
                        Thread th=new Thread(uc);
                        th.start();
                    }
                    accepted=1;

                }else if(((String)msg.obj).equals("Rvoi")){
                   // startPlaying();
                }
            }
        };



        call_call_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ring=false;
                if(WiFiDirectBroadcastReceiver.check_hoast==1)
                {
                    UdpServer us=new UdpServer();
                    Thread th=new Thread(us);
                    th.start();
                }

                Thread t=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(WiFiDirectBroadcastReceiver.check_hoast==1){
                            WiFiDirectBroadcastReceiver.serverThread.acceptedd();
                        }else
                            WiFiDirectBroadcastReceiver.clientThread.accepted();

                    }
                });
                t.start();
                conn=1;
                if(WiFiDirectBroadcastReceiver.check_hoast!=1)
                {

                    UdpClient uc=new UdpClient();
                    Thread th=new Thread(uc);
                    th.start();
                }
               // startMic();
                accepted=1;

                call_call_left.setVisibility(View.INVISIBLE);
                call_end_right.setVisibility(View.INVISIBLE);
                call_end_mid.setVisibility(View.VISIBLE);
            }
        });

        call_end_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ring=false;
                Thread t=new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if(WiFiDirectBroadcastReceiver.check_hoast==1){
                            WiFiDirectBroadcastReceiver.serverThread.dis_me();
                        }else
                            WiFiDirectBroadcastReceiver.clientThread.you_DIS();
                    }
                });
                t.start();
                conn=0;
                Toast.makeText(Call.this, "Call ended", Toast.LENGTH_SHORT).show();
                go_bck();
            }
        });
        call_end_mid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Thread t=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(WiFiDirectBroadcastReceiver.check_hoast==1)
                            WiFiDirectBroadcastReceiver.serverThread.dis_me();
                        else
                            WiFiDirectBroadcastReceiver.clientThread.you_DIS();
                    }
                });
                t.start();
                conn=0;
                Toast.makeText(Call.this, "Call ended", Toast.LENGTH_SHORT).show();
                go_bck();
            }
        });


    }
    public void go_bck(){
        Intent go_bkchat=new Intent(Call.this,ChatActivity.class);
        go_bkchat.putExtra("DisplayName",name);
        go_bkchat.putExtra("DisplayUCName",uuid);
        go_bkchat.putExtra("DisplayUID",locate);
        startActivity(go_bkchat);
        this.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        CallActive=1;
    }

    @Override
    protected void onStop() {
        super.onStop();
        CallActive=0;
    }



    @Override
    protected void onPause() {
        super.onPause();
        if(accepted==1) {
            if (WiFiDirectBroadcastReceiver.check_hoast == 1) {
                if (!UdpServer.udpSockett.isClosed())
                    UdpServer.udpSockett.close();
            } else {
                if (!UdpClient.udpSocket.isClosed())
                    UdpClient.udpSocket.close();
            }
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("Call ", String.valueOf(event.values[0]));
        if(event.values[0]==0.0) {
            call_end_mid.setEnabled(false);
            Log.d("Call Debug","Proximity Enabled");
        }else{
            call_end_mid.setEnabled(true);
            Log.d("Call Debug","Proximity Disabled");
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void call_me(){
        Thread m=new Thread(new Runnable() {
            @Override
            public void run() {
                final MediaPlayer mPlayer = MediaPlayer.create(getApplicationContext(),R.raw.calltone);
                mPlayer.start();
                while (ring){
                    v.vibrate(500);
                    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mPlayer.start();
                        }

                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mPlayer.stop();
                mPlayer.release();
            }
        });

        m.start();

    }


}
