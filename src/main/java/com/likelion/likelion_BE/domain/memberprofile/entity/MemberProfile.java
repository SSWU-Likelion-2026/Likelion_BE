package com.likelion.likelion_BE.domain.memberprofile.entity;

import com.likelion.likelion_BE.common.entity.BaseEntity;
import com.likelion.likelion_BE.domain.memberprofile.dto.request.MemberProfileCreateRequest;
import com.likelion.likelion_BE.domain.memberprofile.dto.request.MemberProfileUpdateRequest;
import com.likelion.likelion_BE.domain.memberprofile.enums.MemberGroup;
import com.likelion.likelion_BE.domain.memberprofile.enums.MemberPosition;
import com.likelion.likelion_BE.domain.memberprofile.enums.MemberType;
import com.likelion.likelion_BE.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "member_profile",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_member_profile_user_term",
                columnNames = {"user_id", "term"}
        ),
        indexes = @Index(
                name = "idx_member_profile_search",
                columnList = "term, member_group, member_type"
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class MemberProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "term", nullable = false)
    private Integer term;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "department", nullable = false, length = 50)
    private String department;

    @Column(name = "student_id", nullable = false, length = 20)
    private String studentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_group", nullable = false, length = 20)
    private MemberGroup memberGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_type", nullable = false, length = 20)
    private MemberType memberType;

    @Enumerated(EnumType.STRING)
    @Column(name = "position", nullable = false, length = 30)
    private MemberPosition position;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "introduction", length = 50)
    private String introduction;

    @Column(name = "github_url", length = 500)
    private String githubUrl;

    @Column(name = "instagram_url", length = 500)
    private String instagramUrl;

    public static MemberProfile create(User user, MemberProfileCreateRequest request) {
        return MemberProfile.builder()
                .user(user)
                .term(request.term())
                .name(request.name())
                .department(request.department())
                .studentId(request.studentId())
                .memberGroup(request.memberGroup())
                .memberType(request.memberType())
                .position(request.position())
                .profileImageUrl(request.profileImageUrl())
                .introduction(request.introduction())
                .githubUrl(request.githubUrl())
                .instagramUrl(request.instagramUrl())
                .build();
    }

    public void update(MemberProfileUpdateRequest request) {
        if (request.name() != null) this.name = request.name();
        if (request.department() != null) this.department = request.department();
        if (request.studentId() != null) this.studentId = request.studentId();
        if (request.memberGroup() != null) this.memberGroup = request.memberGroup();
        if (request.memberType() != null) this.memberType = request.memberType();
        if (request.position() != null) this.position = request.position();
        if (request.profileImageUrl() != null) this.profileImageUrl = request.profileImageUrl();
        if (request.introduction() != null) this.introduction = request.introduction();
        if (request.githubUrl() != null) this.githubUrl = request.githubUrl();
        if (request.instagramUrl() != null) this.instagramUrl = request.instagramUrl();
    }
}
