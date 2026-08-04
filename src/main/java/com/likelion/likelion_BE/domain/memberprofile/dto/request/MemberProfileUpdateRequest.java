package com.likelion.likelion_BE.domain.memberprofile.dto.request;

import com.likelion.likelion_BE.domain.memberprofile.enums.MemberGroup;
import com.likelion.likelion_BE.domain.memberprofile.enums.MemberPosition;
import com.likelion.likelion_BE.domain.memberprofile.enums.MemberType;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberProfileUpdateRequest(
        @Size(min = 1, max = 30, message = "이름은 1자 이상 30자 이하여야 합니다.")
        @Pattern(regexp = ".*\\S.*", message = "이름은 공백으로만 구성할 수 없습니다.")
        String name,

        @Size(min = 1, max = 50, message = "학과는 1자 이상 50자 이하여야 합니다.")
        @Pattern(regexp = ".*\\S.*", message = "학과는 공백으로만 구성할 수 없습니다.")
        String department,

        @Size(min = 1, max = 20, message = "학번은 1자 이상 20자 이하여야 합니다.")
        @Pattern(regexp = ".*\\S.*", message = "학번은 공백으로만 구성할 수 없습니다.")
        String studentId,

        MemberGroup memberGroup,
        MemberType memberType,
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
