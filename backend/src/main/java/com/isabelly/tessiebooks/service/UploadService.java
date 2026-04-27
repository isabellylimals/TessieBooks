package com.isabelly.tessiebooks.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

@Service
public class UploadService {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar pasta de uploads");
        }
    }

    public String saveFile(MultipartFile file) throws IOException {
        // Gerar nome único
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;
        
        // Salvar arquivo
        Path filePath = Paths.get(uploadDir, filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Retornar URL para acessar
        return "/uploads/" + filename;
    }
}