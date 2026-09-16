package com.chapterconnect.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAnnouncementRequest(

        @NotBlank
        String title,

        @NotBlank
        String message,

        @NotNull
        Long createdByUserId

) {
}