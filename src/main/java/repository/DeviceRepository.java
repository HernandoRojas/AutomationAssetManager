package repository;

import java.util.List;
import java.util.Optional;

import model.Device;

public interface DeviceRepository {
    void save(Device device);
    Optional<Device> findById(String id);
    List<Device> findAll();
}