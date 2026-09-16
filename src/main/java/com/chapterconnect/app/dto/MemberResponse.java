package com.chapterconnect.app.dto;

import com.chapterconnect.app.model.MembershipStatus;
import com.chapterconnect.app.model.Role;

public record MemberResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String major,
        Integer graduationYear,
        String pledgeClass,
        String chapterPosition,
        MembershipStatus membershipStatus,
        Role role
) {
}