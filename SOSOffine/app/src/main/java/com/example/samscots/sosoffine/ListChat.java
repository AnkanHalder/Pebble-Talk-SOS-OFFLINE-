package com.example.samscots.sosoffine;



/**
 * Created by Sam Scots on 12/5/2017.
 */

public class ListChat {



    String user_image;
    String mesage;
    int mine;




    public ListChat(String mesage, int mine,String user_image) {
        this.mesage = mesage;
        this.mine=mine;
        this.user_image=user_image;
    }


    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getMesage() {
        return mesage;
    }

    public void setMesage(String mesage) {
        this.mesage = mesage;
    }

    public int getMine() {
        return mine;
    }

    public void setMine(int mine) {
        this.mine = mine;
    }

}
