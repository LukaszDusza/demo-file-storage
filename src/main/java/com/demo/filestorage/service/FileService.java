package com.demo.filestorage.service;

import com.demo.filestorage.model.FileMetadata;
import com.demo.filestorage.repository.FileMetadataRepository;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FileService {

  private final FileMetadataRepository repository;
  private final StorageService storageService;

  public FileService(FileMetadataRepository repository, StorageService storageService) {
    this.repository = repository;
    this.storageService = storageService;
  }


  public Mono<FileMetadata> processFile(String fileName, ByteBuffer fileContent) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] checksum = digest.digest(fileContent.array());
      StringBuilder checksumHex = new StringBuilder();
      for (byte b : checksum) {
        checksumHex.append(String.format("%02x", b));
      }
      long size = fileContent.array().length;
      FileMetadata metadata = new FileMetadata(null, fileName, checksumHex.toString(), size);
      storageService.store(fileName, fileContent);
      return repository.save(metadata);
    } catch (NoSuchAlgorithmException e) {
      return Mono.error(e);
    }
  }

  public Flux<FileMetadata> getAllFiles() {
    return repository.findAll();
  }

  public Mono<FileMetadata> getFileById(Long id) {
    return repository.findById(id);
  }

  public Mono<FileMetadata> getFileByName(String fileName) {
    return repository.findByFileName(fileName);
  }
}
