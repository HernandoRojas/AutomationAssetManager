package service;

import java.util.List;

import exception.DeviceNotFoundException;
import model.Device;
import model.DeviceStatus;
import repository.DeviceRepository;

public class AssetService {
    // We depend on the Interface, not the implementation!
    private final DeviceRepository repository;

    // Constructor Injection: This makes the service easy to test
    public AssetService(DeviceRepository repository) {
        this.repository = repository;
    }

    public void registerNewDevice(Device device) {
        // Business Rule: IDs must be unique (simplified check)
        if (repository.findById(device.getDeviceId()).isPresent()) {
            throw new IllegalArgumentException("Device ID already exists: " + device.getDeviceId());
        }
        repository.save(device);
    }

    public void rentDevice(String deviceId) {
        // 1. Find the device
        Device device = repository.findById(deviceId)
                .orElseThrow(() -> new DeviceNotFoundException(deviceId));

        // 2. Business Logic: The "rent" method inside Device handles the status check
        // This is "Tell, Don't Ask" principle.
        device.rent();

        // 3. Persist the change
        repository.save(device);
        System.out.println("Device rented successfully: " + deviceId);
    }

    public List<Device> getAllAvailableDevices() {
        return repository.findAll().stream()
                .filter(d -> d.getStatus() == DeviceStatus.AVAILABLE)
                .toList();
    }

    public void returnDevice(String deviceId) {
        Device device = repository.findById(deviceId)
                .orElseThrow(() -> new DeviceNotFoundException(deviceId));

        device.returnToInventory();

        repository.save(device);
        System.out.println("Device returned successfully: " + deviceId);
    }
}