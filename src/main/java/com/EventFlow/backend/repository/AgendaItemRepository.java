package com.EventFlow.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EventFlow.backend.model.Event;
import com.EventFlow.backend.model.AgendaItem;

import java.util.List;
import java.util.Optional;

public interface AgendaItemRepository extends JpaRepository<AgendaItem, Long> {
    List<AgendaItem> findByMainEvent(Event mainEvent);
    AgendaItem findByMainEventAndItemAgenda(Event mainEvent, Event itemAgenda);
    Optional<AgendaItem> findByItemAgenda(Event itemAgenda);
    List<AgendaItem> findByMainEventOrderByItemAgendaEventOrderAsc(Event mainEvent);
}
