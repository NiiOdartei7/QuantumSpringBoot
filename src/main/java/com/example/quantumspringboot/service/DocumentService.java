package com.example.quantumspringboot.service;

import com.cosmian.jna.covercrypt.CoverCrypt;
import com.cosmian.jna.covercrypt.structs.MasterKeys;
import com.cosmian.jna.covercrypt.structs.Policy;
import com.cosmian.rest.abe.KmsClient;
import com.cosmian.rest.abe.data.DecryptedData;
import com.cosmian.rest.kmip.objects.PublicKey;
import com.cosmian.utils.CloudproofException;
import com.example.quantumspringboot.config.MasterKeysInfo;
import com.example.quantumspringboot.config.NativeMasterKeys;
import com.example.quantumspringboot.dto.DocumentDTO;
import com.example.quantumspringboot.dto.DocumentResponseDTO;
import com.example.quantumspringboot.dto.UserResponseDTO;
import com.example.quantumspringboot.entity.Document;
import com.example.quantumspringboot.entity.SecurePolicy;
import com.example.quantumspringboot.entity.User;
import com.example.quantumspringboot.exceptions.EntityAlreadyExistsException;
import com.example.quantumspringboot.exceptions.EntityDoesNotExistException;
import com.example.quantumspringboot.repository.DocumentRepository;
import com.example.quantumspringboot.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final LocalStorageService localStorageService;
    private final NativeMasterKeys nativeMasterKeys;

    @Value("${file.upload-dir}")
    private String uploadDiriector;


    public DocumentDTO findDocument(UUID id) throws EntityDoesNotExistException {
        if(documentRepository.existsDocumentById(id)){
            Document doc = documentRepository.findDocumentById(id);
            return objectMapper.convertValue(doc, DocumentDTO.class);
        }
        throw new EntityDoesNotExistException("Document does not exist");
    }

    public DocumentDTO uploadDocument(MultipartFile file, String filename,
                                      UUID userId, String encryptionPolicy ) throws EntityAlreadyExistsException, IOException, CloudproofException {
        if(documentRepository.existsDocumentByFilename(filename)){
            throw new EntityAlreadyExistsException("A document with that name exists");
        }

        byte[] publicKey = nativeMasterKeys.masterKeys().getPublicKey();

        UserResponseDTO user = userService.findUserById(userId);
        objectMapper.convertValue(user, User.class);
        byte[] pdfBytes = file.getBytes();
        byte[] protectedMkgCT = CoverCrypt.encrypt(nativeMasterKeys.policy(),
                publicKey, encryptionPolicy,
                pdfBytes,
                Optional.empty(),
                Optional.empty());

        String url = localStorageService.save(protectedMkgCT, filename);
        Document saveddoc = new Document(objectMapper.convertValue(user, User.class),url,filename);
        documentRepository.save(saveddoc);
        System.out.println(saveddoc.getId());
        return objectMapper.convertValue(saveddoc, DocumentDTO.class);
    }

    public DocumentResponseDTO retrieveAndDecrypt(UUID documentID, UUID userID) throws EntityDoesNotExistException, CloudproofException, IOException {
        if(documentRepository.existsDocumentById(documentID)){
            Document document = documentRepository.findDocumentById(documentID);

            Path uploadDir = Paths.get(uploadDiriector); // "uploads"
            Path filePath = uploadDir.resolve(document.getUrl().substring(1)); // remove leading /
            if (!Files.exists(filePath)) {
                throw new RuntimeException("File not found: " + filePath.toAbsolutePath());
            }
//            byte[] fileBytes = Files.readAllBytes(filePath);

//            Path path = Paths.get(document.getUrl());
            byte[] fileBytes = Files.readAllBytes(filePath);
            byte[] privateKey = nativeMasterKeys.masterKeys().getPrivateKey();

            UserResponseDTO user = userService.findUserById(userID);
            byte[] decryptionKey = CoverCrypt.generateUserPrivateKey(privateKey,
                    user.getUserAccessPolicy(),nativeMasterKeys.policy());

            DecryptedData protectedMkg = CoverCrypt.decrypt(decryptionKey, fileBytes, Optional.empty());
            return new DocumentResponseDTO(documentID, document.getStatus(), document.getUrl(),
                    document.getStatus(), protectedMkg.getPlaintext());
        }
        throw new EntityDoesNotExistException("A document with that ID does not exist");


    }

    private static String bytesToHex(byte[] bytes, int length) {
        int len = Math.min(length, bytes.length);
        byte[] subset = new byte[len];
        System.arraycopy(bytes, 0, subset, 0, len);
        return HexFormat.of().formatHex(subset);
    }


}
