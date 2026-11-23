package com.likelion.likelion_BE.common.s3;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;

public final class FileValidator {

    private static final Set<String> IMAGE_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");
    private static final Set<String> IMAGE_EXTENSIONS =
            Set.of("jpg", "jpeg", "png", "webp");

    private static final Set<String> PDF_TYPES = Set.of("application/pdf");
    private static final Set<String> PDF_EXTENSIONS = Set.of("pdf");

    private FileValidator() {}

    public static void validateImage(MultipartFile file, long maxBytes) {
        validate(file, IMAGE_TYPES, IMAGE_EXTENSIONS, maxBytes, "이미지");
    }

    public static void validatePdf(MultipartFile file, long maxBytes) {
        validate(file, PDF_TYPES, PDF_EXTENSIONS, maxBytes, "PDF 파일");
        validatePdfSignature(file);
    }

    private static void validate(MultipartFile file, Set<String> types,
                                 Set<String> extensions, long maxBytes, String label) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(label + "이(가) 비어있습니다.");
        }
        if (file.getSize() > maxBytes) {
            throw new IllegalArgumentException(
                    label + " 크기는 " + (maxBytes / 1024 / 1024) + "MB 이하여야 합니다.");
        }

        String contentType = file.getContentType() == null
                ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if (!types.contains(contentType)) {
            throw new IllegalArgumentException("허용되지 않는 " + label + " 형식입니다.");
        }

        String name = file.getOriginalFilename();
        int dot = (name == null) ? -1 : name.lastIndexOf('.');
        String ext = (dot < 0) ? "" : name.substring(dot + 1).toLowerCase(Locale.ROOT);
        if (!extensions.contains(ext)) {
            throw new IllegalArgumentException("허용되지 않는 " + label + " 확장자입니다.");
        }
    }

    // 진짜 PDF인지 시그니처로 확인
    private static void validatePdfSignature(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            byte[] header = in.readNBytes(5);
            String head = new String(header, StandardCharsets.US_ASCII);
            if (!"%PDF-".equals(head)) {
                throw new IllegalArgumentException("올바른 PDF 파일이 아닙니다.");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 읽을 수 없습니다.");
        }
    }
}