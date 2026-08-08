package com.likelion.likelion_BE.domain.user.service;

import com.likelion.likelion_BE.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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

    public String upload(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("허용되지 않은 이미지 형식입니다.");
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new IllegalStateException("이미지 업로드 중 오류가 발생했습니다.", e);
        }

        // 실제 바이트가 이미지로 디코딩되는지 검증 (Content-Type 헤더 조작 방지)
        if (!isActuallyImage(bytes)) {
            throw new IllegalArgumentException("허용되지 않은 이미지 형식입니다.");
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
    }

    private boolean isActuallyImage(byte[] bytes) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            return image != null;
        } catch (IOException e) {
            return false;
        }
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

    public void deleteIfOwned(String url) {
        if (url == null || url.isBlank()) {
            return;
        }

        String key = extractOwnedKey(url);
        if (key == null) {
            log.warn("우리 버킷 소유가 아니거나 형식이 다른 URL이라 삭제하지 않음. url={}", url);
            return;
        }

        try {
            s3Client.deleteObject(b -> b.bucket(props.getS3().getBucket()).key(key));
        } catch (Exception e) {
            log.warn("이전 프로필 이미지 삭제 실패 (무시 가능). key={}, error={}", key, e.getMessage());
        }
    }

    // 순수 S3 URL(https://{bucket}.s3.{region}.amazonaws.com/{key}) 형식이고
// 버킷이 우리 버킷과 일치하며 rootPrefix 하위 키일 때만 key를 반환. 그 외(구글 URL 등)는 null.
    private String extractOwnedKey(String url) {
        String bucket = props.getS3().getBucket();
        String expectedHostPrefix = "https://" + bucket + ".s3.";

        if (!url.startsWith(expectedHostPrefix)) {
            return null; // 우리 버킷 URL 형식이 아니면 우리 소유가 아님 (구글 URL 등)
        }

        // https://{bucket}.s3.{region}.amazonaws.com/{key} 에서 {key} 부분만 추출
        int pathStart = url.indexOf('/', "https://".length());
        if (pathStart < 0 || pathStart + 1 >= url.length()) {
            return null;
        }

        String key = url.substring(pathStart + 1);

        String rootPrefix = props.getS3().getRootPrefix();
        if (rootPrefix != null && !rootPrefix.isBlank()) {
            String normalizedPrefix = rootPrefix.endsWith("/") ? rootPrefix : rootPrefix + "/";
            if (!key.startsWith(normalizedPrefix)) {
                return null; // rootPrefix 하위가 아니면 안전하게 스킵
            }
        }

        return key;
    }
}