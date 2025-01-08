package com.demo.filestorage.repository;


import com.demo.filestorage.model.FileMetadata;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface FileMetadataRepository extends ReactiveCrudRepository<FileMetadata, Long> {

  Mono<FileMetadata> findByFileName(String fileName);

}
