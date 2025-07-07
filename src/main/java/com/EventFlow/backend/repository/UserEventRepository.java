package com.EventFlow.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EventFlow.backend.model.Event;
import com.EventFlow.backend.model.User;
import com.EventFlow.backend.model.UserEvent;

import java.util.List;
import java.util.Optional;

public interface UserEventRepository extends JpaRepository<UserEvent, Long> {
    List<UserEvent> findByUserAndHierarchy(User user, int hierarchy);
    Optional<UserEvent> findByUserAndEvent(User user, Event event);
    Optional<UserEvent> findByUserAndEventAndHierarchy(User user, Event event, int hierarchy);
    List<UserEvent> findByEventIn(List<Event> events);
}
