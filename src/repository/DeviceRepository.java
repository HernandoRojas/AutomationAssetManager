package repository;

public interface DeviceRepository {

    public void saveDevice(Device device){
        // Implementation to save device to the repository
    };
    public Device findById(int deviceId){
        // Implementation to find a device by its ID
    };

}