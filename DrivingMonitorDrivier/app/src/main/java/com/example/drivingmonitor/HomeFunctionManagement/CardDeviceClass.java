package com.example.drivingmonitor.HomeFunctionManagement;

public class CardDeviceClass {
    private String name;
    private String address;
    private String connectionState;

    public CardDeviceClass(String name, String address, String connectionState) {
        this.name = name;
        this.address = address;
        this.connectionState = connectionState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(String connectionState) {
        this.connectionState = connectionState;
    }
}
