package com.assetmanager.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "laptops")
public class Laptop extends Device {
    private int ramSizeGb;

    protected Laptop() { super(); }

    public Laptop(String deviceId, String brand, String model, String operatingSystem, int ramSizeGb) {
        super(deviceId, brand, model, operatingSystem);
        this.ramSizeGb = ramSizeGb;
    }

    public int getRamSizeGb() {
        return ramSizeGb;
    }
}