package com.likelion.likelion_BE.domain.user.entity;

import com.likelion.likelion_BE.common.entity.BaseEntity;
import com.likelion.likelion_BE.domain.application.entity.Application;
import com.likelion.likelion_BE.domain.user.enums.Provider;
import com.likelion.likelion_BE.domain.user.enums.Role;
import jakarta.persistence.*;
        import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    // 소셜 로그인 계정은 null (암호화 저장)
    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20)
    private Provider provider;

    @Column(name = "provider_id", length = 255)
    private String providerId;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "major", length = 50)
    private String major;

    @Column(name = "student_id", length = 20)
    private String studentId;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

//    @Builder.Default
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Application> applications = new ArrayList<>();

    // 일반(로컬) 회원가입 생성 메서드
    public static User createLocalUser(
            String email,
            String password,
            String name,
            String major,
            String studentId,
            String phoneNumber
    ) {
        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .role(Role.MEMBER)
                .provider(Provider.LOCAL)
                .major(major)
                .studentId(studentId)
                .phoneNumber(phoneNumber)
                .build();
    }

    // 소셜(구글) 회원가입 생성 메서드
    public static User createSocialUser(
            String email,
            String name,
            String providerId,
            String profileImageUrl
    ) {
        return User.builder()
                .email(email)
                .name(name)
                .role(Role.MEMBER)
                .provider(Provider.GOOGLE)
                .providerId(providerId)
                .profileImageUrl(profileImageUrl)
                .build();
    }

    // 프로필 수정
    public void updateProfile(String name, String major, String studentId, String phoneNumber) {
        this.name = name;
        this.major = major;
        this.studentId = studentId;
        this.phoneNumber = phoneNumber;
    }

    // 역할 변경 (운영진 승인 등)
    public void changeRole(Role role) {
        this.role = role;
    }
}