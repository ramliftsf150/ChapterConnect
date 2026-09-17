package com.chapterconnect.app.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.chapterconnect.app.model.Event;
import com.chapterconnect.app.model.EventStatus;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Override
    @EntityGraph(attributePaths = "createdBy")
    List<Event> findAll();

    @Override
    @EntityGraph(attributePaths = "createdBy")
    Optional<Event> findById(Long id);

    @EntityGraph(attributePaths = "createdBy")
    List<Event> findByCreatedById(Long userId);

    @EntityGraph(attributePaths = "createdBy")
    List<Event> findByStatus(EventStatus status);

    @EntityGraph(attributePaths = "createdBy")
    List<Event> findByStartDateTimeAfterOrderByStartDateTimeAsc(
            LocalDateTime dateTime
    );
}