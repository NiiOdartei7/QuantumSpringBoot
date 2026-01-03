package com.example.quantumspringboot.service;

import com.example.quantumspringboot.dto.DBDocumentDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface StorageService {
    String save(byte[] file, String filename);
    void delete(String url);
    DBDocumentDTO download(UUID documentID) throws IOException;
}
