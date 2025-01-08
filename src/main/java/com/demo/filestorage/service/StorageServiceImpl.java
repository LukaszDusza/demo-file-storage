package com.demo.filestorage.service;

import java.nio.ByteBuffer;
import org.springframework.stereotype.Service;

@Service
public class StorageServiceImpl implements StorageService {

  @Override
  public void store(String fileName, ByteBuffer content) {
    //not implemented yet
  }
}
