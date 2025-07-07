package com.EventFlow.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.EventFlow.backend.model.InvitationStatus;
@Data
@AllArgsConstructor
public class InvitedEventDTO {
    private EventDTO event;
    private String ownerEmail;
    private long ownerID;
    private InvitationStatus status;
}
