package com.chapterconnect.app.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chapterconnect.app.dto.CreateMemberRequest;
import com.chapterconnect.app.model.AccountStatus;
import com.chapterconnect.app.model.MemberProfile;
import com.chapterconnect.app.model.MembershipStatus;
import com.chapterconnect.app.model.User;

@Service
public class MemberService {

    private final UserService userService;
    private final MemberProfileService memberProfileService;

    public MemberService(
            UserService userService,
            MemberProfileService memberProfileService) {

        this.userService = userService;
        this.memberProfileService = memberProfileService;
    }

    @Transactional
    public MemberProfile createMember(CreateMemberRequest request) {

        User user = userService.createUser(
                request.email(),
                "TEMPORARY_HASH",
                request.role(),
                AccountStatus.ACTIVE
        );

        return memberProfileService.createProfile(
                user,
                request.firstName(),
                request.lastName(),
                request.major(),
                request.graduationYear(),
                request.pledgeClass(),
                request.chapterPosition(),
                MembershipStatus.ACTIVE
        );
    }
}