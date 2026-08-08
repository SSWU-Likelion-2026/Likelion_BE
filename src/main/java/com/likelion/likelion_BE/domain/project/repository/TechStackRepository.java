package com.likelion.likelion_BE.domain.project.repository;

import com.likelion.likelion_BE.domain.project.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TechStackRepository extends JpaRepository<TechStack, Long> {
    List<TechStack> findAllByIdIn(List<Long> ids);
}