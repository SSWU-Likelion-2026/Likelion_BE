package com.likelion.likelion_BE.domain.memberprofile.dto.response;

import com.likelion.likelion_BE.domain.memberprofile.entity.MemberProfile;
import com.likelion.likelion_BE.domain.memberprofile.enums.MemberGroup;
import com.likelion.likelion_BE.domain.memberprofile.enums.MemberPosition;
import com.likelion.likelion_BE.domain.memberprofile.enums.MemberType;

public record MemberProfileDetailResponse(
        Long profileId,
        Integer term,
        String name,
        String department,
        String studentId,
        String departmentStudentId,
        MemberGroup memberGroup,
        MemberType memberType,
        MemberPosition position,
        String profileImageUrl,
        String introduction,
        String githubUrl,
        String instagramUrl
) {
    public static MemberProfileDetailResponse from(MemberProfile profile) {
        return new MemberProfileDetailResponse(
                profile.getId(),
                profile.getTerm(),
                profile.getName(),
                profile.getDepartment(),
                profile.getStudentId(),
                profile.getDepartment() + " " + profile.getStudentId(),
                profile.getMemberGroup(),
                profile.getMemberType(),
                profile.getPosition(),
                profile.getProfileImageUrl(),
                profile.getIntroduction(),
                profile.getGithubUrl(),
                profile.getInstagramUrl()
        );
    }
}
