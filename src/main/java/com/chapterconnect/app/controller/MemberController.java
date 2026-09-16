package com.chapterconnect.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chapterconnect.app.dto.CreateMemberRequest;
import com.chapterconnect.app.dto.MemberResponse;
import com.chapterconnect.app.model.MemberProfile;
import com.chapterconnect.app.service.MemberProfileService;
import com.chapterconnect.app.service.MemberService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberProfileService memberProfileService;
    private final MemberService memberService;

    public MemberController(
        MemberProfileService memberProfileService,
        MemberService memberService) {

    this.memberProfileService = memberProfileService;
    this.memberService = memberService;
}

    @GetMapping
    public List<MemberResponse> getAllMembers() {

        return memberProfileService.findAllProfiles()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
public MemberResponse getMemberById(@PathVariable Long id) {

    MemberProfile profile = memberProfileService.findById(id);

    return toResponse(profile);
}

    private MemberResponse toResponse(MemberProfile profile) {

        return new MemberResponse(
                profile.getId(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getUser().getEmail(),
                profile.getMajor(),
                profile.getGraduationYear(),
                profile.getPledgeClass(),
                profile.getChapterPosition(),
                profile.getMembershipStatus(),
                profile.getUser().getRole()
        );
    }

    @PostMapping
public MemberResponse createMember(
        @Valid @RequestBody CreateMemberRequest request) {

    MemberProfile profile = memberService.createMember(request);

    return toResponse(profile);
}
    

}

