package com.EventFlow.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EventFlow.backend.dto.EventCommentDTO;
import com.EventFlow.backend.dto.EventDTO;
import com.EventFlow.backend.dto.InvitedEventDTO;
import com.EventFlow.backend.dto.UserDTO;
import com.EventFlow.backend.model.*;
import com.EventFlow.backend.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventInvitationService {

    @Autowired private EventRepository eventRepository;
    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private EventInvitationRepository eventInvitationRepository;
    @Autowired private AgendaItemRepository agendaItemRepository;
    @Autowired private ContactRepository contactRepository;
    @Autowired private UserEventRepository userEventRepository;
    @Autowired private EventCommentRepository eventCommentRepository;

    public List<InvitedEventDTO> getInvitedEvents() {
        User user = userService.getAuthenticatedUser();
        return eventInvitationRepository.findByContact(user)
                .stream()
                .filter(invitedEvent -> invitedEvent.getStatus() == InvitationStatus.ACCEPTED)
                .map(invitedEvent -> {
                    // Find the UserEvent for the owner and the invited event to get hierarchy
                    UserEvent userEvent = userEventRepository.findByUserAndEvent(invitedEvent.getOwner(), invitedEvent.getEvent())
                            .orElseThrow(() -> new RuntimeException("UserEvent not found for owner and event"));

                    // Pass both Event and UserEvent to EventDTO constructor
                    EventDTO eventDTO = new EventDTO(invitedEvent.getEvent(), userEvent);

                    return new InvitedEventDTO(
                            eventDTO,
                            invitedEvent.getOwner().getEmail(),
                            invitedEvent.getOwner().getId(),
                            invitedEvent.getStatus()
                    );
                })
                .collect(Collectors.toList());
    }

    public InvitedEventDTO getInvitedEventById(Long eventId) {
        User user = userService.getAuthenticatedUser();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        InvitedEvent invitedEvent = eventInvitationRepository.findByEventAndContact(event, user)
                .orElseThrow(() -> new RuntimeException("You are not invited to this event."));

        // Find the UserEvent linking the owner and the event, to get hierarchy
        UserEvent userEvent = userEventRepository.findByUserAndEvent(invitedEvent.getOwner(), event)
                .orElseThrow(() -> new RuntimeException("UserEvent not found for owner and event"));

        return new InvitedEventDTO(
                new EventDTO(invitedEvent.getEvent(), userEvent),
                invitedEvent.getOwner().getEmail(),
                invitedEvent.getOwner().getId(),
                invitedEvent.getStatus()
        );
    }

    
    
    public List<InvitedEventDTO> getGoalsInvitedByUser(Long ownerId) {
        User contact = userService.getAuthenticatedUser(); // current user
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<InvitedEvent> invitedEvents = eventInvitationRepository.findByOwnerAndContact(owner, contact);

        return invitedEvents.stream()
                .map(invitedEvent -> {
                    UserEvent userEvent = userEventRepository.findByUserAndEvent(owner, invitedEvent.getEvent())
                            .orElseThrow(() -> new RuntimeException("UserEvent not found for owner and event"));
                    return new InvitedEventDTO(
                            new EventDTO(invitedEvent.getEvent(), userEvent),
                            invitedEvent.getOwner().getEmail(),
                            invitedEvent.getOwner().getId(),
                            invitedEvent.getStatus()
                    );
                })
                .collect(Collectors.toList());
    }


    public List<UserDTO> getUsersInvitedWith(Long eventId) {
        User owner = userService.getAuthenticatedUser();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Ensure the current user owns the event and it's a main event
        userEventRepository.findByUserAndEventAndHierarchy(owner, event, 1)
                .orElseThrow(() -> new RuntimeException("You do not own this event or it's not a main event"));

        List<InvitedEvent> invitedEvents = eventInvitationRepository.findByEventAndOwner(event, owner);

        return invitedEvents.stream()
        		.filter(invited -> invited.getStatus() == InvitationStatus.ACCEPTED)
                .map(invited -> new UserDTO(invited.getContact()))
                .collect(Collectors.toList());
    }
    
    public List<EventDTO> getSubeventsOfInvitedEvent(Long eventId , Long contactId) {
        User user = userService.getAuthenticatedUser();
        User contactUser = userRepository.findById(contactId)
        		.orElseThrow(() -> new RuntimeException("contactUser not found"));
        Event mainEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        eventInvitationRepository.findByEventAndContact(mainEvent, user)
                .orElseThrow(() -> new RuntimeException("You do not have access to this event"));

        return agendaItemRepository.findByMainEvent(mainEvent)
                .stream()
                .map(sub_event -> {
                    Event itemAgenda = sub_event.getItemAgenda();
                    UserEvent userEvent = userEventRepository.findByUserAndEvent(contactUser, itemAgenda)
                            .orElseThrow(() -> new RuntimeException("UserEvent not found for user and sub event"));
                    return new EventDTO(itemAgenda, userEvent);
                })
                .collect(Collectors.toList());
    }


    public void inviteEvent(Long eventId, Long contactId) {
        User owner = userService.getAuthenticatedUser();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        userEventRepository.findByUserAndEventAndHierarchy(owner, event, 1)
                .orElseThrow(() -> new RuntimeException("Users can only be invited to main events."));

        User contact = userRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("friend user not found"));

        if (owner.getId().equals(contactId)) throw new RuntimeException("Cannot invite yourself");

        boolean areContacts = contactRepository.findBySenderAndContact(owner, contact).filter(Contact::isContactAccepted).isPresent()
                || contactRepository.findBySenderAndContact(contact, owner).filter(Contact::isContactAccepted).isPresent();

        if (!areContacts) {
            throw new RuntimeException("You can only invite your friends to events");
        }

        Optional<InvitedEvent> existing = eventInvitationRepository.findByEventAndOwnerAndContact(event, owner, contact);
        if (existing.isPresent()) throw new RuntimeException("This friend is already invited to the event");

        InvitedEvent invitedEvent = new InvitedEvent();
        invitedEvent.setEvent(event);
        invitedEvent.setOwner(owner);
        invitedEvent.setContact(contact);
        invitedEvent.setStatus(InvitationStatus.INVITED);
        eventInvitationRepository.save(invitedEvent);
    }
    public void acceptInvitation(Long eventId) {
        User user = userService.getAuthenticatedUser();
        InvitedEvent invitedEvent = eventInvitationRepository.findByEventAndContact(eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found")), user)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));

        invitedEvent.setStatus(InvitationStatus.ACCEPTED);
        eventInvitationRepository.save(invitedEvent);
    }

    public void declineInvitation(Long eventId) {
        User user = userService.getAuthenticatedUser();
        InvitedEvent invitedEvent = eventInvitationRepository.findByEventAndContact(eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found")), user)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));

        eventInvitationRepository.delete(invitedEvent);
    }

    public void unInviteEvent(Long eventId, Long contactId) {
        User owner = userService.getAuthenticatedUser();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        InvitedEvent invitedEvent = eventInvitationRepository.findByEventAndOwnerAndContact(event, owner, 
                userRepository.findById(contactId)
                        .orElseThrow(() -> new RuntimeException("friend not found")))
                .orElseThrow(() -> new RuntimeException("This friend has not been invited to the event "));

        eventInvitationRepository.delete(invitedEvent);
    }
    
 //  method to get pending invitations:
    public List<InvitedEventDTO> getPendingInvitations() {
        User user = userService.getAuthenticatedUser();
        return eventInvitationRepository.findByContact(user).stream()
            .filter(invited -> invited.getStatus() == InvitationStatus.INVITED)
            .map(invited -> {
                UserEvent userEvent = userEventRepository.findByUserAndEvent(invited.getOwner(), invited.getEvent())
                    .orElseThrow(() -> new RuntimeException("UserEvent not found"));
                return new InvitedEventDTO(
                    new EventDTO(invited.getEvent(), userEvent),
                    invited.getOwner().getEmail(),
                    invited.getOwner().getId(),
                    invited.getStatus()
                );
            })
            .collect(Collectors.toList());
    }

    public void addComment(Long eventId, String text) {
        User user = userService.getAuthenticatedUser();
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found!"));

        boolean hasAccess = userEventRepository.findByUserAndEvent(user, event).isPresent() ||
                eventInvitationRepository.findByEventAndContact(event, user).isPresent();

        if (!hasAccess) throw new RuntimeException("You do not have access to comment on this event!");

        EventComment comment = new EventComment();
        comment.setEvent(event);
        comment.setAuthor(user);
        comment.setContent(text);
        comment.setCreatedAt(LocalDateTime.now());

        eventCommentRepository.save(comment);
    }

    public List<EventCommentDTO> getComments(Long eventId) {
        User user = userService.getAuthenticatedUser();
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found!"));

        boolean hasAccess = userEventRepository.findByUserAndEvent(user, event).isPresent() ||
                eventInvitationRepository.findByEventAndContact(event, user).isPresent();

        if (!hasAccess) throw new RuntimeException("You do not have access to view comments!");

        return eventCommentRepository.findByEventOrderByCreatedAtAsc(event)
                .stream()
                .map(comment -> new EventCommentDTO(
                		comment.getId(),
                        comment.getAuthor().getEmail(),
                        comment.getCreatedAt(),
                        comment.getContent()
                ))
                .collect(Collectors.toList());
    }

    public void deleteComment(Long commentId) {
        User currentUser = userService.getAuthenticatedUser();

        EventComment comment = eventCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        Event event = comment.getEvent();

        boolean isAuthor = comment.getAuthor().getId().equals(currentUser.getId());
        boolean isEventOwner = userEventRepository.findByUserAndEventAndHierarchy(currentUser, event, 1).isPresent();

        if (!isAuthor && !isEventOwner) {
            throw new RuntimeException("You can only delete your own comments or comments on your events");
        }

        eventCommentRepository.delete(comment);
    }

    
    public void updateComment(Long commentId, String newText) {
        User user = userService.getAuthenticatedUser();
        EventComment comment = eventCommentRepository.findByIdAndAuthor(commentId, user)
                .orElseThrow(() -> new RuntimeException("You can only update your own comments"));
        comment.setContent(newText);
        eventCommentRepository.save(comment);
    }
}
