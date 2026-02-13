package com.assetmanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "phones")
public class MobilePhone extends Device {
    private String phoneNumber;

    protected MobilePhone() { super(); }

    public MobilePhone(String deviceId, String brand, String model, String operatingSystem, String phoneNumber) {
        super(deviceId, brand, model, operatingSystem);
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
