package com.example.quantumspringboot.repository;

import com.example.quantumspringboot.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, Integer> {

    Document findDocumentById(UUID id);


    boolean existsDocumentById(UUID id);

    boolean existsDocumentByFilename(String filename);

}
