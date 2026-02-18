package com.assetmanager.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "laptops")
public class Laptop extends Device {
    private int ramSizeGb;

    protected Laptop() { super(); }

    public Laptop(String deviceId, String brand, String model, String operatingSystem, int ramSizeGb) {
        super(deviceId, brand, model, operatingSystem);
        this.ramSizeGb = ramSizeGb;
    }

    @Positive(message = "RAM size must be a positive integer.")
    public int getRamSizeGb() {
        return ramSizeGb;
    }
}