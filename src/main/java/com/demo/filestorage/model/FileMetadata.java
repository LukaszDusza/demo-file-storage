package com.demo.filestorage.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("files")
public record FileMetadata(
    @Id Long id,
    String fileName,
    String checksum,
    long size
) {

}
