package com.demo.filestorage.service;

import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StorageServiceImpl implements StorageService {

  private static final Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);

  @Override
  public void store(String fileName, ByteBuffer fileContent) {
    logger.info("Storing file locally: {}", fileName);

    //logic

    logger.info("Successfully stored file: {}", fileName);
  }
}
