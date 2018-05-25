package com.example.samscots.sosoffine;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;


/**
 * Created by Sam Scots on 7/12/2017.
 */

public class ClientThread implements Runnable {

    public InetAddress mHoastAddress;
    public int mPort;
    DataInputStream ddis;
    DataOutputStream ddos;
    ObjectOutputStream oos;
    public static Socket accept_server=null;
    int count,l;
    byte[] send_buffer;
    String info,temp,call_info="";
    ObjectInputStream ois;
    String outputfile2 = Environment.getExternalStorageDirectory() + "/SOSOffline/callR.3gp";



    public ClientThread(InetAddress hoastAddress, int port){
        mHoastAddress=hoastAddress;
        mPort=port;
    }

    @Override
    public void run() {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        accept_server=new Socket();
        Log.d("CLIENT THREAD", "Opening client socket - ");
        try {
                accept_server.bind(null);
                accept_server.connect((new InetSocketAddress(mHoastAddress.getHostAddress(), mPort)), 7000);
                Log.d("CLIENT THREAD", "Client socket - " + accept_server.isConnected());
                Log.d("CLIENT THREAD", "Socket is Connected to Server - " + accept_server.getRemoteSocketAddress());
                if(accept_server.isConnected()) {
                    ddis=new DataInputStream(accept_server.getInputStream());
                    ddos=new DataOutputStream(accept_server.getOutputStream());
                    oos=new ObjectOutputStream(accept_server.getOutputStream());
                    ois=new ObjectInputStream(accept_server.getInputStream());
                    send_profile_image();
                    Log.d("CLIENT THREAD", "Message Send ");
                }

        } catch (IOException e) {
            try {
                accept_server.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }

        while(true) {

            if(accept_server.isClosed())
                break;
                try {


                    try {
                        l=ddis.readInt();
                        if(l==100) {
                            count=0;
                            byte[] image_buffer = (byte[]) ois.readObject();
                            ByteArrayInputStream bai = new ByteArrayInputStream(image_buffer);
                            int ch;
                            while((ch = bai.read()) != -1)
                                count++;
                            temp=new String(image_buffer, 0,count);
                           // Log.d("Server Thread", "The String Was -" + temp);
                        }else if(l==101){
                            byte[] image_buffer = (byte[]) ois.readObject();
                            File folder = new File(Environment.getExternalStorageDirectory() + "/SOSOffline");
                            boolean success = true;
                            if (!folder.exists()) {
                                success = folder.mkdirs();
                            }
                            if(success) {
                                temp = Environment.getExternalStorageDirectory() + "/SOSOffline/" + System.currentTimeMillis() + ".jpg";
                                FileOutputStream fos = new FileOutputStream(temp);
                                fos.write(image_buffer);
                                Log.d("Server Thread", "Image Received");
                            }
                        }else if(l==110){

                            //Demo Purpose Check
                            count=0;
                            byte[] msg_buffer = (byte[]) ois.readObject();
                            ByteArrayInputStream bai = new ByteArrayInputStream(msg_buffer);
                            int ch;
                            while((ch = bai.read()) != -1)
                                count++;
                            info=new String(msg_buffer, 0,count);
                            Log.d("Client Thread", "The String Was -" + info);

                            byte[] image_buffer = (byte[]) ois.readObject();
                            File folder = new File(Environment.getExternalStorageDirectory() + "/SOSOffline/profile");
                            Log.d("Server Thread", "The Path 1 " + Environment.getExternalStorageDirectory() + "/SOSOffline/profile");
                            boolean success = true;
                            if (!folder.exists()) {
                                Log.d("Server Thread", "Folder Dosent Exists");
                                success = folder.mkdirs();

                            }
                            if (success) {
                                Log.d("Server Thread", "Folder Made");
                                File file = new File(Environment.getExternalStorageDirectory() + "/SOSOffline/profile/.nomedia");
                                if(!file.exists()) {
                                    FileOutputStream fos2 = new FileOutputStream(Environment.getExternalStorageDirectory() + "/SOSOffline/profile/.nomedia");
                                }


                                temp=Environment.getExternalStorageDirectory() + "/SOSOffline/profile/" + get_image(info) + ".jpg";
                                FileOutputStream fos = new FileOutputStream(temp,false);
                                fos.write(image_buffer);
                                temp="PROFIL"+info+temp+"#";
                                Log.d("Server Thread", "Profile Image Received "+temp);
                            }
                        } else if(l==115){

                            call_info="ACCE";

                        }
                        else if(l==111){
                            //You are calling
                            call_info="YCll";

                        }else if(l==112){
                            //Some one is calling
                            temp="CALLIN";

                        }else if(l==113){
                            //you disconnected
                            call_info="YDis";

                        }else if(l==114){
                            //receiver disconnected
                            call_info="RDis";
                        }
                        else if(l==116){
                            byte[] voice_buffer = (byte[]) ois.readObject();
                            //Log.d("Client","Voice "+voice_buffer.toString());
                           FileOutputStream fos = new FileOutputStream(outputfile2,false);
                            fos.write(voice_buffer);
                             call_info="Rvoi";
                        }
                        else if(l==117){
                            count=0;
                            byte[] image_buffer = (byte[]) ois.readObject();
                            ByteArrayInputStream bai = new ByteArrayInputStream(image_buffer);
                            int ch;
                            while((ch = bai.read()) != -1)
                                count++;
                            temp=new String(image_buffer, 0,count);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }


                   // Log.d("CLIENT THREAD", the_message);*/
                   Message message=Message.obtain();
                    message.obj=temp;
                    MainActivity.server_handler.sendMessage(message);
                    if(ChatActivity.ChatActive==1) {
                        Message message1=Message.obtain();
                        message1.obj=temp;
                        ChatActivity.Chat_Handler.sendMessage(message1);
                    }
                    if(Call.CallActive==1) {
                            Message msg_call = Message.obtain();
                            msg_call.obj = call_info;
                            Call.Call_handler.sendMessage(msg_call);
                            Log.d("ClientCall", "The Msg was " + call_info);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("CLIENT THREAD", "Error Reading Data ");
                    try {
                        accept_server.close();
                        Log.d("CLIENT THREAD", "SO Closing Socket ");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    break;

                }
            }

    }

    public void send(String sendData) {
        sendData="STRING"+MainActivity.getMacAddr()+"#"+sendData;
        send_buffer=new byte[sendData.length()];
        send_buffer = sendData.getBytes();
        try {
            ddos.writeInt(100);
            Log.d("CLIENT THREA DataType", "Data Type- "+100);
            this.oos.writeObject(send_buffer);
            Log.d("CLIENT THREAD", "Sending Data - "+sendData);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("CLIENT THREAD", "Error Sending Data ");
        }
    }

    public void send_image(byte[] by){
        try {
            ddos.writeInt(101);
            Log.d("CLIENT THREA DataType", "Data Type- "+101);
            oos.writeObject(by);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("Client Thread","Image Send");
    }
    public void send_profile_image(){
        try {
            Log.d("Client THREA DataType", "Before- "+MainActivity.getMacAddr());
            String make_type=MainActivity.getMacAddr()+"#"+MainActivity.DisplayName+"#";
            ddos.writeInt(110);
            send_buffer=new byte[make_type.length()];
            send_buffer = make_type.getBytes();
            oos.writeObject(send_buffer);
            oos.flush();
            Log.d("Client THREA DataType", "Data Type- "+110);
            Log.d("Client THREA DataType", "String- "+make_type);
            try {
                FileInputStream fis=new FileInputStream(MainActivity.realpath);
                byte[] buffer=new byte[fis.available()];
                fis.read(buffer);
                this.oos.writeObject(buffer);
            }catch (Exception e){
                this.oos.writeObject(MainActivity.if_error);
            }
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("Client Thread","Profile Image Send");
    }

    public void you_call(){
        try {
            ddos.writeInt(112);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void you_DIS(){
        try {
            ddos.writeInt(114);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void accepted(){
        try {
            ddos.writeInt(115);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public String get_image(String z){

        String s="";
        int l=z.length();
        char ch;
        for(int i=0;i<l;i++){
            ch=z.charAt(i);
            while(ch!='#'){
                if(ch!=':')
                    s+=ch;
                i++;
                ch=z.charAt(i);
            }

        }
        return s;
    }
    public  void send_voice(byte[] byff){

        try {
            ddos.writeInt(116);
            Log.d("Server THREA DataType", "Data Type- "+116);
            this.oos.writeObject(byff);
            this.oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void send_emerge(String emer){
        send_buffer=new byte[emer.length()];
        send_buffer = emer.getBytes();
        try {
            ddos.writeInt(117);
            Log.d("CLIENT THREA DataType", "Data Type- "+117);
            this.oos.writeObject(send_buffer);
            Log.d("CLIENT THREAD", "Sending Data - "+emer);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("CLIENT THREAD", "Error Sending Data ");
        }
    }



}