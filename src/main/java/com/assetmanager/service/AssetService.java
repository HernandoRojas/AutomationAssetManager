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
        if (findDeviceById(device.getDeviceId()).isPresent()) {
            throw new IllegalArgumentException("Device ID already exists: " + device.getDeviceId());
        }
        repository.save(device);
    }

    public Device getCreatedDevice(String deviceId) {
        return findDeviceById(deviceId)
                .orElseThrow(() -> new DeviceNotFoundException(deviceId));
    }

    public void rentDevice(String deviceId) {
        // 1. Find the device
        Device device = findDeviceById(deviceId)
                .orElseThrow(() -> new DeviceNotFoundException(deviceId));

        // 2. Business Logic: The "rent" method inside Device handles the status check
        // This is "Tell, Don't Ask" principle.
        device.rent();

        // 3. Persist the change
        repository.save(device);
        System.out.println("Device rented successfully: " + deviceId);
    }

    public List<Device> getAllDevices() {
        return repository.findAll();
    }

    public List<Device> findByStatusAndBrand(DeviceStatus status, String brand) {
        if (status == null && (brand == null || brand.isBlank())) {
            return repository.findAll();
        } else if (status != null && (brand == null || brand.isBlank())) {
            return repository.findAll().stream()
                    .filter(d -> d.getStatus() == status)
                    .toList();
        } else if (status == null) {
            return repository.findAll().stream()
                    .filter(d -> d.getBrand().equalsIgnoreCase(brand))
                    .toList();
        } else {
            return repository.findAll().stream()
                    .filter(d -> d.getStatus() == status && d.getBrand().equalsIgnoreCase(brand))
                    .toList();
        }
    }

    public List<Device> getAllAvailableDevices() {
        return repository.findAll().stream()
                .filter(d -> d.getStatus() == DeviceStatus.AVAILABLE)
                .toList();
    }

    public List<Device> getAllOnMaintenanceDevices() {
        return repository.findAll().stream()
                .filter(d -> d.getStatus() == DeviceStatus.UNDER_REPAIR)
                .toList();
    }

    public List<Device> getAllRentedDevices() {
        return repository.findAll().stream()
                .filter(d -> d.getStatus() == DeviceStatus.IN_USE)
                .toList();
    }

    public List<Device> getAllDecommissionedDevices() {
        return repository.findAll().stream()
                .filter(d -> d.getStatus() == DeviceStatus.DECOMMISSIONED)
                .toList();
    }

    public void returnDevice(String deviceId) {
        Device device = findDeviceById(deviceId)
                .orElseThrow(() -> new DeviceNotFoundException(deviceId));

        device.returnToInventory();

        repository.save(device);
        System.out.println("Device returned successfully: " + deviceId);
    }

    public void moveDeviceToMaintenance(String deviceId, String reason) {
        Device device = findDeviceById(deviceId)
                .orElseThrow(() -> new DeviceNotFoundException(deviceId));

        device.sendToMaintenance(reason);

        repository.save(device);
        System.out.println("Device moved to maintenance: " + deviceId + " Reason: " + reason);
    }

    public void completeDeviceRepair(String deviceId) {
        Device device = findDeviceById(deviceId)
                .orElseThrow(() -> new DeviceNotFoundException(deviceId));

        device.repairCompleted();

        repository.save(device);
        System.out.println("Device repair completed: " + deviceId);
    }

    public void decommissionDevice(String deviceId) {
        Device device = findDeviceById(deviceId)
                .orElseThrow(() -> new DeviceNotFoundException(deviceId));

        device.decommission();

        repository.save(device);
        System.out.println("Device decommissioned: " + deviceId);
    }
}