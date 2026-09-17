package com.chapterconnect.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chapterconnect.app.dto.CreateEventRequest;
import com.chapterconnect.app.dto.UpdateEventRequest;
import com.chapterconnect.app.exception.ResourceNotFoundException;
import com.chapterconnect.app.model.Event;
import com.chapterconnect.app.model.EventStatus;
import com.chapterconnect.app.model.Role;
import com.chapterconnect.app.model.User;
import com.chapterconnect.app.repository.EventRepository;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserService userService;

    public EventService(
            EventRepository eventRepository,
            UserService userService) {

        this.eventRepository = eventRepository;
        this.userService = userService;
    }

    public Event createEvent(
        CreateEventRequest request,
        String authenticatedEmail) {

    User creator =
            userService.findByEmailOrThrow(authenticatedEmail);

    if (creator.getRole() == Role.BROTHER) {
        throw new IllegalArgumentException(
                "Brothers are not allowed to create events."
        );
    }

    if (request.endDateTime().isBefore(request.startDateTime())) {
        throw new IllegalArgumentException(
                "Event end time cannot be before start time."
        );
    }

    Event event = new Event(
            request.title(),
            request.description(),
            request.eventType(),
            request.startDateTime(),
            request.endDateTime(),
            request.location(),
            creator,
            EventStatus.SCHEDULED
    );

    return eventRepository.save(event);
}

    public List<Event> findAllEvents() {
        return eventRepository.findAll();
    }

    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Event not found with id: " + id
                        )
                );
    }

    public List<Event> findUpcomingEvents() {
        return eventRepository
                .findByStartDateTimeAfterOrderByStartDateTimeAsc(
                        LocalDateTime.now()
                );
    }

  @Transactional
public Event updateEvent(
        Long eventId,
        UpdateEventRequest request,
        String authenticatedEmail) {

    Event event = findById(eventId);

    User requestingUser =
            userService.findByEmailOrThrow(authenticatedEmail);

    if (requestingUser.getRole() == Role.BROTHER) {
        throw new IllegalArgumentException(
                "Brothers are not allowed to edit events."
        );
    }

    boolean isAdmin =
            requestingUser.getRole() == Role.ADMIN;

    boolean isCreator =
            event.getCreatedBy()
                    .getId()
                    .equals(requestingUser.getId());

    if (!isAdmin && !isCreator) {
        throw new IllegalArgumentException(
                "Officers may only edit events they created."
        );
    }

    if (request.endDateTime().isBefore(request.startDateTime())) {
        throw new IllegalArgumentException(
                "Event end time cannot be before start time."
        );
    }

    event.setTitle(request.title());
    event.setDescription(request.description());
    event.setEventType(request.eventType());
    event.setStartDateTime(request.startDateTime());
    event.setEndDateTime(request.endDateTime());
    event.setLocation(request.location());
    event.setStatus(request.status());

    return eventRepository.save(event);
}

public void deleteEvent(
        Long eventId,
        String authenticatedEmail) {

    Event event = findById(eventId);

    User requestingUser =
            userService.findByEmailOrThrow(authenticatedEmail);

    if (requestingUser.getRole() != Role.ADMIN) {
        throw new IllegalArgumentException(
                "Only admins are allowed to delete events."
        );
    }

    eventRepository.delete(event);
}
}