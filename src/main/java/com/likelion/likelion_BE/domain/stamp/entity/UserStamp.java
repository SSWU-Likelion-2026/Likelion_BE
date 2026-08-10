package com.likelion.likelion_BE.domain.stamp.entity;


import com.likelion.likelion_BE.common.entity.BaseEntity;
import com.likelion.likelion_BE.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "user_stamp",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_stamp_user_mission",
                        columnNames = {"user_id", "mission_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class UserStamp extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "auth_image_url", length = 500)
    private String authImageUrl;

    @Column(name = "auth_date", nullable = false)
    private LocalDate authDate;

    @Column(name = "content", nullable = false)
    private String content;

    // 생성 메서드
    public static UserStamp of(Mission mission, User user, String authImageUrl, LocalDate authDate, String content) {
        return UserStamp.builder()
                .mission(mission)
                .user(user)
                .authImageUrl(authImageUrl)
                .authDate(authDate)
                .content(content)
                .build();
    }

}
