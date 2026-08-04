package com.likelion.likelion_BE.domain.memberprofile.dto.response;

import com.likelion.likelion_BE.domain.memberprofile.entity.MemberProfile;
import com.likelion.likelion_BE.domain.memberprofile.enums.MemberGroup;
import com.likelion.likelion_BE.domain.memberprofile.enums.MemberPosition;
import com.likelion.likelion_BE.domain.memberprofile.enums.MemberType;

public record MemberProfileListResponse(
        Long profileId,
        Integer term,
        String name,
        String departmentStudentId,
        MemberGroup memberGroup,
        MemberType memberType,
        MemberPosition position,
        String profileImageUrl
) {
    public static MemberProfileListResponse from(MemberProfile profile) {
        return new MemberProfileListResponse(
                profile.getId(),
                profile.getTerm(),
                profile.getName(),
                profile.getDepartment() + " " + profile.getStudentId(),
                profile.getMemberGroup(),
                profile.getMemberType(),
                profile.getPosition(),
                profile.getProfileImageUrl()
        );
    }
}
