package com.example.samscots.sosoffine;




import android.os.Environment;
import android.os.Message;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Sam Scots on 12/17/2017.
 */

public class Handle_Client implements Runnable {

    Socket client;
    String my_address;
    DataInputStream ddis;
    DataOutputStream ddos;
    ObjectInputStream ois;
    int count;
    ObjectOutputStream oos;
    byte[] send_buffer ;
    int l;
    String temp,info,call_info="";
    String outputfile2 = Environment.getExternalStorageDirectory() + "/SOSOffline/callR.3gp";



    public Handle_Client(Socket client, ObjectInputStream ois,ObjectOutputStream oos,String my_address){
        this.client=client;
        this.ois=ois;
        this.my_address=my_address;
        this.oos=oos;
    }

    @Override
    public void run() {
        try {
            ddis=new DataInputStream(this.client.getInputStream());
            ddos=new DataOutputStream(this.client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true){
            if(client.isClosed())
                break;
            try {

                try {

                    Log.d("Server Thread", "Trying to Read------");
                    l=ddis.readInt();
                    Log.d("Server Thread", "The Val of L-"+l);
                    if(l==100){
                        count=0;
                        byte[] image_buffer = (byte[]) ois.readObject();
                        ByteArrayInputStream bai = new ByteArrayInputStream(image_buffer);
                        int ch;
                        while((ch = bai.read()) != -1)
                            count++;
                        temp=new String(image_buffer, 0,count);
                        Log.d("Server Thread", "The String Was -" + temp);
                    }else if(l==101) {
                        byte[] image_buffer = (byte[]) ois.readObject();
                        File folder = new File(Environment.getExternalStorageDirectory() + "/SOSOffline");
                        boolean success = true;
                        if (!folder.exists()) {
                            success = folder.mkdirs();
                        }
                        if (success) {
                            temp=Environment.getExternalStorageDirectory() + "/SOSOffline/" + System.currentTimeMillis() + ".jpg";
                            FileOutputStream fos = new FileOutputStream(temp);
                            fos.write(image_buffer);
                            Log.d("Server Thread", "Image Received");
                        }

                    }else if(l==110){

                       //Demo Purpose Check
                                count=0;
                        byte[] image_buffer = (byte[]) ois.readObject();
                        ByteArrayInputStream bai = new ByteArrayInputStream(image_buffer);
                        int ch;
                        while((ch = bai.read()) != -1)
                            count++;
                        info=new String(image_buffer, 0,count);
                        Log.d("Server Thread", "The String Was -" + info);

                        image_buffer = (byte[]) ois.readObject();
                        File folder = new File(Environment.getExternalStorageDirectory() + "/SOSOffline/profile");
                        Log.d("Server Thread", "The Path 1 " + Environment.getExternalStorageDirectory() + "/SOSOffline/profile");
                        boolean success = true;
                        if (!folder.exists()) {
                            Log.d("Server Thread", "Folder Dosent Exists");
                            success = folder.mkdirs();
                            Log.d("Server Thread", "Folder Made");
                        }
                        if (success) {
                            Log.d("Server Thread", "Folder Made Successful");
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
                    }
                    else if(l==115){
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
                    }else if(l==116){
                            byte[] voice_buffer = (byte[]) ois.readObject();
                       // Log.d("Handle_Client","Voice "+voice_buffer.toString());
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

                Message message=Message.obtain();
                message.obj=temp;
                MainActivity.server_handler.sendMessage(message);
                Log.d("Handle_Client","Current State of ChatActive ="+ChatActivity.ChatActive);
                if(ChatActivity.ChatActive==1) {
                    Message message1=Message.obtain();
                    message1.obj=temp;
                    ChatActivity.Chat_Handler.sendMessage(message1);
                }
                if(Call.CallActive==1) {
                        Message msg_call = Message.obtain();
                        msg_call.obj = call_info;
                        Call.Call_handler.sendMessage(msg_call);
                        Log.d("ServerCall", "The Msg was " + call_info);
                }


            } catch (IOException e) {
                e.printStackTrace();
                try {
                    client.close();
                    break;
                } catch (IOException e1) {
                    e1.printStackTrace();
                    try {
                        client.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    break;
                }
            }
        }

    }
    public void send_to_this(String str){

        str="STRING"+MainActivity.getMacAddr()+"#"+str;
        send_buffer=new byte[str.length()];
        send_buffer = str.getBytes();

        try {
            ddos.writeInt(100);
            Log.d("Server THREA DataType", "Data Type- "+100);
            this.oos.writeObject(send_buffer);
            Log.d("Server THREAD", "Client - " + str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void send_image(byte[] by){
        try {
            ddos.writeInt(101);
            Log.d("Server THREA DataType", "Data Type- "+101);
            this.oos.writeObject(by);
            this.oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("Server Thread","Image Send");
    }


    public void send_profile_image(){
        try {
            Log.d("Server THREA DataType", "Before- "+MainActivity.getMacAddr());
            String make_type=MainActivity.getMacAddr()+"#"+MainActivity.DisplayName+"#";
            ddos.writeInt(110);
            send_buffer=new byte[make_type.length()];
            send_buffer = make_type.getBytes();
            this.oos.writeObject(send_buffer);
            oos.flush();
            Log.d("Server THREA DataType", "Data Type- "+110);
            Log.d("Server THREA DataType", "String- "+make_type);
            //if(MainActivity.file!=null)
            try {
                FileInputStream fis=new FileInputStream(MainActivity.realpath);
                byte[] buffer=new byte[fis.available()];
                fis.read(buffer);
                this.oos.writeObject(buffer);
            }catch (Exception e){
                this.oos.writeObject(MainActivity.if_error);
            }
            this.oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("Server Thread","Profile Image Send");
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
            Log.d("Server THREA DataType", "Data Type- "+117);
            this.oos.writeObject(send_buffer);
            Log.d("Server THREAD", "Sending Data - "+emer);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Server THREAD", "Error Sending Data ");
        }
    }

}
