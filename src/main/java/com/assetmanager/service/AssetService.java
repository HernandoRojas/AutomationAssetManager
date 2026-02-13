package com.assetmanager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.assetmanager.exception.DeviceNotFoundException;
import com.assetmanager.model.Device;
import com.assetmanager.model.DeviceStatus;
import com.assetmanager.repository.DeviceRepository;

@Service
public class AssetService {
    // We depend on the Interface, not the implementation!
    private final DeviceRepository repository;

    // Constructor Injection: This makes the service easy to test
    public AssetService(DeviceRepository repository) {
        this.repository = repository;
    }

    private Optional<Device> findDeviceById(String deviceId) {
        return repository.findById(deviceId);
    }

    public void registerNewDevice(Device device) {
        // Business Rule: IDs must be unique (simplified check)
        if (repository.existsById(device.getDeviceId())) {
            throw new IllegalArgumentException("Device ID already exists: " + device.getDeviceId());
        }
        repository.save(device);
    }

    public Device getCreatedDevice(String deviceId) {
        return findDeviceById(deviceId)
                .orElseThrow(() -> new DeviceNotFoundException(deviceId));
    }

    public List<Device> findByStatusAndBrand(DeviceStatus status, String brand) {
        if (status != null && (brand != null && !brand.isBlank())) {
            return repository.findByStatusAndBrandIgnoreCase(status, brand);
        } else if (status != null) {
            return repository.findByStatus(status);
        } else if (brand != null && !brand.isBlank()) {
            return repository.findByBrandIgnoreCase(brand);
        }
        return repository.findAll();
    }

    public List<Device> getAllDevices() {
        return repository.findAll();
    }

    public List<Device> getAllAvailableDevices() {
        return repository.findByStatus(DeviceStatus.AVAILABLE);
    }

    public List<Device> getAllOnMaintenanceDevices() {
        return repository.findByStatus(DeviceStatus.UNDER_REPAIR);
    }

    public List<Device> getAllRentedDevices() {
        return repository.findByStatus(DeviceStatus.IN_USE);
    }

    public List<Device> getAllDecommissionedDevices() {
        return repository.findByStatus(DeviceStatus.DECOMMISSIONED);
    }

    public void rentDevice(String deviceId) {
        // 1. Find the device
        Device device = getCreatedDevice(deviceId);

        // 2. Business Logic: The "rent" method inside Device handles the status check
        // This is "Tell, Don't Ask" principle.
        device.rent();

        // 3. Persist the change
        repository.save(device);
        System.out.println("Device rented successfully: " + deviceId);
    }

    public void returnDevice(String deviceId) {
        Device device = getCreatedDevice(deviceId);

        device.returnToInventory();

        repository.save(device);
        System.out.println("Device returned successfully: " + deviceId);
    }

    public void moveDeviceToMaintenance(String deviceId, String reason) {
        Device device = getCreatedDevice(deviceId);

        device.sendToMaintenance(reason);

        repository.save(device);
        System.out.println("Device moved to maintenance: " + deviceId + " Reason: " + reason);
    }

    public void completeDeviceRepair(String deviceId) {
        Device device = getCreatedDevice(deviceId);

        device.repairCompleted();

        repository.save(device);
        System.out.println("Device repair completed: " + deviceId);
    }

    public void decommissionDevice(String deviceId) {
        Device device = getCreatedDevice(deviceId);

        device.decommission();

        repository.save(device);
        System.out.println("Device decommissioned: " + deviceId);
    }
}