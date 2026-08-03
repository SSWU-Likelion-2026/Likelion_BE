package com.likelion.likelion_BE.domain.faq.controller;


import com.likelion.likelion_BE.common.response.ApiResponse;
import com.likelion.likelion_BE.domain.faq.dto.request.FaqRequest;
import com.likelion.likelion_BE.domain.faq.dto.request.FaqUpdateRequest;
import com.likelion.likelion_BE.domain.faq.dto.response.FaqResponse;
import com.likelion.likelion_BE.domain.faq.service.FaqService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/admin/faqs")
@Tag(name = "FAQ Application API", description = "관리자 - FAQ 관련 API")

public class FaqController {

    private final FaqService faqService;

    @Operation(summary = "FAQ 등록", description = "새로운 FAQ를 등록합니다. partId가 null이면 공통 FAQ로 등록됩니다.")
    @PostMapping
    public ApiResponse<List<FaqResponse>> createFaq(@Valid @RequestBody FaqRequest request) {
        return ApiResponse.onSuccess(faqService.createFaq(request));
    }

    @Operation(summary = "FAQ 수정", description = "기존 FAQ의 파트 지정, 질문, 답변을 수정합니다.")
    @PatchMapping("/{faqId}")
    public ApiResponse<FaqResponse> updateFaq(
            @PathVariable Long faqId,
            @Valid @RequestBody FaqUpdateRequest request
    ) {
        return ApiResponse.onSuccess(faqService.updateFaq(faqId, request));
    }

    @Operation(summary = "FAQ 삭제", description = "FAQ를 삭제합니다.")
    @DeleteMapping("/{faqId}")
    public ApiResponse<Void> deleteFaq(@PathVariable Long faqId) {
        faqService.deleteFaq(faqId);
        return ApiResponse.onSuccess(null);
    }
}
