package com.demo.filestorage.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.demo.filestorage.model.FileMetadata;
import com.demo.filestorage.repository.FileMetadataRepository;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class FileServiceTest {

  private FileMetadataRepository repository;
  private StorageService storageService;
  private FileService fileService;

  @BeforeEach
  void setUp() {
    repository = mock(FileMetadataRepository.class);
    storageService = mock(StorageService.class);
    fileService = new FileService(repository, storageService);
  }

  @Test
  void testProcessFile() {
    String fileName = "test.txt";
    ByteBuffer fileContent = ByteBuffer.wrap("Sample content".getBytes());

    FileMetadata metadata = new FileMetadata(null, fileName, "expectedChecksum", fileContent.array().length);

    when(repository.save(any(FileMetadata.class))).thenReturn(Mono.just(metadata));

    Mono<FileMetadata> result = fileService.processFile(fileName, fileContent);

    StepVerifier.create(result)
        .expectNextMatches(savedMetadata -> {
          verify(storageService, times(1)).store(eq(fileName), eq(fileContent));
          return savedMetadata.fileName().equals(fileName)
              && savedMetadata.size() == fileContent.array().length;
        })
        .verifyComplete();

    verify(repository, times(1)).save(any(FileMetadata.class));
  }

  @Test
  void testGetAllFiles() {
    FileMetadata file1 = new FileMetadata(1L, "file1.txt", "checksum1", 100L);
    FileMetadata file2 = new FileMetadata(2L, "file2.txt", "checksum2", 200L);

    when(repository.findAll()).thenReturn(Flux.just(file1, file2));

    Flux<FileMetadata> result = fileService.getAllFiles();

    StepVerifier.create(result)
        .expectNext(file1)
        .expectNext(file2)
        .verifyComplete();

    verify(repository, times(1)).findAll();
  }

  @Test
  void testGetFileById() {
    Long fileId = 1L;
    FileMetadata file = new FileMetadata(fileId, "file1.txt", "checksum1", 100L);

    when(repository.findById(fileId)).thenReturn(Mono.just(file));

    Mono<FileMetadata> result = fileService.getFileById(fileId);

    StepVerifier.create(result)
        .expectNext(file)
        .verifyComplete();

    verify(repository, times(1)).findById(fileId);
  }

  @Test
  void testGetFileByName() {
    String fileName = "file1.txt";
    FileMetadata file = new FileMetadata(1L, fileName, "checksum1", 100L);

    when(repository.findByFileName(fileName)).thenReturn(Mono.just(file));

    Mono<FileMetadata> result = fileService.getFileByName(fileName);

    StepVerifier.create(result)
        .expectNext(file)
        .verifyComplete();

    verify(repository, times(1)).findByFileName(fileName);
  }
}