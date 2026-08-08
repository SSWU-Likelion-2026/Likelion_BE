package com.likelion.likelion_BE.domain.stamp.entity;

import com.likelion.likelion_BE.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "mission")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Mission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(name = "term", nullable = false)
    private Integer term;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "stamp_url", length = 500)
    private String stampUrl;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    // 생성 메서드
    public static Mission of(String title, String description, Integer term,String imageUrl,
                                 String stampUrl, LocalDateTime startAt, LocalDateTime endAt) {
        return Mission.builder()
                .title(title)
                .description(description)
                .term(term)
                .imageUrl(imageUrl)
                .stampUrl(stampUrl)
                .startAt(startAt)
                .endAt(endAt)
                .build();
    }

    // 수정 메서드
    public void update(String title, String description, Integer term,String imageUrl,
                       String stampUrl, LocalDateTime startAt, LocalDateTime endAt) {
        this.title = title;
        this.description = description;
        this.term = term;
        this.imageUrl = imageUrl;
        this.stampUrl = stampUrl;
        this.startAt = startAt;
        this.endAt = endAt;
    }
}
