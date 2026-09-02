package com.likelion.likelion_BE.domain.session.entity;

import com.likelion.likelion_BE.common.entity.BaseEntity;
import com.likelion.likelion_BE.domain.project.enums.Part;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Table(
        name = "session",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_session_term_part_week",
                        columnNames = {"term", "part", "week_number"}
                )
        }
)
public class Session extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long id;

    @Column(nullable = false)
    private Integer term;

    @Column(name = "week_number", nullable = false)
    private Integer weekNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Part part;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "sub_title", nullable = false, length = 200)
    private String subTitle;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(name = "thumbnail_url", nullable = false, length = 500)
    private String thumbnailUrl;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearningTopic> learningTopics = new ArrayList<>();
}