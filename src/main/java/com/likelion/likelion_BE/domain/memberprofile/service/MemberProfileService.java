package com.likelion.likelion_BE.domain.memberprofile.service;

import com.likelion.likelion_BE.common.exception.CustomException;
import com.likelion.likelion_BE.domain.memberprofile.dto.request.MemberProfileCreateRequest;
import com.likelion.likelion_BE.domain.memberprofile.dto.request.MemberProfileUpdateRequest;
import com.likelion.likelion_BE.domain.memberprofile.dto.response.MemberProfileDetailResponse;
import com.likelion.likelion_BE.domain.memberprofile.dto.response.MemberProfileListResponse;
import com.likelion.likelion_BE.domain.memberprofile.entity.MemberProfile;
import com.likelion.likelion_BE.domain.memberprofile.enums.MemberGroup;
import com.likelion.likelion_BE.domain.memberprofile.enums.MemberType;
import com.likelion.likelion_BE.domain.memberprofile.exception.MemberProfileErrorCode;
import com.likelion.likelion_BE.domain.memberprofile.repository.MemberProfileRepository;
import com.likelion.likelion_BE.domain.user.entity.User;
import com.likelion.likelion_BE.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberProfileService {

    private final MemberProfileRepository memberProfileRepository;
    private final UserRepository userRepository;

    public List<MemberProfileListResponse> getProfiles(
            Integer term,
            MemberGroup memberGroup,
            MemberType memberType
    ) {
        return memberProfileRepository.findAllByCondition(term, memberGroup, memberType)
                .stream()
                .map(MemberProfileListResponse::from)
                .toList();
    }

    public MemberProfileDetailResponse getProfile(Long profileId) {
        return MemberProfileDetailResponse.from(findProfile(profileId));
    }

    @Transactional
    public MemberProfileDetailResponse createMyProfile(Principal principal, MemberProfileCreateRequest request) {
        User user = getCurrentUser(principal);
        Long userId = user.getId();
        if (memberProfileRepository.existsByUserIdAndTerm(userId, request.term())) {
            throw new CustomException(MemberProfileErrorCode.MEMBER_PROFILE_ALREADY_EXISTS);
        }

        MemberProfile savedProfile = memberProfileRepository.save(MemberProfile.create(user, request));
        return MemberProfileDetailResponse.from(savedProfile);
    }

    public MemberProfileDetailResponse getMyProfile(Principal principal, Integer term) {
        return MemberProfileDetailResponse.from(findMyProfile(getCurrentUser(principal).getId(), term));
    }

    @Transactional
    public MemberProfileDetailResponse updateMyProfile(
            Principal principal,
            Integer term,
            MemberProfileUpdateRequest request
    ) {
        MemberProfile profile = findMyProfile(getCurrentUser(principal).getId(), term);
        profile.update(request);
        return MemberProfileDetailResponse.from(profile);
    }

    @Transactional
    public void deleteMyProfile(Principal principal, Integer term) {
        memberProfileRepository.delete(findMyProfile(getCurrentUser(principal).getId(), term));
    }

    private MemberProfile findProfile(Long profileId) {
        return memberProfileRepository.findById(profileId)
                .orElseThrow(() -> new CustomException(MemberProfileErrorCode.MEMBER_PROFILE_NOT_FOUND));
    }

    private MemberProfile findMyProfile(Long userId, Integer term) {
        return memberProfileRepository.findByUserIdAndTerm(userId, term)
                .orElseThrow(() -> new CustomException(MemberProfileErrorCode.MEMBER_PROFILE_NOT_FOUND));
    }

    private User getCurrentUser(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new CustomException(MemberProfileErrorCode.AUTHENTICATION_REQUIRED);
        }
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException(MemberProfileErrorCode.USER_NOT_FOUND));
    }
}
