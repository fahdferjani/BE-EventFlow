package com.EventFlow.backend.dto;

import com.EventFlow.backend.model.Contact;

import lombok.Getter;

@Getter
public class ContactDTO {
	private Long senderId;
    private Long receiverId;
    private boolean contactAccepted;

    public ContactDTO(Contact contact) {
    	this.senderId = contact.getSender().getId();
        this.receiverId = contact.getContact().getId();
        this.contactAccepted = contact.isContactAccepted();
    }
}