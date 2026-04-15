package com.assetmanager.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import com.assetmanager.model.Device;

public class BatchDeviceRequest {
    
    @NotEmpty(message = "Device list cannot be empty")
    @Valid  // This ensures validation of nested Device objects
    private List<Device> devices;

    public BatchDeviceRequest() {}

    public BatchDeviceRequest(List<Device> devices) {
        this.devices = devices;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }
}