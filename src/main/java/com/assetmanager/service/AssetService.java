package com.assetmanager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.assetmanager.exception.DeviceNotFoundException;
import com.assetmanager.exception.UserNotFoundException;
import com.assetmanager.model.Device;
import com.assetmanager.model.User;
import com.assetmanager.model.DeviceStatus;
import com.assetmanager.repository.DeviceRepository;
import com.assetmanager.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class AssetService {
    // We depend on the Interface, not the implementation
    private final DeviceRepository repository;
    private final UserRepository userRepository;

    // Constructor Injection
    public AssetService(DeviceRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
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

    public List<User> findUserByEmployeeId(String employeeId) {
        return userRepository.findByEmployeeIdIgnoreCase(employeeId);
    }

    public List<Device> findDevicesByUserId(String employeeId) {
        if (employeeId != null && !employeeId.isBlank()) {
            return findUserByEmployeeId(employeeId).stream()
                    .flatMap(user -> repository.findByUser(user).stream())
                    .toList();
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

    @Transactional
    public void rentDevice(String deviceId, int userId) {
        // 1. Find the user
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        // 2. Find the device
        Device device = getCreatedDevice(deviceId);

        // 3. Business Logic: The "rent" method inside Device handles the status check and state transition
        device.rent();

        // 4. Asign the device to the user
        device.setOwner(user);

        // 4. Persist the change
        repository.save(device);
        userRepository.save(user);
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

    @Transactional
    public void transferDevice(String deviceId, String employeeId) {
        // 1. Check that device exists
        Device device = getCreatedDevice(deviceId);

        // 2. Check that user exists
        List<User> users = userRepository.findByEmployeeIdIgnoreCase(employeeId);
        if (users.isEmpty()) {
             throw new UserNotFoundException(0);
        }
        User targetUser = users.get(0);

        // 3. Transfer the device to the new owner
        device.transfer(targetUser);

        // 4. Persist the changes
        repository.save(device);
        userRepository.save(targetUser);
        System.out.println("Device transfered " + deviceId + " to: " + targetUser.getEmployeeId());
    }

    @Transactional
    public void registerDevicesBatch(List<Device> devices) {
        // Validate all devices before saving any (all-or-nothing approach)
        for (int i = 0; i < devices.size(); i++) {
            Device device = devices.get(i);

            // Validate device is not null
            if (device == null) {
                throw new IllegalArgumentException(
                    "Batch processing failed at index " + i + ": Device cannot be null"
                );
            }
            
            // Check if device ID already exists
            if (repository.existsById(device.getDeviceId())) {
                throw new IllegalArgumentException(
                    "Batch processing failed at index " + i + ": Device ID already exists: " + device.getDeviceId()
                );
            }
        }
        
        // If all validations pass, save all devices
        for (Device device : devices) {
            repository.save(device);
        }
        
        System.out.println("Batch processing completed successfully. " + devices.size() + " devices registered.");
    }
}