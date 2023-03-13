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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/s3")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class AwsS3BucketStorageController {

    @Autowired
    private AwsS3BucketStorageService awsS3BucketStorageService;

    @GetMapping("/{filename}")
    public ResponseEntity<GlobalResponse<String>> generateUrl(@PathVariable String filename) {
        String presignedUrl = awsS3BucketStorageService.generateFileUrl(filename, HttpMethod.GET);

        return new ResponseEntity<>(GlobalResponse.<String>builder()
                .timestamp(LocalDateTime.now())
                .message("Presigned link generated")
                .status(200)
                .data(List.of(presignedUrl))
                .build(), HttpStatus.OK);
    }

    @PostMapping
    @SneakyThrows(IOException.class)
    public ResponseEntity<GlobalResponse<String>> saveFile(@RequestPart("images") List<MultipartFile> images) {
        List<String> uploadedFileS3Keys = awsS3BucketStorageService.uploadFile(images);
        GlobalResponse<String> response = GlobalResponse.<String>builder()
                .timestamp(LocalDateTime.now())
                .message("Images successfully uploaded to S3")
                .status(200)
                .data(uploadedFileS3Keys)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<GlobalResponse<String>>  deleteFile(@PathVariable String filename) {
        String response = awsS3BucketStorageService.deleteFile(filename);
        return new ResponseEntity<>(GlobalResponse.<String>builder()
                .timestamp(LocalDateTime.now())
                .message(response)
                .status(200)
                .data(null)
                .build(), HttpStatus.OK);
    }

}
