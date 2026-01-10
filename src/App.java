import model.Laptop;
import model.MobilePhone;
import repository.DeviceRepository;
import repository.InMemoryDeviceRepository;
import service.AssetService;

public class App {
    public static void main(String[] args) {
        // 1. SETUP: Initialize the infrastructure
        // We declare the variable as the Interface (DeviceRepository)
        DeviceRepository repository = new InMemoryDeviceRepository();
        AssetService assetService = new AssetService(repository);

        System.out.println("--- Starting Automation Asset Manager ---");

        // 2. DATA INPUT: Registering devices
        // Note: We use the specific subclasses
        MobilePhone phone1 = new MobilePhone("M001", "Samsung", "S24 Ultra", "Android 14", "+57300123");
        Laptop laptop1 = new Laptop("L001", "Apple", "MacBook Pro", "macOS Sonoma", 16);
        MobilePhone phone2 = new MobilePhone("M002", "Apple", "iPhone 15 Pro", "iOS 17", "+57300456");
        assetService.registerNewDevice(phone1);
        assetService.registerNewDevice(laptop1);
        assetService.registerNewDevice(phone2);

        // 3. EXECUTION: Testing our Business Logic
        System.out.println("\nInventory before rental:");
        assetService.getAllAvailableDevices().forEach(System.out::println);

        try {
            System.out.println("\nAttempting to rent device: M001...");
            assetService.rentDevice("M001");
            
            System.out.println("\nInventory after rental:");
            // M001 should no longer appear in 'available' list
            assetService.getAllAvailableDevices().forEach(System.out::println);

            // 4. TEST ERROR HANDLING: Attempting to rent the same device again
            System.out.println("\nAttempting to rent M001 again (this should fail)...");
            assetService.rentDevice("M001");

        } catch ( RuntimeException e) {
            System.err.println("ALERT: " + e.getMessage());
        }


        try {
            // TRYING TO RENT A NON-EXISTENT DEVICE
            System.out.println("\nAttempting to rent non-existent device: X999...");
            assetService.rentDevice("X999");

        } catch ( RuntimeException e) {
            System.err.println("ALERT: " + e.getMessage());
        }
        
        // 5. VERIFY MAINTENANCE LOGIC
        System.out.println("\nFinal State of all assets (including rented):");
        // To see everything, we'd go to the repository or add a method to Service
        repository.findAll().forEach(System.out::println);
    }
}