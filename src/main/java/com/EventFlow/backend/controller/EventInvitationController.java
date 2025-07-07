package com.EventFlow.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.EventFlow.backend.service.EventInvitationService;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/invitations")
@Tag(name = "Invitations", description = "Manage events invitations between contacts, and comments")
public class EventInvitationController {

    @Autowired
    private EventInvitationService invitationService;

    @Operation(summary = "Get events accepted by the current user (for home page)")
    @ApiResponse(responseCode = "200", description = "List of invited events retrieved successfully")
    @GetMapping("/my-invited-events")
    public ResponseEntity<List<?>> getInvitedEventsForCurrentUser() {
        return ResponseEntity.ok(invitationService.getInvitedEvents());
    }
    
    @Operation(summary = "Get pending invitations for the current user")
    @GetMapping("/my-pending-invitations")
    public ResponseEntity<List<?>> getPendingInvitations() {
        return ResponseEntity.ok(invitationService.getPendingInvitations());
    }


    @Operation(summary = "Get details of an invited event by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invited event found"),
        @ApiResponse(responseCode = "404", description = "Event not found or not invited to the user")
    })
    @GetMapping("/my-invited-events/{eventId}")
    public ResponseEntity<?> getInvitedEventDetails(@PathVariable Long eventId) {
        try {
            return ResponseEntity.ok(invitationService.getInvitedEventById(eventId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }    
    
    @Operation(summary = "Get events invited by a specific user")
    @ApiResponse(responseCode = "200", description = "List of events invited by the user")
    @GetMapping("/invited-events")
    public ResponseEntity<List<?>> getEventsInvitedByUser(@RequestParam Long ownerId) {
        List<?> invitedEvents = invitationService.getGoalsInvitedByUser(ownerId);
        return ResponseEntity.ok(invitedEvents);
    }

    @Operation(summary = "Get users invited to a specific event")
    @ApiResponse(responseCode = "200", description = "List of users invited to the event")
    @GetMapping("/invited/{eventId}/users")
    public ResponseEntity<List<?>> getUsersInvitedTo(@PathVariable Long eventId) {
        List<?> users = invitationService.getUsersInvitedWith(eventId);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get sub-events of an invited event")
    @ApiResponse(responseCode = "200", description = "Sub-events of the invited event retrieved successfully")
    @GetMapping("/invited-events/{eventId}/sub-events")
    public ResponseEntity<List<?>> getSubeventsOfInvitedEvent(@PathVariable Long eventId, @RequestParam Long contactId) {
        return ResponseEntity.ok(invitationService.getSubeventsOfInvitedEvent(eventId, contactId));
    }

    @Operation(summary = "Send an event invitation to a friend ")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Event invitation sent successfully"),
        @ApiResponse(responseCode = "400", description = "Validation or permission error")
    })
    @PostMapping("/invite")
    public ResponseEntity<?> inviteEvent(@RequestParam Long eventId, @RequestParam Long contactId) {
        invitationService.inviteEvent(eventId, contactId);
        return ResponseEntity.ok("Event invitation sent  successfully!");
    }

    @Operation(summary = "Cancel an event invitation")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Event invitation cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Event was not invited or permission denied")
    })
    @DeleteMapping("/invite/{eventId}/{contactId}")
    public ResponseEntity<?> unInviteEvent(@PathVariable Long eventId, @PathVariable Long contactId) {
        invitationService.unInviteEvent(eventId, contactId);
        return ResponseEntity.ok("Event invitation cancelled successfully!");
    }


    @Operation(summary = "Add a comment to an event")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comment added successfully"),
        @ApiResponse(responseCode = "400", description = "User does not have permission to comment")
    })
    @PostMapping("/comment")
    public ResponseEntity<?> commentEvent(@RequestParam Long eventId, @RequestParam String text) {
        invitationService.addComment(eventId, text);
        return ResponseEntity.ok("Comment added!");
    }

    @Operation(summary = "Get all comments on an event")
    @ApiResponse(responseCode = "200", description = "List of comments returned")
    @GetMapping("/comments/{eventId}")
    public ResponseEntity<List<?>> getComments(@PathVariable Long eventId) {
        return ResponseEntity.ok(invitationService.getComments(eventId));
    }


    @Operation(summary = "Delete a comment", description = "Delete your own comment or comments on your own events.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comment deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Not authorized to delete this comment")
    })
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        try {
            invitationService.deleteComment(commentId);
            return ResponseEntity.ok("Comment deleted successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @Operation(summary = "Update your comment")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comment updated successfully"),
        @ApiResponse(responseCode = "400", description = "User is not the owner of the comment")
    })
    @PatchMapping("/comment/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId, @RequestParam String newText) {
        invitationService.updateComment(commentId, newText);
        return ResponseEntity.ok("Comment updated successfully!");
    }
    
    @Operation(summary = "Accept an event invitation", description = "Allows the current user to accept an event invitation.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invitation accepted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or permission denied")
    })
    @PostMapping("/invitation/{eventId}/accept")
    public ResponseEntity<?> acceptInvitation(@PathVariable Long eventId) {
        invitationService.acceptInvitation(eventId);
        return ResponseEntity.ok("Invitation accepted!");
    }

    @Operation(summary = "Decline an event invitation", description = "Allows the current user to decline an event invitation.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invitation declined successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or permission denied")
    })
    @PostMapping("/invitation/{eventId}/decline")
    public ResponseEntity<?> declineInvitation(@PathVariable Long eventId) {
        invitationService.declineInvitation(eventId);
        return ResponseEntity.ok("Invitation declined!");
    }
    
}

