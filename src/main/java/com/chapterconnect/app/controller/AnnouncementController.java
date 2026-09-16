package com.chapterconnect.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chapterconnect.app.dto.AnnouncementResponse;
import com.chapterconnect.app.dto.CreateAnnouncementRequest;
import com.chapterconnect.app.dto.ReviewAnnouncementRequest;
import com.chapterconnect.app.model.Announcement;
import com.chapterconnect.app.service.AnnouncementService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(
            AnnouncementService announcementService) {

        this.announcementService = announcementService;
    }

    @PostMapping
    public AnnouncementResponse createAnnouncement(
            @Valid @RequestBody CreateAnnouncementRequest request) {

        return toResponse(
                announcementService.createAnnouncement(request)
        );
    }

    @GetMapping("/published")
    public List<AnnouncementResponse> getPublishedAnnouncements() {

        return announcementService
                .findPublishedAnnouncements()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/pending")
    public List<AnnouncementResponse> getPendingAnnouncements() {

        return announcementService
                .findPendingAnnouncements()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PutMapping("/{id}/submit")
    public AnnouncementResponse submitAnnouncement(
            @PathVariable Long id,
            @RequestParam Long requestingUserId) {

        return toResponse(
                announcementService.submitForApproval(
                        id,
                        requestingUserId
                )
        );
    }

    @PutMapping("/{id}/review")
    public AnnouncementResponse reviewAnnouncement(
            @PathVariable Long id,
            @Valid @RequestBody ReviewAnnouncementRequest request) {

        return toResponse(
                announcementService.reviewAnnouncement(
                        id,
                        request
                )
        );
    }

    @PutMapping("/{id}/publish")
    public AnnouncementResponse publishAnnouncement(
            @PathVariable Long id,
            @RequestParam Long requestingUserId) {

        return toResponse(
                announcementService.publishAnnouncement(
                        id,
                        requestingUserId
                )
        );
    }

    private AnnouncementResponse toResponse(
            Announcement announcement) {

        return new AnnouncementResponse(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getMessage(),
                announcement.getStatus(),

                announcement.getCreatedBy().getId(),
                announcement.getCreatedBy().getEmail(),
                announcement.getCreatedBy().getRole(),

                announcement.getReviewedBy() != null
                        ? announcement.getReviewedBy().getId()
                        : null,

                announcement.getReviewedBy() != null
                        ? announcement.getReviewedBy().getEmail()
                        : null,

                announcement.getReviewNote(),

                announcement.getCreatedAt(),
                announcement.getUpdatedAt(),
                announcement.getReviewedAt(),
                announcement.getPublishedAt()
        );
    }
}