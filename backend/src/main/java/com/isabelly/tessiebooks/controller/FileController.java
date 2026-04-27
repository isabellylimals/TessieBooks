package com.isabelly.tessiebooks.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.isabelly.tessiebooks.service.UploadService;

@RestController
@RequestMapping("/uploads")
public class FileController {

    private final UploadService uploadService;
    
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    public FileController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/review-image")
    public ResponseEntity<?> uploadReviewImage(@RequestParam("image") MultipartFile file) {
        try {
            String imageUrl = uploadService.saveFile(file);
            return ResponseEntity.ok(java.util.Map.of("imageUrl", imageUrl));
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Erro ao fazer upload: " + e.getMessage());
        }
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir, filename);
            byte[] image = Files.readAllBytes(filePath);
            String contentType = Files.probeContentType(filePath);
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(image);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}