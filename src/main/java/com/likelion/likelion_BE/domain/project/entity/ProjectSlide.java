package com.likelion.likelion_BE.domain.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class ProjectSlide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_slide_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "image_url", nullable = false, length = 200)
    private String imageUrl;

    @Column(name = "sequence_num", nullable = false)
    private Integer sequenceNum;

    public void assignProject(Project project) {
        this.project = project;
    }

    public static ProjectSlide createSlide(String imageUrl, Integer sequenceNum) {
        return ProjectSlide.builder()
                .imageUrl(imageUrl)
                .sequenceNum(sequenceNum)
                .build();
    }
}
