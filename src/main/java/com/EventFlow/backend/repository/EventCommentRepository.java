package com.EventFlow.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EventFlow.backend.model.Event;
import com.EventFlow.backend.model.EventComment;
import com.EventFlow.backend.model.User;

import java.util.List;
import java.util.Optional;

public interface EventCommentRepository extends JpaRepository<EventComment, Long> {
    List<EventComment> findByEventOrderByCreatedAtAsc(Event event);
    Optional<EventComment> findByIdAndAuthor(Long id, User author);
}