package exception;

public class DeviceNotFoundException extends AssetManagerException{
    public DeviceNotFoundException(String id) {
        super("Device with ID " + id + " was not found in the inventory.");
    }
}