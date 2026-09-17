package com.chapterconnect.app.dto;

import com.chapterconnect.app.model.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateMemberRequest(

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

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