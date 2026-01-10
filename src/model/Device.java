package model;

public abstract class Device {
    private final String deviceId; // Can be UUID or any unique identifier
    private final String brand;
    private final String model;
    private final String operatingSystem;
    private DeviceStatus status;

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
            throw new IllegalStateException("Device is not available for rental. Current status: " + status);
        } else {
            this.status = DeviceStatus.IN_USE;
        }
    }

    public void returnToInventory() {
        if (this.status != DeviceStatus.IN_USE) {
            throw new IllegalStateException("Device is not currently rented out. Current status: " + status);
        } else {
            this.status = DeviceStatus.AVAILABLE;
        }
    }

    @Override
    public String toString() {
        return String.format("[ID: %s | Brand: %s | Model: %s | Status: %s]", deviceId, brand, model, status);
    }
}
