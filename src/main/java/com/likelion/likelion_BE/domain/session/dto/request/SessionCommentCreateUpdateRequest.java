package com.likelion.likelion_BE.domain.session.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SessionCommentCreateUpdateRequest(
        @NotBlank(message = "후기 내용을 입력해 주세요.")
        String content
) {}