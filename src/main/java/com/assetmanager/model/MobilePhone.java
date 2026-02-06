package com.assetmanager.model;

public class MobilePhone extends Device {
    private String phoneNumber;

    public MobilePhone(String deviceId, String brand, String model, String operatingSystem, String phoneNumber) {
        super(deviceId, brand, model, operatingSystem);
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
