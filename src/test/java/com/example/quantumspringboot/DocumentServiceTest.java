package com.example.quantumspringboot;

import com.example.quantumspringboot.config.NativeMasterKeys;
import com.example.quantumspringboot.dto.*;
import com.example.quantumspringboot.entity.Document;
import com.example.quantumspringboot.entity.User;
import com.example.quantumspringboot.exceptions.AttributeMismatch;
import com.example.quantumspringboot.exceptions.EntityAlreadyExistsException;
import com.example.quantumspringboot.exceptions.EntityDoesNotExistException;
import com.example.quantumspringboot.repository.DocumentRepository;
import com.cosmian.jna.covercrypt.CoverCrypt;
import com.cosmian.jna.covercrypt.structs.MasterKeys;
import com.cosmian.jna.covercrypt.structs.Policy;
import com.cosmian.utils.CloudproofException;
import com.example.quantumspringboot.service.DocumentService;
import com.example.quantumspringboot.service.LocalStorageService;
import com.example.quantumspringboot.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private UserService userService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private LocalStorageService localStorageService;

    @Mock
    private NativeMasterKeys nativeMasterKeys;


    @InjectMocks
    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- findDocument ---
    @Test
    void testFindDocument_exists() throws EntityDoesNotExistException {
        UUID docId = UUID.randomUUID();
        Document document = new Document();
        document.setId(docId);
        DocumentDTO dto = new DocumentDTO();

        when(documentRepository.existsDocumentById(docId)).thenReturn(true);
        when(documentRepository.findDocumentById(docId)).thenReturn(document);
        when(objectMapper.convertValue(document, DocumentDTO.class)).thenReturn(dto);

        DocumentDTO result = documentService.findDocument(docId);

        assertNotNull(result);
        verify(documentRepository).findDocumentById(docId);
    }

    @Test
    void testFindDocument_notExists() {
        UUID docId = UUID.randomUUID();
        when(documentRepository.existsDocumentById(docId)).thenReturn(false);

        assertThrows(EntityDoesNotExistException.class, () -> documentService.findDocument(docId));
    }

    // --- findAllDocuments ---
    @Test
    void testFindAllDocuments() throws EntityDoesNotExistException {
        Document doc1 = new Document();
        Document doc2 = new Document();
        List<Document> docs = Arrays.asList(doc1, doc2);

        DocumentDTO dto1 = new DocumentDTO();
        DocumentDTO dto2 = new DocumentDTO();

        when(documentRepository.findAll()).thenReturn(docs);
        // Use argument matchers instead of exact object
        when(objectMapper.convertValue(any(Document.class), eq(DocumentDTO.class)))
                .thenAnswer(invocation -> {
                    Document arg = invocation.getArgument(0);
                    if(arg == doc1) return dto1;
                    else if(arg == doc2) return dto2;
                    else return null;
                });

        List<DocumentDTO> result = documentService.findAllDocuments();

        assertEquals(2, result.size());
        assertSame(dto1, result.get(0));
        assertSame(dto2, result.get(1));
    }

    // --- uploadDocument ---
    @Test
    void testUploadDocument_success() throws IOException, CloudproofException, EntityAlreadyExistsException {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "test content".getBytes());
        UUID userId = UUID.randomUUID();
        String filename = "test.pdf";
        String policy = "policy";

        byte[] publicKey = new byte[]{1,2,3};
        UserResponseDTO userDto = new UserResponseDTO();
        User userEntity = new User();
        Document savedDoc = new Document();

        when(documentRepository.existsDocumentByFilename(filename)).thenReturn(false);
        when(nativeMasterKeys.masterKeys()).thenReturn(mock(MasterKeys.class));
        when(nativeMasterKeys.masterKeys().getPublicKey()).thenReturn(publicKey);
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(objectMapper.convertValue(userDto, User.class)).thenReturn(userEntity);
        when(localStorageService.save(any(byte[].class), eq(filename))).thenReturn("/path/to/file");
        when(objectMapper.convertValue(any(Document.class), eq(DocumentDTO.class))).thenReturn(new DocumentDTO());

        DocumentDTO result = documentService.uploadDocument(file, filename, userId, policy);

        assertNotNull(result);
        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void testUploadDocument_fileAlreadyExists() {
        UUID userId = UUID.randomUUID();
        String filename = "test.pdf";

        when(documentRepository.existsDocumentByFilename(filename)).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class,
                () -> documentService.uploadDocument(new MockMultipartFile("file","",null,new byte[0]), filename, userId, "policy"));
    }

    // --- retrieveAndDecrypt ---
//    @Test
//    void testRetrieveAndDecrypt_success() throws Exception {
//        UUID docId = UUID.randomUUID();
//        UUID userId = UUID.randomUUID();
//
//        DBDocumentDTO dbDocumentDTO = new DBDocumentDTO();
//        dbDocumentDTO.setContent("test".getBytes());
//        dbDocumentDTO.setStatus("READY");
//        dbDocumentDTO.setUrl("/path/file");
//
//        UserResponseDTO userDto = new UserResponseDTO();
//        userDto.setUserAccessPolicy("policy");
//
//        byte[] privateKey = new byte[]{1,2,3};
//        byte[] decryptionKey = new byte[]{4,5,6};
//
//        DecryptedData decryptedData = mock(DecryptedData.class);
//        when(decryptedData.getPlaintext()).thenReturn("decrypted".getBytes());
//
//        MasterKeys masterKeys = mock(MasterKeys.class);
//        when(masterKeys.getPrivateKey()).thenReturn(privateKey);
//        when(nativeMasterKeys.masterKeys()).thenReturn(masterKeys);
//        when(nativeMasterKeys.policy()).thenReturn(mock(Policy.class));
//        when(localStorageService.download(docId)).thenReturn(dbDocumentDTO);
//        when(userService.findUserById(userId)).thenReturn(userDto);
//
//        try (MockedStatic<CoverCrypt> coverCryptMockedStatic = mockStatic(CoverCrypt.class)) {
//            coverCryptMockedStatic.when(() -> CoverCrypt.generateUserPrivateKey(privateKey, userDto.getUserAccessPolicy(), nativeMasterKeys.policy()))
//                    .thenReturn(decryptionKey);
//            coverCryptMockedStatic.when(() -> CoverCrypt.decrypt(decryptionKey, dbDocumentDTO.getContent(), Optional.empty()))
//                    .thenReturn(decryptedData);
//
//            var response = documentService.retrieveAndDecrypt(docId, userId);
//
//            assertNotNull(response);
//            assertArrayEquals("decrypted".getBytes(), response.getDocument());
//        }
//    }
//
    @Test
    void testRetrieveAndDecrypt_attributeMismatch() throws Exception {
        UUID docId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        DBDocumentDTO dbDocumentDTO = new DBDocumentDTO();
        dbDocumentDTO.setContent("test".getBytes());
        dbDocumentDTO.setStatus("READY");

        UserResponseDTO userDto = new UserResponseDTO();
        userDto.setUserAccessPolicy("policy");

        byte[] privateKey = new byte[]{1,2,3};
        byte[] decryptionKey = new byte[]{4,5,6};

        MasterKeys masterKeys = mock(MasterKeys.class);
        when(masterKeys.getPrivateKey()).thenReturn(privateKey);
        when(nativeMasterKeys.masterKeys()).thenReturn(masterKeys);
        when(nativeMasterKeys.policy()).thenReturn(mock(Policy.class));
        when(localStorageService.download(docId)).thenReturn(dbDocumentDTO);
        when(userService.findUserById(userId)).thenReturn(userDto);

        try (MockedStatic<CoverCrypt> coverCryptMockedStatic = mockStatic(CoverCrypt.class)) {
            coverCryptMockedStatic.when(() -> CoverCrypt.generateUserPrivateKey(privateKey, userDto.getUserAccessPolicy(), nativeMasterKeys.policy()))
                    .thenReturn(decryptionKey);
            coverCryptMockedStatic.when(() -> CoverCrypt.decrypt(decryptionKey, dbDocumentDTO.getContent(), Optional.empty()))
                    .thenThrow(new CloudproofException("User decryption key has not the right policy to decrypt this input."));

            assertThrows(AttributeMismatch.class, () -> documentService.retrieveAndDecrypt(docId, userId));
        }
    }
}

