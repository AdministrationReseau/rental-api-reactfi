package inc.yowyob.rental_api_reactive.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

/**
 * Service de gestion des images avec chiffrement des noms et liens
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleImageService {

    @Value("${app.file.upload-dir:./uploads}")
    private String uploadDir;

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String SECRET_KEY = "MySecretKey12345"; // À configurer en variable d'environnement

    /**
     * Sauvegarde un fichier et retourne les informations chiffrées
     */
    public Mono<FileUploadResult> saveFile(byte[] fileData, String originalFilename, String entityType, UUID entityId) {
        return Mono.fromCallable(() -> {
            try {
                // Créer les dossiers nécessaires
                String entityDir = uploadDir + "/images/" + entityType;
                Path entityPath = Paths.get(entityDir);
                Files.createDirectories(entityPath);

                // Générer un nom de fichier unique et chiffré
                String fileExtension = getFileExtension(originalFilename);
                String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
                String encryptedFilename = encryptString(uniqueFilename);

                // Chemin complet du fichier
                Path filePath = entityPath.resolve(uniqueFilename);

                // Sauvegarder le fichier
                Files.write(filePath, fileData);

                // Créer le lien chiffré
                String relativePath = "images/" + entityType + "/" + uniqueFilename;
                String encryptedUrl = encryptString(relativePath);

                log.info("File saved successfully: {} -> {}", originalFilename, uniqueFilename);

                return FileUploadResult.builder()
                    .encryptedFilename(encryptedFilename)
                    .encryptedUrl(encryptedUrl)
                    .originalFilename(originalFilename)
                    .filePath(filePath.toString())
                    .fileSize((long) fileData.length)
                    .build();

            } catch (Exception e) {
                log.error("Error saving file: {}", originalFilename, e);
                throw new RuntimeException("Failed to save file", e);
            }
        });
    }

    /**
     * Déchiffre une URL pour récupérer le fichier
     */
    public Mono<String> decryptUrl(String encryptedUrl) {
        return Mono.fromCallable(() -> {
            try {
                String decryptedPath = decryptString(encryptedUrl);
                return uploadDir + "/" + decryptedPath;
            } catch (Exception e) {
                log.error("Error decrypting URL: {}", encryptedUrl, e);
                throw new RuntimeException("Failed to decrypt URL", e);
            }
        });
    }

    /**
     * Supprime un fichier
     */
    public Mono<Void> deleteFile(String encryptedUrl) {
        return decryptUrl(encryptedUrl)
            .flatMap(filePath -> Mono.fromRunnable(() -> {
                try {
                    Path path = Paths.get(filePath);
                    Files.deleteIfExists(path);
                    log.info("File deleted successfully: {}", filePath);
                } catch (IOException e) {
                    log.error("Error deleting file: {}", filePath, e);
                    throw new RuntimeException("Failed to delete file", e);
                }
            }));
    }

    /**
     * Chiffre une chaîne de caractères
     */
    private String encryptString(String plainText) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    /**
     * Déchiffre une chaîne de caractères
     */
    private String decryptString(String encryptedText) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedData);
    }

    /**
     * Extrait l'extension du fichier
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        return (lastDotIndex > 0) ? filename.substring(lastDotIndex) : "";
    }

    /**
     * Résultat du téléchargement de fichier
     */
    @lombok.Data
    @lombok.Builder
    public static class FileUploadResult {
        private String encryptedFilename;
        private String encryptedUrl;
        private String originalFilename;
        private String filePath;
        private Long fileSize;
    }
}
