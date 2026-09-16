package com.chapterconnect.app.dto;

import java.time.LocalDateTime;

import com.chapterconnect.app.model.AnnouncementStatus;
import com.chapterconnect.app.model.Role;

public record AnnouncementResponse(

        Long id,
        String title,
        String message,
        AnnouncementStatus status,

        Long createdByUserId,
        String createdByEmail,
        Role createdByRole,

        Long reviewedByUserId,
        String reviewedByEmail,

        String reviewNote,

        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime reviewedAt,
        LocalDateTime publishedAt

) {
}