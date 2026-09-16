package com.chapterconnect.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.chapterconnect.app.exception.ResourceNotFoundException;
import com.chapterconnect.app.model.MemberProfile;
import com.chapterconnect.app.model.MembershipStatus;
import com.chapterconnect.app.model.User;
import com.chapterconnect.app.repository.MemberProfileRepository;

@Service
public class MemberProfileService {

    private final MemberProfileRepository memberProfileRepository;

    public MemberProfileService(
            MemberProfileRepository memberProfileRepository) {

        this.memberProfileRepository = memberProfileRepository;
    }

    public MemberProfile createProfile(
            User user,
            String firstName,
            String lastName,
            String major,
            Integer graduationYear,
            String pledgeClass,
            String chapterPosition,
            MembershipStatus membershipStatus) {

        if (memberProfileRepository.findByUserId(user.getId()).isPresent()) {
            throw new IllegalArgumentException(
                    "This user already has a member profile."
            );
        }

        MemberProfile profile = new MemberProfile(
                user,
                firstName,
                lastName,
                major,
                graduationYear,
                pledgeClass,
                chapterPosition,
                membershipStatus
        );

        return memberProfileRepository.save(profile);
    }

    public Optional<MemberProfile> findByUserId(Long userId) {
        return memberProfileRepository.findByUserId(userId);
    }

    public List<MemberProfile> findAllProfiles() {
        return memberProfileRepository.findAll();
    }


    public MemberProfile findById(Long id) {
    return memberProfileRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Member profile not found with id: " + id
                    )
            );
}
}