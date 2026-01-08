package repository;

import model.Device;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryDeviceRepository implements DeviceRepository {
    // Map<Key, Value> -> Map<ID, DeviceObject>
    private final Map<String, Device> inventory = new HashMap<>();

    @Override
    public void save(Device device) {
        inventory.put(device.getDeviceId(), device);
    }

    @Override
    public Optional<Device> findById(String id) {
        // Optional handles the case where the ID doesn't exist safely
        return Optional.ofNullable(inventory.get(id));
    }

    @Override
    public List<Device> findAll() {
        return new ArrayList<>(inventory.values());
    }
}