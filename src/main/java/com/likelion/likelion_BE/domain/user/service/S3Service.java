package com.likelion.likelion_BE.domain.user.service;

import com.likelion.likelion_BE.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final AppProperties props;

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public String uploadIcon(byte[] bytes) {
        String key = buildKey(".png");

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(props.getS3().getBucket())
                        .key(key)
                        .contentType("image/png")
                        .build(),
                RequestBody.fromBytes(bytes)
        );

        return toPublicUrl(key);
    }

    public String uploadFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(imageUrl))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<byte[]> response = HTTP_CLIENT.send(
                    request,
                    HttpResponse.BodyHandlers.ofByteArray()
            );

            if (response.statusCode() != 200) {
                log.warn("프로필 이미지 다운로드 실패. status={}, url={}", response.statusCode(), imageUrl);
                return null;
            }

            byte[] bytes = response.body();
            if (bytes == null || bytes.length == 0) {
                log.warn("프로필 이미지 응답이 비어있음. url={}", imageUrl);
                return null;
            }

            String contentType = response.headers()
                    .firstValue("Content-Type")
                    .orElse("image/jpeg");

            String key = buildKey(resolveExtension(contentType));

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(props.getS3().getBucket())
                            .key(key)
                            .contentType(contentType)
                            .build(),
                    RequestBody.fromBytes(bytes)
            );

            return toPublicUrl(key);

        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            log.warn("프로필 이미지 업로드 중 오류 발생. url={}, error={}", imageUrl, e.getMessage());
            return null;
        }
    }

    private String buildKey(String extension) {
        String prefix = props.getS3().getRootPrefix();
        if (prefix == null || prefix.isBlank()) {
            return UUID.randomUUID() + extension;
        }
        // yml에 "profile-images/" 처럼 슬래시를 안 붙여도 안전하게 붙여준다
        String normalized = prefix.endsWith("/") ? prefix : prefix + "/";
        return normalized + UUID.randomUUID() + extension;
    }

    private String resolveExtension(String contentType) {
        if (contentType == null) {
            return ".jpg";
        }
        return switch (contentType.toLowerCase()) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> ".jpg";
        };
    }

    private String toPublicUrl(String key) {
        String cf = props.getS3().getCloudfrontDomain();
        if (cf != null && !cf.isBlank()) {
            return "https://" + cf + "/" + key;
        }
        return s3Client.utilities()
                .getUrl(b -> b.bucket(props.getS3().getBucket()).key(key))
                .toString();
    }
}