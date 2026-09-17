package com.chapterconnect.app.dto;

import java.time.LocalDateTime;

import com.chapterconnect.app.model.EventType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateEventRequest(

        @NotBlank
        String title,

        String description,

        @NotNull
        EventType eventType,

        @NotNull
        LocalDateTime startDateTime,

        @NotNull
        LocalDateTime endDateTime,

        String location

) {
}