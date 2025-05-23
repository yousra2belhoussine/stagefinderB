package ma.stagefinder.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.File;

@Component
public class FileStorageInitializer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.images-dir}")
    private String imagesDir;

    @Value("${file.documents-dir}")
    private String documentsDir;

    @PostConstruct
    public void init() {
        // Créer les dossiers s'ils n'existent pas
        new File(uploadDir).mkdirs();
        new File(imagesDir).mkdirs();
        new File(documentsDir).mkdirs();
    }
}