package com.chapterconnect.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chapterconnect.app.dto.CreateAnnouncementRequest;
import com.chapterconnect.app.dto.ReviewAnnouncementRequest;
import com.chapterconnect.app.exception.ResourceNotFoundException;
import com.chapterconnect.app.model.Announcement;
import com.chapterconnect.app.model.AnnouncementStatus;
import com.chapterconnect.app.model.Role;
import com.chapterconnect.app.model.User;
import com.chapterconnect.app.repository.AnnouncementRepository;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserService userService;

    public AnnouncementService(
            AnnouncementRepository announcementRepository,
            UserService userService) {

        this.announcementRepository = announcementRepository;
        this.userService = userService;
    }

    public Announcement createAnnouncement(
        CreateAnnouncementRequest request,
        String authenticatedEmail) {

    User creator =
            userService.findByEmailOrThrow(authenticatedEmail);

    if (creator.getRole() == Role.BROTHER) {
        throw new IllegalArgumentException(
                "Brothers are not allowed to create announcements."
        );
    }

    Announcement announcement = new Announcement(
            request.title(),
            request.message(),
            creator,
            AnnouncementStatus.DRAFT
    );

    return announcementRepository.save(announcement);
}

    public Announcement findById(Long id) {
        return announcementRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Announcement not found with id: " + id
                        )
                );
    }

    public List<Announcement> findPublishedAnnouncements() {
        return announcementRepository.findByStatus(
                AnnouncementStatus.PUBLISHED
        );
    }

    public List<Announcement> findPendingAnnouncements() {
        return announcementRepository.findByStatus(
                AnnouncementStatus.PENDING_APPROVAL
        );
    }

   @Transactional
public Announcement submitForApproval(
        Long announcementId,
        String authenticatedEmail) {

    Announcement announcement = findById(announcementId);

    User requestingUser =
            userService.findByEmailOrThrow(authenticatedEmail);

    boolean isAdmin =
            requestingUser.getRole() == Role.ADMIN;

    boolean isCreator =
            announcement.getCreatedBy()
                    .getId()
                    .equals(requestingUser.getId());

    if (!isAdmin && !isCreator) {
        throw new IllegalArgumentException(
                "You may only submit your own announcement."
        );
    }

    if (announcement.getStatus() != AnnouncementStatus.DRAFT
            && announcement.getStatus() != AnnouncementStatus.REJECTED) {

        throw new IllegalArgumentException(
                "Only draft or rejected announcements can be submitted."
        );
    }

    announcement.setStatus(
            AnnouncementStatus.PENDING_APPROVAL
    );

    return announcementRepository.save(announcement);
}

    @Transactional
public Announcement reviewAnnouncement(
        Long announcementId,
        ReviewAnnouncementRequest request,
        String authenticatedEmail) {

    Announcement announcement = findById(announcementId);

    User reviewer =
            userService.findByEmailOrThrow(authenticatedEmail);

    if (reviewer.getRole() != Role.ADMIN) {
        throw new IllegalArgumentException(
                "Only admins may review announcements."
        );
    }

    if (announcement.getStatus()
            != AnnouncementStatus.PENDING_APPROVAL) {

        throw new IllegalArgumentException(
                "Only pending announcements may be reviewed."
        );
    }

    announcement.setReviewedBy(reviewer);
    announcement.setReviewedAt(LocalDateTime.now());
    announcement.setReviewNote(request.reviewNote());

    if (request.approved()) {
        announcement.setStatus(
                AnnouncementStatus.APPROVED
        );
    } else {
        announcement.setStatus(
                AnnouncementStatus.REJECTED
        );
    }

    return announcementRepository.save(announcement);
}

 @Transactional
public Announcement publishAnnouncement(
        Long announcementId,
        String authenticatedEmail) {

    Announcement announcement = findById(announcementId);

    User requestingUser =
            userService.findByEmailOrThrow(authenticatedEmail);

    if (requestingUser.getRole() != Role.ADMIN) {
        throw new IllegalArgumentException(
                "Only admins may publish announcements."
        );
    }

    if (announcement.getStatus()
            != AnnouncementStatus.APPROVED) {

        throw new IllegalArgumentException(
                "Only approved announcements may be published."
        );
    }

    announcement.setStatus(
            AnnouncementStatus.PUBLISHED
    );

    announcement.setPublishedAt(
            LocalDateTime.now()
    );

    return announcementRepository.save(announcement);
}
}