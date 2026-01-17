package model;

import exception.InvalidDeviceStateException;

public abstract class Device {
    private final String deviceId; // Can be UUID or any unique identifier
    private final String brand;
    private final String model;
    private final String operatingSystem;
    private DeviceStatus status;
    private String maintenanceReason;

    protected Device(String deviceId, String brand, String model, String operatingSystem) {
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
    

    public void rent() {
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
        if (this.status != DeviceStatus.IN_USE) {
            throw new InvalidDeviceStateException(this.deviceId, "return", this.status.name());
        } else {
            this.status = DeviceStatus.AVAILABLE;
        }
    }

    public void sendToMaintenance(String reason) {
        if (this.status == DeviceStatus.UNDER_REPAIR) {
            throw new InvalidDeviceStateException(this.deviceId, "send to maintenance", this.status.name());
        } 
        this.status = DeviceStatus.UNDER_REPAIR;
        this.maintenanceReason = reason;
    }

    public void repairCompleted() {
        if (this.status != DeviceStatus.UNDER_REPAIR) {
            throw new InvalidDeviceStateException(this.deviceId, "complete repair", this.status.name());
        }
        this.status = DeviceStatus.AVAILABLE;
        this.maintenanceReason = null;
    }

    @Override
    public String toString() {
        return String.format("[ID: %s | Brand: %s | Model: %s | Status: %s]", deviceId, brand, model, status);
    }
}
