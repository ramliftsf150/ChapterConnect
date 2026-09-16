package com.chapterconnect.app.dto;

import jakarta.validation.constraints.NotNull;

public record ReviewAnnouncementRequest(

        @NotNull
        Long requestingUserId,

        @NotNull
        Boolean approved,

        String reviewNote

) {
}