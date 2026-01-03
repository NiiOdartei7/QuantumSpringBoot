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
import com.example.quantumspringboot.dto.*;
import com.example.quantumspringboot.entity.Department;
import com.example.quantumspringboot.entity.Document;
import com.example.quantumspringboot.entity.SecurePolicy;
import com.example.quantumspringboot.entity.User;
import com.example.quantumspringboot.exceptions.AttributeMismatch;
import com.example.quantumspringboot.exceptions.EntityAlreadyExistsException;
import com.example.quantumspringboot.exceptions.EntityDoesNotExistException;
import com.example.quantumspringboot.repository.DocumentRepository;
import com.example.quantumspringboot.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final LocalStorageService localStorageService;
    private final NativeMasterKeys nativeMasterKeys;
    private final S3Service S3service;

    @Value("${file.upload-dir}")
    private String uploadDiriector;

    @Value("${storage.type}")
    private String storageType;

    public DocumentDTO findDocument(UUID id) throws EntityDoesNotExistException {
        if(documentRepository.existsDocumentById(id)){
            Document doc = documentRepository.findDocumentById(id);
            return objectMapper.convertValue(doc, DocumentDTO.class);
        }
        throw new EntityDoesNotExistException("Document does not exist");
    }

    public List<DocumentDTO> findAllDocuments() throws EntityDoesNotExistException {
        List<Document> documents = documentRepository.findAll();
        List<DocumentDTO> documentDTOs = new ArrayList<>();
        for(Document document: documents){
            DocumentDTO documentDTO = objectMapper.convertValue(document, DocumentDTO.class);
            documentDTOs.add(documentDTO);

        }

        return documentDTOs;
    }


    public DocumentDTO uploadDocument(MultipartFile file, String filename,
                                      UUID userId, String encryptionPolicy ) throws EntityAlreadyExistsException, IOException, CloudproofException {
        if(documentRepository.existsDocumentByFilename(filename)){
            throw new EntityAlreadyExistsException("A document with that name exists");
        }


        UserResponseDTO user = userService.findUserById(userId);
        objectMapper.convertValue(user, User.class);
        byte[] pdfBytes = file.getBytes();

        if(Objects.equals(storageType, "local")){
            String url = localStorageService.save(pdfBytes, filename);
            Document saveddoc = new Document(objectMapper.convertValue(user, User.class),url,filename);
            documentRepository.save(saveddoc);
            System.out.println(saveddoc.getId());
            return objectMapper.convertValue(saveddoc, DocumentDTO.class);
        }else{
            String url = S3service.save(pdfBytes, filename);
            Document saveddoc = new Document(objectMapper.convertValue(user, User.class),url,filename);
            documentRepository.save(saveddoc);
            System.out.println(saveddoc.getId());
            return objectMapper.convertValue(saveddoc, DocumentDTO.class);
        }

//

    }

    public DocumentResponseDTO retrieveAndDecrypt(UUID documentID, UUID userID) throws EntityDoesNotExistException, CloudproofException, IOException {
//        byte[] fileBytes =localStorageService.download(documentID);
//        Document document = documentRepository.findDocumentById(documentID);
        DBDocumentDTO documentDTO = localStorageService.download(documentID);

            byte[] privateKey = nativeMasterKeys.masterKeys().getPrivateKey();

            UserResponseDTO user = userService.findUserById(userID);
            byte[] decryptionKey = CoverCrypt.generateUserPrivateKey(privateKey,
                    user.getUserAccessPolicy(),nativeMasterKeys.policy());
            try{
                DecryptedData protectedMkg = CoverCrypt.decrypt(decryptionKey, documentDTO.getContent(), Optional.empty());
                return new DocumentResponseDTO(documentID, documentDTO.getStatus(), documentDTO.getUrl(),
                        documentDTO.getStatus(), protectedMkg.getPlaintext());
            } catch (CloudproofException e){
                if(e.getMessage() != null && e.getMessage().contains("User decryption key has not the right policy to decrypt this input.")){
                    throw new AttributeMismatch("You do not have the right attributes");
                }else{
                    throw new RuntimeException("Decryption failed due to "+ e.getMessage());
                }
            }

    }

    public byte[] getPublicKey() {
        return nativeMasterKeys.masterKeys().getPublicKey();
    }

    public Policy getPolicy() {
        return nativeMasterKeys.policy();
    }

    public List<String> previewDecryptedDocument(UUID documentID, UUID userID) throws Exception {
        DBDocumentDTO documentDTO;
        if(storageType.equals("local")){
            documentDTO = localStorageService.download(documentID);
        }else{
            documentDTO = S3service.download(documentID);
        }

            byte[] privateKey = nativeMasterKeys.masterKeys().getPrivateKey();

            UserResponseDTO user = userService.findUserById(userID);
            byte[] decryptionKey = CoverCrypt.generateUserPrivateKey(privateKey,
                    user.getUserAccessPolicy(),nativeMasterKeys.policy());
            try{
                DecryptedData protectedMkg = CoverCrypt.decrypt(decryptionKey, documentDTO.getContent(), Optional.empty());
                return convertPDFToImages(protectedMkg.getPlaintext());
            } catch (CloudproofException e){
                if(e.getMessage() != null && e.getMessage().contains("User decryption key has not the right policy to decrypt this input.")){
                    throw new AttributeMismatch("You do not have the right attributes");
                }else{
                    throw new RuntimeException("Decryption failed due to "+ e.getMessage());
                }
            }
        }

    private List<String> convertPDFToImages(byte[] plaintext) throws IOException {
        List<String> pages = new ArrayList<>();

        try (PDDocument doc = Loader.loadPDF(plaintext)) {
            PDFRenderer renderer = new PDFRenderer(doc);

            for (int page = 0; page < doc.getNumberOfPages(); page++) {
                BufferedImage image = renderer.renderImageWithDPI(page, 150);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);

                String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
                pages.add("data:image/png;base64," + base64);
            }
        }
        return pages;
    }
}
