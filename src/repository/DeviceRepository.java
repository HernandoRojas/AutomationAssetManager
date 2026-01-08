package repository;

import model.Device;
import java.util.List;
import java.util.Optional;

public interface DeviceRepository {
    void save(Device device);
    Optional<Device> findById(String id);
    List<Device> findAll();
}