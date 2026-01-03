package com.example.quantumspringboot.controller;

import com.cosmian.jna.covercrypt.structs.Policy;
import com.cosmian.utils.CloudproofException;
import com.example.quantumspringboot.dto.*;
import com.example.quantumspringboot.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DocumentController {
    private final DocumentService service;



    @PostMapping("/v1/document")
    public ResponseEntity<DocumentDTO> uploadDocument( @RequestParam("file") MultipartFile file,
                                                       @RequestParam("user_id") UUID userId,
                                                       @RequestParam("filename") String filename,
                                                       @RequestParam("encryptionPolicy") String encryptionPolicy
    ) throws CloudproofException, IOException {
        return ResponseEntity.ok(service.uploadDocument(file, filename, userId,encryptionPolicy));
    }

    @GetMapping("/v1/document/{documentID}")
    public ResponseEntity<ByteArrayResource> downloadDocument( @PathVariable UUID documentID,
                                                               @RequestParam UUID userID
    ) throws CloudproofException, IOException {
        System.out.println("docid " + documentID );
        DocumentResponseDTO responseDTO = service.retrieveAndDecrypt(documentID, userID);
        ByteArrayResource resource = new ByteArrayResource(responseDTO.getContent());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(responseDTO.getContent().length)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + responseDTO.getFilename() + "\"")
                .body(resource);
//        return ResponseEntity.ok(service.retrieveAndDecrypt(requestDTO.getDocumentID(), requestDTO.getUser_id()));
    }

    @GetMapping("/v1/document/find/{documentID}")
    public ResponseEntity<DocumentDTO> findDoc(@PathVariable UUID documentID
    ){
        return ResponseEntity.ok(service.findDocument(documentID));
    }

    @GetMapping("/v1/document/public-key")
    public ResponseEntity<Map<String, String>> publicKey(
    ){
        byte[] key = service.getPublicKey();
        return ResponseEntity.ok(Map.of("key", Base64.getEncoder().encodeToString(key)));
    }

    @GetMapping("/v1/document/policy")
    public ResponseEntity<Policy> getPolicy(
    ){
        return ResponseEntity.ok(service.getPolicy());
    }

    @GetMapping("/v1/document/all")
    public ResponseEntity<List<DocumentDTO>> getAllDocuments(
    ){
        return ResponseEntity.ok(service.findAllDocuments());
    }

    @GetMapping("/v1/document/preview/{documentID}")
    public ResponseEntity<List<String>> getDocImages( @PathVariable UUID documentID,
                                                      @RequestParam UUID userID
    ) throws Exception {
        return ResponseEntity.ok(service.previewDecryptedDocument(documentID, userID));
    }

}
