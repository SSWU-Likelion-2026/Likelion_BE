package com.likelion.likelion_BE.domain.memberprofile.repository;

import com.likelion.likelion_BE.domain.memberprofile.entity.MemberProfile;
import com.likelion.likelion_BE.domain.memberprofile.enums.MemberGroup;
import com.likelion.likelion_BE.domain.memberprofile.enums.MemberType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {

    boolean existsByUserIdAndTerm(Long userId, Integer term);

    Optional<MemberProfile> findByUserIdAndTerm(Long userId, Integer term);

    @Query("""
            select mp
            from MemberProfile mp
            where mp.term = :term
              and (:memberGroup is null or mp.memberGroup = :memberGroup)
              and (:memberType is null or mp.memberType = :memberType)
            order by mp.name asc
            """)
    List<MemberProfile> findAllByCondition(
            @Param("term") Integer term,
            @Param("memberGroup") MemberGroup memberGroup,
            @Param("memberType") MemberType memberType
    );
}
