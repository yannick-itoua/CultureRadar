package com.cultureradar.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a cultural event in the CultureRadar application.
 * Each event belongs to a specific location and category and includes
 * information about timing, pricing, and approval status.
 */
@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Event name is required")
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotNull(message = "Start time is required")
    @Column(nullable = false)
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private String imageUrl;
    
    private Double price;
    
    @Column(nullable = false)
    private Boolean isFree = false;
    
    private String externalId;
    
    private String externalSource; // "EVENTBRITE", "CANADA_GOV", "MANUAL"
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id")
    private Location location;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventCategory category;
    
    @Column(nullable = false)
    private Boolean approved = false;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;
    
    @Transient
    private Double distanceKm;
    
    /**
     * Checks if the event is happening now (between start and end time).
     * 
     * @return true if the event is currently happening
     */
    @Transient
    public boolean isHappeningNow() {
        LocalDateTime now = LocalDateTime.now();
        if (endTime == null) {
            // If no end time, assume event is 2 hours long
            return now.isAfter(startTime) && now.isBefore(startTime.plusHours(2));
        }
        return now.isAfter(startTime) && now.isBefore(endTime);
    }
    
    /**
     * Checks if the event has already happened.
     * 
     * @return true if the event has already happened
     */
    @Transient
    public boolean isPast() {
        LocalDateTime now = LocalDateTime.now();
        if (endTime == null) {
            // If no end time, use start time plus 2 hours
            return now.isAfter(startTime.plusHours(2));
        }
        return now.isAfter(endTime);
    }
    
    /**
     * Checks if the event is free of charge.
     * 
     * @return true if the event is free
     */
    @Transient
    public boolean isFreeEvent() {
        return isFree != null && isFree || (price != null && price <= 0);
    }
}
