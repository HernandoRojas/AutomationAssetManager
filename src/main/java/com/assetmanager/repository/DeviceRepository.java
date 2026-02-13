package com.assetmanager.repository;

import com.assetmanager.model.Device;
import com.assetmanager.model.DeviceStatus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
    // Finds by status
    List<Device> findByStatus(DeviceStatus status);

    // Finds by brand (ignoring case)
    List<Device> findByBrandIgnoreCase(String brand);

    // Combined filter for your search UI
    List<Device> findByStatusAndBrandIgnoreCase(DeviceStatus status, String brand);
}