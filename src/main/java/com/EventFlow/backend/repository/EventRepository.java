package com.EventFlow.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EventFlow.backend.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
	
//    List<Event> findByHierarchy(int hierarchy);
//    Optional<Event> findByIdAndHierarchy(long id, int hierarchy);
}
