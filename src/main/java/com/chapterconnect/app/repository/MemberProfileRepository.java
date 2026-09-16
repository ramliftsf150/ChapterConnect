package com.chapterconnect.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chapterconnect.app.model.MemberProfile;

public interface MemberProfileRepository
        extends JpaRepository<MemberProfile, Long> {

    Optional<MemberProfile> findByUserId(Long userId);
}
