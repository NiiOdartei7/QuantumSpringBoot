package com.example.quantumspringboot.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.example.quantumspringboot.dto.DBDocumentDTO;
import com.example.quantumspringboot.entity.Document;
import com.example.quantumspringboot.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service implements StorageService{
    private final AmazonS3 amazonS3;
    private final DocumentRepository documentRepository;

    @Value("${aws.s3.bucket}")
    private String bucketName;


    @Override
    public String save(byte[] file, String filename) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.length);
            metadata.setContentType("application/octet-stream");

            ByteArrayInputStream inputStream = new ByteArrayInputStream(file);

            amazonS3.putObject(new PutObjectRequest(bucketName, filename, inputStream, metadata));
            return "File uploaded successfully: " + filename;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error uploading file";
        }
    }

    @Override
    public void delete(String url) {

    }

    @Override
    public DBDocumentDTO download(UUID documentID) throws IOException {
        if(documentRepository.existsDocumentById(documentID)){
            Document document = documentRepository.findDocumentById(documentID);
            S3Object s3Object = amazonS3.getObject(bucketName, document.getFilename());
            byte[] fileBytes;
            try (InputStream inputStream = s3Object.getObjectContent()) {
               fileBytes = inputStream.readAllBytes();
            }
            DBDocumentDTO documentDTO = new DBDocumentDTO();
            documentDTO.setFilename(document.getFilename());
            if(fileBytes.length == 0){
                throw new FileNotFoundException("The is a file but no bytes");
            }
            documentDTO.setContent(fileBytes);
            return documentDTO;
        }
        throw new FileNotFoundException("File does not exist");

    }




}
