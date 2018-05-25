package com.example.samscots.sosoffine;

/**
 * Created by Sam Scots on 12/30/2017.
 */

public class UsersChat {

    String identy;
    String display_name;
    String last_message;
    String profile_uri;

    public UsersChat(String identy,String display_name, String last_message, String profile_uri) {
        this.identy=identy;
        this.display_name = display_name;
        this.last_message = last_message;
        this.profile_uri = profile_uri;
    }

    public String getIdenty() {
        return identy;
    }

    public void setIdenty(String id) {
        this.identy = identy;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public String getProfile_uri() {
        return profile_uri;
    }

    public void setProfile_uri(String profile_uri) {
        this.profile_uri = profile_uri;
    }
}
