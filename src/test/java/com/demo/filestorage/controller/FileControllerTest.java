package com.demo.filestorage.controller;

import com.demo.filestorage.model.FileMetadata;
import com.demo.filestorage.repository.FileMetadataRepository;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureWebTestClient(timeout = "128000")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FileControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private FileMetadataRepository repository;

  @BeforeEach
  void setUp() {
    repository.deleteAll().block();
  }

  @Test
  void testUploadFiles() {
    MultiValueMap<String, Object> body = createMultipartBody("test.txt", "Sample content");

    webTestClient.post()
        .uri("/api/v1/files/upload")
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .bodyValue(body)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(FileMetadata.class)
        .hasSize(1)
        .value(response -> {
          assert response.getFirst().fileName().equals("test.txt");
        });

    var allFiles = repository.findAll().collectList().block();
    assert allFiles != null;
    assert allFiles.size() == 1;
    assert allFiles.getFirst().fileName().equals("test.txt");
  }


  @Test
  void testGetAllFiles() {
    webTestClient.post()
        .uri("/api/v1/files/upload")
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .bodyValue(createMultipartBody("file1.txt", "content1"))
        .exchange()
        .expectStatus().isOk();

    webTestClient.post()
        .uri("/api/v1/files/upload")
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .bodyValue(createMultipartBody("file2.txt", "content2"))
        .exchange()
        .expectStatus().isOk();

    Flux<FileMetadata> result = webTestClient.get()
        .uri("/api/v1/files")
        .exchange()
        .expectStatus().isOk()
        .returnResult(FileMetadata.class)
        .getResponseBody();

    StepVerifier.create(result)
        .expectNextMatches(metadata -> metadata.fileName().equals("file1.txt") && metadata.size() == "content1".length())
        .expectNextMatches(metadata -> metadata.fileName().equals("file2.txt") && metadata.size() == "content2".length())
        .verifyComplete();
  }


  @Test
  void testGetFileById() {
    webTestClient.post()
        .uri("/api/v1/files/upload")
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .bodyValue(createMultipartBody("file1.txt", "content1"))
        .exchange()
        .expectStatus().isOk();

    var file = repository.findAll().blockFirst();

    webTestClient.get()
        .uri("/api/v1/files/" + file.id())
        .exchange()
        .expectStatus().isOk()
        .expectBody(FileMetadata.class)
        .value(response -> {
          assert response.fileName().equals("file1.txt");
        });
  }

  @Test
  void testGetFileByName() {
    webTestClient.post()
        .uri("/api/v1/files/upload")
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .bodyValue(createMultipartBody("file1.txt", "content1"))
        .exchange()
        .expectStatus().isOk();

    webTestClient.get()
        .uri(uriBuilder -> uriBuilder.path("/api/v1/files/by-name")
            .queryParam("fileName", "file1.txt")
            .build())
        .exchange()
        .expectStatus().isOk()
        .expectBody(FileMetadata.class)
        .value(response -> {
          assert response.fileName().equals("file1.txt");
        });
  }

  @Test
  void testLargeFileUpload() throws IOException {
    File largeFile = generateLargeFile("large_test_file.txt", 100 * 1024 * 1024); // 100 MB

    try {
      MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
      body.add("files", new FileSystemResource(largeFile));

      // 1. Przesyłanie dużego pliku
      webTestClient.post()
          .uri("/api/v1/files/upload")
          .contentType(MediaType.MULTIPART_FORM_DATA)
          .bodyValue(body)
          .exchange()
          .expectStatus().isOk()
          .expectBodyList(FileMetadata.class)
          .hasSize(1)
          .value(response -> {
            assert response.getFirst().fileName().equals("large_test_file.txt");
            assert response.getFirst().size() == largeFile.length();
          });

      // 2. Weryfikacja pliku w bazie przez API
      webTestClient.get()
          .uri(uriBuilder -> uriBuilder
              .path("/api/v1/files/by-name")
              .queryParam("fileName", "large_test_file.txt")
              .build())
          .exchange()
          .expectStatus().isOk()
          .expectBody(FileMetadata.class)
          .value(metadata -> {
            assert metadata.fileName().equals("large_test_file.txt");
            assert metadata.size() == largeFile.length();
          });
    } finally {
      if (largeFile.exists()) {
        largeFile.delete();
      }
    }
  }

  @Test
  void testLargeFileUploadInputStreamApproach() throws IOException {
    File largeFile = generateLargeFile("large_test_file.txt", 100 * 1024 * 1024); // 100 MB

    try {
      MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
      body.add("files", new FileSystemResource(largeFile));

      // 1. Przesyłanie dużego pliku
      webTestClient.post()
          .uri("/api/v1/files/upload/input-stream")
          .contentType(MediaType.MULTIPART_FORM_DATA)
          .bodyValue(body)
          .exchange()
          .expectStatus().isOk()
          .expectBodyList(FileMetadata.class)
          .hasSize(1)
          .value(response -> {
            assert response.getFirst().fileName().equals("large_test_file.txt");
            assert response.getFirst().size() == largeFile.length();
          });

      // 2. Weryfikacja pliku w bazie przez API
      webTestClient.get()
          .uri(uriBuilder -> uriBuilder
              .path("/api/v1/files/by-name")
              .queryParam("fileName", "large_test_file.txt")
              .build())
          .exchange()
          .expectStatus().isOk()
          .expectBody(FileMetadata.class)
          .value(metadata -> {
            assert metadata.fileName().equals("large_test_file.txt");
            assert metadata.size() == largeFile.length();
          });
    } finally {
      if (largeFile.exists()) {
        largeFile.delete();
      }
    }
  }

  private MultiValueMap<String, Object> createMultipartBody(String fileName, String content) {
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("files", new ByteArrayResource(content.getBytes()) {
      @Override
      public String getFilename() {
        return fileName;
      }
    });
    return body;
  }

  private File generateLargeFile(String fileName, int sizeInBytes) throws IOException {
    File file = new File(fileName);
    try (FileOutputStream fos = new FileOutputStream(file)) {
      byte[] buffer = "A".repeat(1024).getBytes(StandardCharsets.UTF_8);
      int blocks = sizeInBytes / buffer.length;
      for (int i = 0; i < blocks; i++) {
        fos.write(buffer);
      }
    }
    return file;
  }
}