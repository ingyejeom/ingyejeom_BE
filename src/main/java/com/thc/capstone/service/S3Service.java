package com.thc.capstone.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface S3Service {
    String upload(MultipartFile multipartFile, String storeFileName);
    void delete(String storeFileName);
}
