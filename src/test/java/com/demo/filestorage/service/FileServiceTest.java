package com.demo.filestorage.service;

import com.demo.filestorage.model.FileMetadata;
import com.demo.filestorage.repository.FileMetadataRepository;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
class FileServiceTest {

  @Autowired
  private FileMetadataRepository repository;

  @Autowired
  private FileService fileService;


  @BeforeEach
  void setUp() {
    repository.deleteAll().block(); // Czyszczenie bazy przed każdym testem
  }

  @Test
  void testProcessFile() throws IOException, NoSuchAlgorithmException {
    String fileName = "test.txt";
    InputStream fileContent = new ByteArrayInputStream("Sample content".getBytes(StandardCharsets.UTF_8));

    // Oczekiwane dane
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] expectedChecksum = digest.digest("Sample content".getBytes(StandardCharsets.UTF_8));
    String expectedChecksumHex = bytesToHex(expectedChecksum);

    // Wywołanie metody
    Mono<FileMetadata> result = fileService.processFile(fileName, fileContent);

    // Weryfikacja
    StepVerifier.create(result)
        .assertNext(metadata -> {
          assert metadata.fileName().equals(fileName);
          assert metadata.size() == "Sample content".length();
          assert metadata.checksum().equals(expectedChecksumHex);
        })
        .verifyComplete();

    // Sprawdzenie zapisu w bazie
    StepVerifier.create(repository.findByFileName(fileName))
        .assertNext(metadata -> {
          assert metadata.fileName().equals(fileName);
          assert metadata.size() == "Sample content".length();
          assert metadata.checksum().equals(expectedChecksumHex);
        })
        .verifyComplete();
  }

  @Test
  void testGetAllFiles() {
    FileMetadata file1 = new FileMetadata(null, "file1.txt", "checksum1", 100L);
    FileMetadata file2 = new FileMetadata(null, "file2.txt", "checksum2", 200L);

    repository.save(file1).block();
    repository.save(file2).block();

    Flux<FileMetadata> result = fileService.getAllFiles();

    StepVerifier.create(result)
        .expectNextMatches(metadata -> metadata.fileName().equals("file1.txt") &&
            metadata.checksum().equals("checksum1") &&
            metadata.size() == 100L)
        .expectNextMatches(metadata -> metadata.fileName().equals("file2.txt") &&
            metadata.checksum().equals("checksum2") &&
            metadata.size() == 200L)
        .verifyComplete();
  }

  @Test
  void testGetFileById() {
    FileMetadata file = repository.save(new FileMetadata(null, "file1.txt", "checksum1", 100L)).block();

    Mono<FileMetadata> result = fileService.getFileById(file.id());

    StepVerifier.create(result)
        .expectNext(file)
        .verifyComplete();
  }

  @Test
  void testGetFileByName() {
    FileMetadata file = repository.save(new FileMetadata(null, "file1.txt", "checksum1", 100L)).block();

    Mono<FileMetadata> result = fileService.getFileByName("file1.txt");

    StepVerifier.create(result)
        .expectNext(file)
        .verifyComplete();
  }

  @Test
  void testProcessLargeFile() throws IOException, NoSuchAlgorithmException {
    String largeFileContent = "A".repeat(100 * 1024 * 1024); // 100 MB danych
    InputStream largeFileStream = new ByteArrayInputStream(largeFileContent.getBytes(StandardCharsets.UTF_8));

    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] expectedChecksum = digest.digest(largeFileContent.getBytes(StandardCharsets.UTF_8));
    String expectedChecksumHex = bytesToHex(expectedChecksum);

    Mono<FileMetadata> result = fileService.processFile("large_test_file.txt", largeFileStream);

    StepVerifier.create(result)
        .assertNext(metadata -> {
          assert metadata.fileName().equals("large_test_file.txt");
          assert metadata.size() == largeFileContent.length();
          assert metadata.checksum().equals(expectedChecksumHex);
        })
        .verifyComplete();

    // Sprawdzenie zapisu w bazie
    StepVerifier.create(repository.findByFileName("large_test_file.txt"))
        .assertNext(metadata -> {
          assert metadata.fileName().equals("large_test_file.txt");
          assert metadata.size() == largeFileContent.length();
          assert metadata.checksum().equals(expectedChecksumHex);
        })
        .verifyComplete();
  }

  private String bytesToHex(byte[] bytes) {
    StringBuilder hexString = new StringBuilder();
    for (byte b : bytes) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }

}
