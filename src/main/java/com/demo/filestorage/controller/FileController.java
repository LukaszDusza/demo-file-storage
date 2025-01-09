package com.demo.filestorage.controller;

import com.demo.filestorage.model.FileMetadata;
import com.demo.filestorage.service.FileService;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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
  public Flux<FileMetadata> uploadFiles(@RequestPart("files") Flux<FilePart> files) {
    return files.flatMap(file ->
        DataBufferUtils.join(file.content())
            .flatMap(dataBuffer -> {
              try {
                byte[] fileBytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(fileBytes);
                DataBufferUtils.release(dataBuffer);
                return fileService.processFile(file.filename(), ByteBuffer.wrap(fileBytes));
              } catch (Exception e) {
                return Mono.error(e);
              }
            })
    );
  }

  @PostMapping(value = "/upload/input-stream", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Flux<FileMetadata> uploadFilesInputStreamApproach(@RequestPart("files") Flux<FilePart> files) {
    return files.flatMap(file ->
        DataBufferUtils.join(file.content())
            .flatMap(dataBuffer -> {
              try {
                byte[] fileBytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(fileBytes);
                DataBufferUtils.release(dataBuffer);
                InputStream inputStream = new ByteArrayInputStream(fileBytes);
                return fileService.processFile(file.filename(), inputStream);
              } catch (Exception e) {
                return Mono.error(e);
              }
            })
    );
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
