package inc.yowyob.rental_api_reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RentalApiReactiveApplication {

	public static void main(String[] args) {
        createRequiredDirectories();
		SpringApplication.run(RentalApiReactiveApplication.class, args);
	}

    /**
     * Crée les dossiers nécessaires pour upload de fichiers
     */
    private static void createRequiredDirectories() {
        try {
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("./uploads/images/vehicles"));
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("./uploads/images/drivers"));
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("./uploads/images/users"));
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("./uploads/files/documents"));
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("./uploads/files/contracts"));

            System.out.println("✅ Upload directories created successfully");
        } catch (Exception e) {
            System.err.println("❌ Failed to create upload directories: " + e.getMessage());
        }
    }

}
