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
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private static final long MAX_DECODED_PIXELS = 20_000_000L; // 약 20MP, 예: 5000x4000. 정책값 확인 필요

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
        try (ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
            if (iis == null) {
                return false;
            }

            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (!readers.hasNext()) {
                return false; // 이미지 포맷 자체를 인식 못함
            }

            ImageReader reader = readers.next();
            try {
                reader.setInput(iis, true, true); // 헤더만 읽음, 픽셀 디코딩 안 함
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);

                long pixelCount = (long) width * height;
                if (pixelCount > MAX_DECODED_PIXELS) {
                    log.warn("이미지 픽셀 수 초과. width={}, height={}, pixels={}", width, height, pixelCount);
                    return false;
                }

                return true;
            } finally {
                reader.dispose();
            }
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

    private String extractOwnedKey(String url) {
        String key = extractKeyFromCloudFront(url);
        if (key == null) {
            key = extractKeyFromS3Url(url);
        }
        if (key == null) {
            return null; // 우리 소유 URL 형식이 아님 (구글 URL 등)
        }

        String rootPrefix = props.getS3().getRootPrefix();
        if (rootPrefix != null && !rootPrefix.isBlank()) {
            String normalizedPrefix = rootPrefix.endsWith("/") ? rootPrefix : rootPrefix + "/";
            if (!key.startsWith(normalizedPrefix)) {
                return null; // rootPrefix 하위가 아니면 안전하게 스킵
            }
        }

        return key;
    }

    private String extractKeyFromCloudFront(String url) {
        String cf = props.getS3().getCloudfrontDomain();
        if (cf == null || cf.isBlank()) {
            return null;
        }

        URI uri = parseStrict(url);
        if (uri == null) {
            return null;
        }

        if (!cf.equalsIgnoreCase(uri.getHost())) {
            return null;
        }

        return extractPath(uri);
    }

    private String extractKeyFromS3Url(String url) {
        URI uri = parseStrict(url);
        if (uri == null) {
            return null;
        }

        String bucket = props.getS3().getBucket();
        String region = props.getS3().getRegion();
        String expectedHost = bucket + ".s3." + region + ".amazonaws.com";

        if (!expectedHost.equalsIgnoreCase(uri.getHost())) {
            return null; // 호스트가 정확히 일치하지 않으면 우리 버킷이 아님
        }

        return extractPath(uri);
    }

    private URI parseStrict(String url) {
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            return null;
        }

        if (!"https".equalsIgnoreCase(uri.getScheme())) {
            return null;
        }

        if (uri.getRawQuery() != null || uri.getRawFragment() != null) {
            return null; // 우리가 생성한 URL엔 query/fragment가 없음 → 조작 의심
        }

        return uri;
    }

    private String extractPath(URI uri) {
        String path = uri.getRawPath(); // "/{key}"
        if (path == null || path.length() < 2) {
            return null;
        }
        return path.substring(1); // 맨 앞 "/" 제거
    }
}