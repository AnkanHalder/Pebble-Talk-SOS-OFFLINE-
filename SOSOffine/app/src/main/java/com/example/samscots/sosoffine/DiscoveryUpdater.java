package com.example.samscots.sosoffine;

import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import static com.example.samscots.sosoffine.MainActivity.mChannel;
import static com.example.samscots.sosoffine.MainActivity.mManager;
import static com.example.samscots.sosoffine.Online.peerListListener;

/**
 * Created by Sam Scots on 2/18/2018.
 */

public class DiscoveryUpdater implements Runnable {
    @Override
    public void run() {
        while(true) {
            mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    Log.d("DiscoveryUpdater", "Successful in adding Discovery Request");
                    mManager.requestPeers(mChannel,peerListListener);
                }

                @Override
                public void onFailure(int reasonCode) {
                    Log.d("DiscoveryUpdater", "Failed in adding Discovery Request "+reasonCode);
                }
            });
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
