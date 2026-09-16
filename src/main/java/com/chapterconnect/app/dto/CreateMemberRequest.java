package com.chapterconnect.app.dto;

import com.chapterconnect.app.model.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMemberRequest(

        @NotBlank
        @Email
        String email,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        String major,

        Integer graduationYear,

        String pledgeClass,

        String chapterPosition,

        @NotNull
        Role role
) {
}