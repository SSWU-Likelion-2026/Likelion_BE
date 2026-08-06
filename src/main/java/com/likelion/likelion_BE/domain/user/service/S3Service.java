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
import java.util.Set;
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

    // 구글 프로필 이미지가 실제로 서빙되는 호스트만 허용 (SSRF 방지)
    private static final Set<String> ALLOWED_IMAGE_HOSTS = Set.of(
            "lh3.googleusercontent.com",
            "lh4.googleusercontent.com",
            "lh5.googleusercontent.com",
            "lh6.googleusercontent.com"
    );

    // 저장을 허용할 Content-Type만 화이트리스트로 관리 (svg/html 등 실행형 콘텐츠 차단)
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/webp",
            "image/gif"
    );

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

        if (!isAllowedImageUrl(imageUrl)) {
            log.warn("허용되지 않은 이미지 호스트. url={}", imageUrl);
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
                    .map(String::toLowerCase)
                    .orElse("image/jpeg");

            if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
                log.warn("허용되지 않은 이미지 Content-Type. contentType={}, url={}", contentType, imageUrl);
                return null;
            }

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

    private boolean isAllowedImageUrl(String imageUrl) {
        try {
            URI uri = URI.create(imageUrl);
            return "https".equalsIgnoreCase(uri.getScheme())
                    && uri.getHost() != null
                    && ALLOWED_IMAGE_HOSTS.contains(uri.getHost().toLowerCase());
        } catch (Exception e) {
            return false;
        }
    }

    private String buildKey(String extension) {
        String prefix = props.getS3().getRootPrefix();
        if (prefix == null || prefix.isBlank()) {
            return UUID.randomUUID() + extension;
        }
        String normalized = prefix.endsWith("/") ? prefix : prefix + "/";
        return normalized + UUID.randomUUID() + extension;
    }

    private String resolveExtension(String contentType) {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> ".jpg"; // image/jpeg
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