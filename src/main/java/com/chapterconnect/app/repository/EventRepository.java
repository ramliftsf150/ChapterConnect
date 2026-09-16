package com.chapterconnect.app.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chapterconnect.app.model.Event;
import com.chapterconnect.app.model.EventStatus;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCreatedById(Long userId);

    List<Event> findByStatus(EventStatus status);

    List<Event> findByStartDateTimeAfterOrderByStartDateTimeAsc(
            LocalDateTime dateTime
    );
}
