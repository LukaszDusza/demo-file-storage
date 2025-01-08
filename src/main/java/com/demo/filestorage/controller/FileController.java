package com.demo.filestorage.controller;

import com.demo.filestorage.model.FileMetadata;
import com.demo.filestorage.service.FileService;
import java.nio.ByteBuffer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

  private final FileService fileService;

  public FileController(FileService fileService) {
    this.fileService = fileService;
  }

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Flux<FileMetadata> uploadFiles(@RequestPart("files") Flux<MultipartFile> files) {
    return files.flatMap(file -> {
      try {
        ByteBuffer fileContent = ByteBuffer.wrap(file.getBytes());
        return fileService.processFile(file.getOriginalFilename(), fileContent);
      } catch (Exception e) {
        return Mono.error(e);
      }
    });
  }

  @GetMapping
  public Flux<FileMetadata> getAllFiles() {
    return fileService.getAllFiles();
  }

  @GetMapping("/{id}")
  public Mono<FileMetadata> getFileById(@PathVariable Long id) {
    return fileService.getFileById(id);
  }

  @GetMapping("/by-name")
  public Mono<FileMetadata> getFileByName(@RequestParam String fileName) {
    return fileService.getFileByName(fileName);
  }

}
