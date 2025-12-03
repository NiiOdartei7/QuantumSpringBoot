package com.example.quantumspringboot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalStorageService implements StorageService{
    private final Path root = Paths.get("uploads");

    public String save(byte[] file, String filename) {
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            Path target = root.resolve(filename);
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(file)) {
                Files.copy(inputStream, target);
            }
//            Files.copy(file.getInputStream(), target);

            return "/uploads/" + filename;
        }
        catch (Exception e) {
            throw new RuntimeException("Could not store file", e);
        }
    }

    @Override
    public void delete(String url) {

    }
}
