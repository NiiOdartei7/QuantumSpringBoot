package com.example.quantumspringboot;

import com.example.quantumspringboot.dto.DBDocumentDTO;
import com.example.quantumspringboot.entity.Document;
import com.example.quantumspringboot.repository.DocumentRepository;
import com.example.quantumspringboot.service.LocalStorageService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocalStorageServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    private LocalStorageService localStorageService;

    private final Path uploadsDir = Paths.get("uploads");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        localStorageService = new LocalStorageService(documentRepository);

        // inject @Value field manually
        ReflectionTestUtils.setField(
                localStorageService,
                "uploadDiriector",
                "uploads"
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(uploadsDir)) {
            Files.walk(uploadsDir)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException ignored) {}
                    });
        }
    }

    // ---------------- SAVE ----------------

    @Test
    void save_success() throws IOException {
        byte[] data = "hello".getBytes();
        String filename = "test.txt";

        String result = localStorageService.save(data, filename);

        assertEquals("/uploads/" + filename, result);
        assertTrue(Files.exists(uploadsDir.resolve(filename)));
    }

    @Test
    void save_fileAlreadyExists() throws IOException {
        String filename = "duplicate.txt";
        Files.createDirectories(uploadsDir);
        Files.createFile(uploadsDir.resolve(filename));

        assertThrows(RuntimeException.class,
                () -> localStorageService.save("data".getBytes(), filename));
    }

    // ---------------- DOWNLOAD ----------------

    @Test
    void download_success() throws IOException {
        UUID docId = UUID.randomUUID();
        String filename = "doc.txt";
        byte[] content = "secret".getBytes();

        // ⚠️ MUST create uploads/uploads
        Path nestedUploads = Paths.get("uploads", "uploads");
        Files.createDirectories(nestedUploads);

        Files.write(nestedUploads.resolve(filename), content);

        Document document = new Document();
        document.setId(docId);
        document.setFilename(filename);
        document.setStatus("READY");
        document.setUrl("/uploads/" + filename);

        when(documentRepository.existsDocumentById(docId))
                .thenReturn(true);
        when(documentRepository.findDocumentById(docId))
                .thenReturn(document);

        DBDocumentDTO result = localStorageService.download(docId);

        assertNotNull(result);
        assertArrayEquals(content, result.getContent());
        assertEquals(filename, result.getFilename());
    }


    @Test
    void download_documentDoesNotExist() {
        UUID docId = UUID.randomUUID();

        when(documentRepository.existsDocumentById(docId))
                .thenReturn(false);

        assertThrows(FileNotFoundException.class,
                () -> localStorageService.download(docId));
    }

    @Test
    void download_fileMissingOnDisk() {
        UUID docId = UUID.randomUUID();

        Document document = new Document();
        document.setId(docId);
        document.setFilename("missing.txt");
        document.setUrl("/uploads/missing.txt");

        when(documentRepository.existsDocumentById(docId))
                .thenReturn(true);
        when(documentRepository.findDocumentById(docId))
                .thenReturn(document);

        assertThrows(RuntimeException.class,
                () -> localStorageService.download(docId));
    }
}

