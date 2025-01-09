package com.demo.filestorage.service;

import java.io.InputStream;
import java.nio.ByteBuffer;

public interface StorageService {

  void store(String fileName, ByteBuffer content);

  void store(String fileName, InputStream content);

}
