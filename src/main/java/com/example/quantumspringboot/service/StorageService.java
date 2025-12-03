package com.example.quantumspringboot.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String save(byte[] file, String filename);
    void delete(String url);
}
