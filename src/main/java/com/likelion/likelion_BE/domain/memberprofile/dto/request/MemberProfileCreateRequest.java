package com.likelion.likelion_BE.domain.memberprofile.dto.request;

import com.likelion.likelion_BE.domain.memberprofile.enums.MemberGroup;
import com.likelion.likelion_BE.domain.memberprofile.enums.MemberPosition;
import com.likelion.likelion_BE.domain.memberprofile.enums.MemberType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MemberProfileCreateRequest(
        @NotNull(message = "기수는 필수 입력값입니다.")
        @Positive(message = "기수는 1 이상의 양수여야 합니다.")
        Integer term,

        @NotBlank(message = "이름은 필수 입력값입니다.")
        @Size(max = 30, message = "이름은 30자 이하여야 합니다.")
        String name,

        @NotBlank(message = "학과는 필수 입력값입니다.")
        @Size(max = 50, message = "학과는 50자 이하여야 합니다.")
        String department,

        @NotBlank(message = "학번은 필수 입력값입니다.")
        @Size(max = 20, message = "학번은 20자 이하여야 합니다.")
        String studentId,

        @NotNull(message = "파트는 필수 입력값입니다.")
        MemberGroup memberGroup,

        @NotNull(message = "부원 유형은 필수 입력값입니다.")
        MemberType memberType,

        @NotNull(message = "직책은 필수 입력값입니다. 직책이 없으면 NONE을 사용해 주세요.")
        MemberPosition position,

        @Size(max = 500, message = "프로필 이미지 URL은 500자 이하여야 합니다.")
        String profileImageUrl,

        @Size(max = 50, message = "한 줄 소개는 50자 이하여야 합니다.")
        String introduction,

        @Size(max = 500, message = "GitHub URL은 500자 이하여야 합니다.")
        String githubUrl,

        @Size(max = 500, message = "Instagram URL은 500자 이하여야 합니다.")
        String instagramUrl
) {
}
