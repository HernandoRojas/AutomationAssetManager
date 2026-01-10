package exception;

public class InvalidDeviceStateException extends AssetManagerException {
    public InvalidDeviceStateException(String id, String action, String currentStatus) {
        super(String.format("Cannot perform '%s' on device %s. Current status is: %s", 
              action, id, currentStatus));
    }
}