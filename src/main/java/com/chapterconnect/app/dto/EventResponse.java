package com.chapterconnect.app.dto;

import java.time.LocalDateTime;

import com.chapterconnect.app.model.EventStatus;
import com.chapterconnect.app.model.EventType;
import com.chapterconnect.app.model.Role;

public record EventResponse(
        Long id,
        String title,
        String description,
        EventType eventType,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        String location,
        EventStatus status,

        Long createdByUserId,
        String createdByEmail,
        Role createdByRole,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}