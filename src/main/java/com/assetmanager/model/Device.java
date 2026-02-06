package com.assetmanager.model;

import java.time.LocalDate;

import com.assetmanager.exception.InvalidDeviceStateException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME, 
  include = JsonTypeInfo.As.PROPERTY, 
  property = "type" // This is the key we will use in Postman
)
@JsonSubTypes({
  @JsonSubTypes.Type(value = MobilePhone.class, name = "phone"),
  @JsonSubTypes.Type(value = Laptop.class, name = "laptop")
})
public abstract class Device {
    private String deviceId; // Can be UUID or any unique identifier
    private String brand;
    private String model;
    private String operatingSystem;
    private DeviceStatus status;
    private String maintenanceReason;
    private LocalDate decommissionDate;

    private Device() {}

    @JsonCreator
    public Device(
        @JsonProperty("deviceId") String deviceId, 
        @JsonProperty("brand") String brand, 
        @JsonProperty("model") String model, 
        @JsonProperty("operatingSystem") String operatingSystem
    ){
        if (deviceId == null || deviceId.isBlank()) {
        throw new IllegalArgumentException("Device ID is mandatory and cannot be empty.");
        }
        this.deviceId = deviceId;
        this.brand = brand;
        this.model = model;
        this.operatingSystem = operatingSystem;
        this.status = DeviceStatus.AVAILABLE;
    }

    //Getters
    public String getDeviceId() {
        return deviceId;
    }
    public String getBrand() {
        return brand;
    }
    public String getModel() {
        return model;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }
    public DeviceStatus getStatus() {
        return status;
    }

    public void setStatus(DeviceStatus status) {
        this.status = status;
    }
    
    public LocalDate getDecommissionDate() {
        return decommissionDate;
    }

    public void rent() {
        ensuredNotDecommissioned();
        if (this.status != DeviceStatus.AVAILABLE) {
            throw new InvalidDeviceStateException(this.deviceId, "rent", this.status.name());
        } else {
            this.status = DeviceStatus.IN_USE;
        }
    }

    public String getMaintenanceReason() {
        return maintenanceReason;
    }

    public void returnToInventory() {
        ensuredNotDecommissioned();
        if (this.status != DeviceStatus.IN_USE) {
            throw new InvalidDeviceStateException(this.deviceId, "return", this.status.name());
        } else {
            this.status = DeviceStatus.AVAILABLE;
        }
    }

    public void sendToMaintenance(String reason) {
        ensuredNotDecommissioned();
        if (this.status == DeviceStatus.UNDER_REPAIR) {
            throw new InvalidDeviceStateException(this.deviceId, "send to maintenance", this.status.name());
        } 
        this.status = DeviceStatus.UNDER_REPAIR;
        this.maintenanceReason = reason;
    }

    public void repairCompleted() {
        ensuredNotDecommissioned();
        if (this.status != DeviceStatus.UNDER_REPAIR) {
            throw new InvalidDeviceStateException(this.deviceId, "complete repair", this.status.name());
        }
        this.status = DeviceStatus.AVAILABLE;
        this.maintenanceReason = null;
    }

    public void decommission() {
        ensuredNotDecommissioned();
        if (this.status == DeviceStatus.IN_USE) {
            throw new InvalidDeviceStateException(this.deviceId, "decommission", this.status.name());
        }
        this.status = DeviceStatus.DECOMMISSIONED;
        this.decommissionDate = LocalDate.now();
    }

    private void ensuredNotDecommissioned() {
        if (this.status == DeviceStatus.DECOMMISSIONED) {
            throw new InvalidDeviceStateException(this.deviceId, "operate on", this.status.name());
        }
    }

    @Override
    public String toString() {
        return String.format("[ID: %s | Brand: %s | Model: %s | Status: %s]", deviceId, brand, model, status);
    }
}
