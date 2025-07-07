package com.EventFlow.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EventFlow.backend.model.Event;
import com.EventFlow.backend.model.InvitedEvent;
import com.EventFlow.backend.model.User;

import java.util.List;
import java.util.Optional;

public interface EventInvitationRepository extends JpaRepository<InvitedEvent, Long> {
    List<InvitedEvent> findByContact(User contact);
    Optional<InvitedEvent> findByEventAndContact(Event event, User contact);
    Optional<InvitedEvent> findByEventAndOwnerAndContact(Event event, User owner, User contact);
    List<InvitedEvent> findByEvent(Event event);
    List<InvitedEvent> findByOwnerAndContact(User owner, User contact);
    List<InvitedEvent> findByEventAndOwner(Event event, User owner);
}