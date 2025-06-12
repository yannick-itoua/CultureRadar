package com.cultureradar.controller;

import com.cultureradar.model.Event;
import com.cultureradar.model.EventCategory;
import com.cultureradar.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for managing cultural events.
 * Provides endpoints for searching, creating, updating, and managing events.
 */
@RestController
@RequestMapping("/api/events")
public class EventController {
    
    private final EventService eventService;
    
    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }
    
    /**
     * Retrieves events with optional filtering parameters
     * 
     * @param city Filter by city
     * @param isFree Filter by free admission (true/false)
     * @param category Filter by event category
     * @param startDate Filter by minimum start date
     * @param endDate Filter by maximum end date
     * @param page Page number (0-based)
     * @param size Page size
     * @param sortBy Field to sort by
     * @param direction Sort direction (asc/desc)
     * @return Page of events matching criteria
     */
    @GetMapping("/public/search")
    public ResponseEntity<Page<Event>> searchEvents(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Boolean isFree,
            @RequestParam(required = false) EventCategory category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startTime") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, sortDirection, sortBy);
        
        Page<Event> events = eventService.findEvents(city, isFree, category, 
            startDate, endDate, pageable);
        
        return ResponseEntity.ok(events);
    }
    
    /**
     * Retrieves an event by its ID
     * 
     * @param id Event ID
     * @return Event if found
     */
    @GetMapping("/public/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Retrieves upcoming events (next 7 days)
     * 
     * @return List of upcoming events
     */
    @GetMapping("/public/upcoming")
    public ResponseEntity<List<Event>> getUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekLater = now.plusDays(7);
        
        Pageable pageable = PageRequest.of(0, 20, Sort.Direction.ASC, "startTime");
        Page<Event> events = eventService.findEvents(null, null, null, 
            now, oneWeekLater, pageable);
        
        return ResponseEntity.ok(events.getContent());
    }
    
    /**
     * Creates a new event
     * 
     * @param event Event data
     * @return Created event
     */
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Event> createEvent(@Valid @RequestBody Event event) {
        Event savedEvent = eventService.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
    }
    
    /**
     * Updates an existing event
     * 
     * @param id Event ID
     * @param eventDetails Updated event data
     * @return Updated event
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Event> updateEvent(
            @PathVariable Long id, 
            @Valid @RequestBody Event eventDetails) {
        
        return eventService.findById(id).map(event -> {
            event.setName(eventDetails.getName());
            event.setDescription(eventDetails.getDescription());
            event.setStartTime(eventDetails.getStartTime());
            event.setEndTime(eventDetails.getEndTime());
            event.setLocation(eventDetails.getLocation());
            event.setCategory(eventDetails.getCategory());
            event.setPrice(eventDetails.getPrice());
            event.setIsFree(eventDetails.getIsFree());
            event.setImageUrl(eventDetails.getImageUrl());
            
            Event updatedEvent = eventService.updateEvent(event);
            return ResponseEntity.ok(updatedEvent);
        }).orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Deletes an event
     * 
     * @param id Event ID
     * @return No content response if successful
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        return eventService.findById(id).map(event -> {
            eventService.deleteEvent(id);
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }).orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Retrieves events pending approval
     * 
     * @return List of events pending approval
     */
    @GetMapping("/admin/pending-approval")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<List<Event>> getPendingApprovalEvents() {
        // This would typically call a method like eventService.findByApproved(false)
        // For now we're using the existing find method with null parameters
        Pageable pageable = PageRequest.of(0, 50, Sort.Direction.DESC, "id");
        Page<Event> pendingEvents = eventService.findEvents(null, null, null, null, null, pageable);
        
        return ResponseEntity.ok(pendingEvents.getContent());
    }
    
    /**
     * Approves events by ID
     * 
     * @param ids List of event IDs to approve
     * @return List of approved events
     */
    @PutMapping("/admin/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<List<Event>> approveEvents(@RequestBody List<Long> ids) {
        List<Event> approvedEvents = eventService.approveEvents(ids);
        return ResponseEntity.ok(approvedEvents);
    }
    
    /**
     * Triggers manual fetch from external APIs
     * 
     * @return Status message
     */
    @PostMapping("/admin/fetch-external")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> fetchExternalEvents() {
        // This would typically be the same method called by the scheduler
        eventService.fetchExternalEvents();
        return ResponseEntity.ok("External event fetch initiated");
    }
}
