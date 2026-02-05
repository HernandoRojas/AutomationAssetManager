package com.assetmanager.repository;

import java.util.List;
import java.util.Optional;

import com.assetmanager.model.Device;

public interface DeviceRepository {
    void save(Device device);
    Optional<Device> findById(String id);
    List<Device> findAll();
}