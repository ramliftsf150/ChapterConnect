package com.chapterconnect.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chapterconnect.app.dto.CreateEventRequest;
import com.chapterconnect.app.dto.EventResponse;
import com.chapterconnect.app.dto.UpdateEventRequest;
import com.chapterconnect.app.model.Event;
import com.chapterconnect.app.service.EventService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventResponse> getAllEvents() {

        return eventService.findAllEvents()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public EventResponse getEventById(
            @PathVariable Long id) {

        return toResponse(
                eventService.findById(id)
        );
    }

    @GetMapping("/upcoming")
    public List<EventResponse> getUpcomingEvents() {

        return eventService.findUpcomingEvents()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    public EventResponse createEvent(
            @Valid @RequestBody CreateEventRequest request) {

        return toResponse(
                eventService.createEvent(request)
        );
    }

    private EventResponse toResponse(Event event) {

        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getEventType(),
                event.getStartDateTime(),
                event.getEndDateTime(),
                event.getLocation(),
                event.getStatus(),

                event.getCreatedBy().getId(),
                event.getCreatedBy().getEmail(),
                event.getCreatedBy().getRole(),

                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }

    @PutMapping("/{id}")
public EventResponse updateEvent(
        @PathVariable Long id,
        @Valid @RequestBody UpdateEventRequest request) {

    return toResponse(
            eventService.updateEvent(id, request)
    );
}

@DeleteMapping("/{id}")
public void deleteEvent(
        @PathVariable Long id,
        @RequestParam Long requestingUserId) {

    eventService.deleteEvent(
            id,
            requestingUserId
    );
}

}