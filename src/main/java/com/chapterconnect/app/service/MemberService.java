package com.chapterconnect.app.service;

import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public MemberService(
            UserService userService,
            MemberProfileService memberProfileService,
            PasswordEncoder passwordEncoder) {

        this.userService = userService;
        this.memberProfileService = memberProfileService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public MemberProfile createMember(CreateMemberRequest request) {

        String passwordHash =
                passwordEncoder.encode(request.password());

        User user = userService.createUser(
                request.email(),
                passwordHash,
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