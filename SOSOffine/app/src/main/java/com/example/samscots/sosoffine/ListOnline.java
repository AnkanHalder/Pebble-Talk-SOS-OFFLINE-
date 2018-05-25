package com.example.samscots.sosoffine;

/**
 * Created by Sam Scots on 2/11/2017.
 */

public class ListOnline {
    String device_name;
    String device_address;
    boolean online;
    boolean wifi_online;
    String path_Uri;

    public ListOnline(String device_name, String device_address, boolean online, boolean wifi_online,String path_Uri) {
        this.device_name = device_name;
        this.device_address = device_address;
        this.online = online;
        this.wifi_online = wifi_online;
        this.path_Uri=path_Uri;
    }

    public String getPath_Uri() {
        return path_Uri;
    }

    public void setPath_Uri(String path_Uri) {
        this.path_Uri = path_Uri;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDevice_address() {
        return device_address;
    }

    public void setDevice_address(String device_address) {
        this.device_address = device_address;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isWifi_online() {
        return wifi_online;
    }

    public void setWifi_online(boolean wifi_online) {
        this.wifi_online = wifi_online;
    }
}
