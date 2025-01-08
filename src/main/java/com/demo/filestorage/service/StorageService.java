package com.demo.filestorage.service;

import java.nio.ByteBuffer;

public interface StorageService {

  void store(String fileName, ByteBuffer content);

}
