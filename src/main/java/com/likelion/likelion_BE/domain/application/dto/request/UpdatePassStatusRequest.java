package com.likelion.likelion_BE.domain.application.dto.request;

import com.likelion.likelion_BE.domain.application.enums.PassStatus;
import jakarta.validation.constraints.NotNull;

public record UpdatePassStatusRequest (
        @NotNull(message = "합불 상태값은 필수입니다.")
        PassStatus passStatus
){
}
