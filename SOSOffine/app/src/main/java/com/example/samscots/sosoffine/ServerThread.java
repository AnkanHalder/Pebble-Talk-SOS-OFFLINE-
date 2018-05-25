package com.example.samscots.sosoffine;


import android.util.Log;

import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;


/**
 * Created by Sam Scots on 7/12/2017.
 */

public class ServerThread implements Runnable {


    public int mPort;
    public static ServerSocket sockett=null;
    public static Vector<Handle_Client> arr_client_handler = new Vector<>();


    public ServerThread(int port) throws IOException {
        mPort = port;
    }

    @Override
    public void run() {

        try {
            Log.d("SERVER THREAD", "Creating Server Socket");
            sockett=new ServerSocket();
            sockett.setReuseAddress(true);
            sockett.bind(new InetSocketAddress(mPort));
            Log.d("SERVER THREAD", "Created Socket");
            Socket client = sockett.accept();
            Log.d("SERVER THREAD", "Client Accepted");

            ObjectInputStream dis = new ObjectInputStream(client.getInputStream());
            ObjectOutputStream dos = new ObjectOutputStream(client.getOutputStream());



            Handle_Client hc=new Handle_Client(client,dis,dos,client.getRemoteSocketAddress().toString());
            Thread th=new Thread(hc);
            arr_client_handler.add(hc);
            th.start();
            Log.d("SERVER THREAD", "Created ");


        } catch (IOException e) {
            e.printStackTrace();
            if (sockett != null && !sockett.isClosed()) {
                try {
                    sockett.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            Log.d("SERVER THREAD", "Socket Closed");
        }


    }
    public void send(String sendData){
        for(Handle_Client hhc :arr_client_handler) {
            hhc.send_to_this(sendData);
        }
    }
    public void send_image(byte[] buffer){
        for(Handle_Client hhc :arr_client_handler) {
            hhc.send_image(buffer);
        }
    }
    public void send_profile_image(){
        for(Handle_Client hhc :arr_client_handler) {
            hhc.send_profile_image();
        }
    }
    public void dis_me(){
        for(Handle_Client hhc :arr_client_handler) {
            hhc.you_DIS();
        }
    }
    public void call_me(){
        for(Handle_Client hhc :arr_client_handler) {
            hhc.you_call();
        }
    }

    public void acceptedd(){
        for(Handle_Client hhc :arr_client_handler) {
            hhc.accepted();
        }
    }
    public void voice(byte[] by){
        for(Handle_Client hhc :arr_client_handler) {
            hhc.send_voice(by);
    }
    }

    public void emer(String msg) {
        for (Handle_Client hhc : arr_client_handler) {
            hhc.send_emerge(msg);
        }
    }

}