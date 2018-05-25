package com.example.samscots.sosoffine;


import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static com.example.samscots.sosoffine.WiFiDirectBroadcastReceiver.port;

/**
 * Created by Sam Scots on 1/13/2018.
 */



public class UdpServer implements Runnable {

    private static final String LOG_TAG = "AudioCall";
    private static final int SAMPLE_RATE = 8000; // Hertz
    private static final int SAMPLE_INTERVAL = 20; // Milliseconds
    private static final int SAMPLE_SIZE = 2; // Bytes
    private static final int BUF_SIZE = SAMPLE_INTERVAL * SAMPLE_INTERVAL * SAMPLE_SIZE * 2; //Bytes
    public static DatagramSocket udpSockett = null;


    @Override
    public void run() {

        try {
            udpSockett = new DatagramSocket(port);
            udpSockett.setReuseAddress(true);
            InetAddress serverAddr;
            serverAddr = InetAddress.getByName(WiFiDirectBroadcastReceiver.s);
            byte[] message = new byte[8000];
            DatagramPacket packet = new DatagramPacket(message,message.length);
            Log.i("UDP client: ", "about to receive packet");
            udpSockett.receive(packet);
            String text = new String(message, 0, packet.getLength());
            Log.d("Received data", text);
            Log.d("Received data","My Address "+serverAddr.toString());
            Log.d("Received data","User Address "+packet.getAddress());

            AudioSendReceive asr=new AudioSendReceive(port,udpSockett,packet.getAddress());
            asr.record();
            asr.play();

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
      /*  Log.i(LOG_TAG, "Receive thread started. Thread id: " + Thread.currentThread().getId());
        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, BUF_SIZE, AudioTrack.MODE_STREAM);
        track.play();
        byte[] buf = new byte[BUF_SIZE];
        //record();
        while (Call.conn==1) {
            try {
                DatagramPacket packet = new DatagramPacket(buf,BUF_SIZE);
                Log.i("UDP client: ", "about to wait to receive");
                udpSockett.receive(packet);
                track.write(packet.getData(), 0, BUF_SIZE);
                Log.i(LOG_TAG, "Packet received: " + packet.getLength());
            }catch (IOException e) {
                Log.e("UDP client has", "error: "+ e);
               Call.conn=0;
            }
        }
        udpSockett.disconnect();
        udpSockett.close();
        track.stop();
        track.flush();
        track.release();
        Call.conn=0;*/
    }
    /*void record(){
        Thread thj=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress serverAddr;
                    serverAddr = InetAddress.getByName(WiFiDirectBroadcastReceiver.s);
                    AudioRecord audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                            AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT) * 10);
                    byte[] buf = new byte[BUF_SIZE];
                    //byte[] buf = ("The String to Send").getBytes();
                    int bytes_read = 0;
                    audioRecorder.startRecording();
                    while (Call.conn == 1) {
                        bytes_read = audioRecorder.read(buf, 0, BUF_SIZE);
                        DatagramPacket packet = new DatagramPacket(buf, bytes_read, serverAddr, port);
                        Log.i(LOG_TAG, "Packet Sent: " + packet.getLength());
                        udpSockett.send(packet);
                        Thread.sleep(SAMPLE_INTERVAL, 0);
                    }
                    audioRecorder.stop();
                    audioRecorder.release();
                    udpSockett.disconnect();
                    udpSockett.close();
                    Call.conn = 0;
                }catch (SocketException e){
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thj.start();
    }*/

}
