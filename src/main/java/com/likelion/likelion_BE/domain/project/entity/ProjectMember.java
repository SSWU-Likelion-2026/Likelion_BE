package com.likelion.likelion_BE.domain.project.entity;

import com.likelion.likelion_BE.domain.project.enums.Part;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "project_member")
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "name", nullable = false, length = 10)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "part", nullable = false)
    private Part part;

    public void assignProject(Project project) {
        this.project = project;
    }

    public static ProjectMember createMember(String name, Part part) {
        return ProjectMember.builder()
                .name(name)
                .part(part)
                .build();
    }
}
