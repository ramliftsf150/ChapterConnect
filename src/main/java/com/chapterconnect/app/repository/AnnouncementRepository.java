package com.chapterconnect.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.chapterconnect.app.model.Announcement;
import com.chapterconnect.app.model.AnnouncementStatus;

public interface AnnouncementRepository
        extends JpaRepository<Announcement, Long> {

    @Override
    @EntityGraph(attributePaths = {"createdBy", "reviewedBy"})
    Optional<Announcement> findById(Long id);

    @EntityGraph(attributePaths = {"createdBy", "reviewedBy"})
    List<Announcement> findByStatus(
            AnnouncementStatus status
    );

    @EntityGraph(attributePaths = {"createdBy", "reviewedBy"})
    List<Announcement> findByCreatedById(Long userId);
}