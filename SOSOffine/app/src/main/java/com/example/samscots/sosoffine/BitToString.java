package com.example.samscots.sosoffine;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/**
 * Created by Sam Scots on 12/23/2017.
 */

public class BitToString implements Runnable {

    public Bitmap bitmap;
    public String encodedString;

    public BitToString(Bitmap bitmap,String encodedString){
        this.bitmap=bitmap;
        this.encodedString=encodedString;
    }

    @Override
    public void run() {
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch(Exception e) {
            e.getMessage();
        }
    }
    public Bitmap getBit(){
        if(bitmap!=null)
        return bitmap;
        else
            return null;
    }
}
