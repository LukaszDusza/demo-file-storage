package com.demo.filestorage.service;

import com.demo.filestorage.model.FileMetadata;
import com.demo.filestorage.repository.FileMetadataRepository;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FileService {

  private final FileMetadataRepository repository;
  private final StorageService storageService;
  private static final Logger logger = LoggerFactory.getLogger(FileService.class);

  public FileService(FileMetadataRepository repository, StorageService storageService) {
    this.repository = repository;
    this.storageService = storageService;
  }


  public Mono<FileMetadata> processFile(String fileName, ByteBuffer fileContent) {
    logger.info("Processing file: {}", fileName);
    try {
      logger.debug("Calculating checksum for file: {}", fileName);
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] checksum = digest.digest(fileContent.array());
      String checksumHex = bytesToHex(checksum);
      long size = fileContent.remaining();
      FileMetadata metadata = new FileMetadata(null, fileName, checksumHex, size);
      storageService.store(fileName, fileContent);
      logger.info("Storing metadata for file: {}", fileName);
      return repository.save(metadata)
          .doOnSuccess(savedMetadata -> logger.info("Successfully saved metadata for file: {}", fileName))
          .doOnError(error -> logger.error("Error saving metadata for file: {}", fileName, error));
    } catch (NoSuchAlgorithmException e) {
      logger.error("Error processing file: {}", fileName, e);
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
