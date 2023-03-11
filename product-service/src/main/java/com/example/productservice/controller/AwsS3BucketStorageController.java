package com.example.productservice.controller;

import com.amazonaws.HttpMethod;
import com.example.productservice.dto.GlobalResponse;
import com.example.productservice.service.AwsS3BucketStorageService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/s3")
@AllArgsConstructor
public class AwsS3BucketStorageController {

    @Autowired
    private AwsS3BucketStorageService awsS3BucketStorageService;

    @GetMapping("/{filename}")
    public ResponseEntity<GlobalResponse> generateUrl(@PathVariable String filename) {
        return awsS3BucketStorageService.generateFileUrl(filename, HttpMethod.GET);
    }

    @PostMapping
    @SneakyThrows(IOException.class)
    public ResponseEntity<GlobalResponse> saveFile(@RequestPart("file") List<MultipartFile> multipartFiles) {
        return awsS3BucketStorageService.uploadFile(multipartFiles);
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<GlobalResponse>  deleteFile(@PathVariable String filename) {
        return awsS3BucketStorageService.deleteFile(filename);
    }

}
