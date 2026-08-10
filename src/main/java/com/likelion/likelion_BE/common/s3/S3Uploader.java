package com.likelion.likelion_BE.common.s3;

import com.likelion.likelion_BE.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final S3Client s3Client;
    private final AppProperties props;

    public String upload(MultipartFile file, String dirName) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String storeFileName = dirName + "/" + UUID.randomUUID() + "_" + originalFilename;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(props.getS3().getBucket())
                    .key(storeFileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            // S3에 업로드된 객체의 URL 반환
            return String.format("https://%s.s3.%s.amazonaws.com/%s",
                    props.getS3().getBucket(),
                    props.getS3().getRegion(),
                    storeFileName);

        } catch (IOException e) {
            throw new RuntimeException("S3 파일 업로드에 실패했습니다.", e);
        }
    }
}
