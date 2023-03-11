package com.example.productservice.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.productservice.dto.GlobalResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsS3BucketStorageService {

    @Autowired
    private AmazonS3 AwsS3Client;

    @Value("${s3.bucket.name}")
    private String s3BucketName;

    public List<String> listFiles() throws AmazonClientException {
        List<String> keys = new ArrayList<>();
        ObjectListing objectListing = AwsS3Client.listObjects(s3BucketName);

        while (true) {
            List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
            if (objectSummaries.isEmpty()) {
                break;
            }

            objectSummaries.stream()
                    .map(S3ObjectSummary::getKey)
                    .filter(key -> !key.endsWith("/"))
                    .forEach(keys::add);

            objectListing = AwsS3Client.listNextBatchOfObjects(objectListing);
        }

        log.info("Files found in bucket({}): {}", s3BucketName, keys);
        return keys;
    }

    @Async
    public ResponseEntity<GlobalResponse> generateFileUrl(String fileName, HttpMethod httpMethod) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 1);
        String presignedUrl =  AwsS3Client.generatePresignedUrl(s3BucketName, fileName, calendar.getTime(), httpMethod).toString();
        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Files uploaded to bucket " + s3BucketName)
                .status(200)
                .data(List.of(presignedUrl))
                .build(), HttpStatus.OK);

    }

    public ResponseEntity<GlobalResponse> uploadFile(List<MultipartFile> files ) throws AmazonClientException, IOException {
        List<String> uploadedFiles = new ArrayList<>();
        for(MultipartFile file: files) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            log.info("Uploading file to bucket({}): {}", s3BucketName, file.getOriginalFilename());
            AwsS3Client.putObject(s3BucketName, file.getOriginalFilename(), file.getInputStream(), metadata);
            uploadedFiles.add(file.getOriginalFilename());
        }

        log.info("Files uploaded to bucket({})", s3BucketName);
        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Files uploaded to bucket " + s3BucketName)
                .status(200)
                .data(uploadedFiles)
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<GlobalResponse> deleteFile(String filename) {

        if (AwsS3Client.doesObjectExist(s3BucketName, filename)){
            AwsS3Client.deleteObject(s3BucketName,filename);
            log.info("File deleted from bucket({}): {}", s3BucketName, filename);
            return new ResponseEntity<>(GlobalResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .message("File " + filename + " successfully deleted.")
                    .status(200)
                    .build(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("File " + filename + " does not exist, aborting deletion.")
                .status(200)
                .build(), HttpStatus.OK);
        }
    }
}
