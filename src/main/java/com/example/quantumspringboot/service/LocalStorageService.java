package com.example.quantumspringboot.service;

import com.example.quantumspringboot.dto.DBDocumentDTO;
import com.example.quantumspringboot.entity.Document;
import com.example.quantumspringboot.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalStorageService implements StorageService{
    private final Path root = Paths.get("uploads");
    private final DocumentRepository documentRepository;

    @Value("${file.upload-dir}")
    private String uploadDiriector;

    public String save(byte[] file, String filename) {
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            try{
                Path target = root.resolve(filename);
                try (ByteArrayInputStream inputStream = new ByteArrayInputStream(file)) {
                    Files.copy(inputStream, target);
                }

                return "/uploads/" + filename;

            }catch (FileAlreadyExistsException e){
                throw new FileAlreadyExistsException("File already exists");
            }

        }
        catch (Exception e) {
            throw new RuntimeException("Could not store file", e);
        }
    }

    @Override
    public void delete(String url) {

    }

    @Override
    public DBDocumentDTO download(UUID documentID) throws IOException {
        if(documentRepository.existsDocumentById(documentID)){
            Document document = documentRepository.findDocumentById(documentID);

            Path uploadDir = Paths.get(uploadDiriector);
            Path filePath = uploadDir.resolve(document.getUrl().substring(1));
            if (!Files.exists(filePath)) {
                throw new RuntimeException("File not found: " + filePath.toAbsolutePath());
            }
            byte[] fileBytes= Files.readAllBytes(filePath);
            return new DBDocumentDTO(document.getFilename(), fileBytes, document.getStatus(), document.getUrl(), document.getId());
        }
       else throw new FileNotFoundException("No file with this ID exists");
    }
}
