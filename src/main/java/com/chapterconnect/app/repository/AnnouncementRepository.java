package com.chapterconnect.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chapterconnect.app.model.Announcement;
import com.chapterconnect.app.model.AnnouncementStatus;

public interface AnnouncementRepository
        extends JpaRepository<Announcement, Long> {

    List<Announcement> findByStatus(
            AnnouncementStatus status
    );

    List<Announcement> findByCreatedById(
            Long userId
    );
}