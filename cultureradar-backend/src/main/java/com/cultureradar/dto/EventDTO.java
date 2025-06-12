package com.cultureradar.dto;

import com.cultureradar.model.EventCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Event information.
 * Used to transfer event data between the controller and client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    
    private Long id;
    
    @NotBlank(message = "Event name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Start time is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
    
    private String imageUrl;
    
    private Double price;
    
    private Boolean isFree;
    
    private EventCategory category;
    
    private LocationDTO location;
    
    private String externalId;
    
    private String externalSource;
    
    private Boolean approved;
    
    // Distance from user's location (optional, can be populated by controller)
    private Double distanceKm;
    
    // Computed property for frontend display convenience
    public String getFormattedDate() {
        if (startTime == null) {
            return "";
        }
        
        String formattedDate = startTime.toLocalDate().toString();
        
        if (endTime != null && !startTime.toLocalDate().equals(endTime.toLocalDate())) {
            formattedDate += " - " + endTime.toLocalDate().toString();
        }
        
        return formattedDate;
    }
    
    // Computed property for frontend display convenience
    public String getFormattedTime() {
        if (startTime == null) {
            return "";
        }
        
        String formattedTime = startTime.toLocalTime().toString();
        
        if (endTime != null) {
            formattedTime += " - " + endTime.toLocalTime().toString();
        }
        
        return formattedTime;
    }
}