package com.chapterconnect.app.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAnnouncementRequest(

        @NotBlank
        String title,

        @NotBlank
        String message

) {
}