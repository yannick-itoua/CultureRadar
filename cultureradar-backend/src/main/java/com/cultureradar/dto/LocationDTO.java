package com.cultureradar.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Data Transfer Object for Location information.
 * Used to transfer venue/location data between the controller and client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
    
    private Long id;
    
    @NotBlank(message = "Location name is required")
    private String name;
    
    private String address;
    
    @NotBlank(message = "City is required")
    private String city;
    
    private String province;
    
    private String postalCode;
    
    private Double latitude;
    
    private Double longitude;
    
    /**
     * Returns a formatted full address string
     * @return Complete address as a single string
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        
        if (address != null && !address.isEmpty()) {
            sb.append(address);
        }
        
        if (city != null && !city.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }
        
        if (province != null && !province.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(province);
        }
        
        if (postalCode != null && !postalCode.isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(postalCode);
        }
        
        return sb.toString();
    }
    
    /**
     * Checks if the location has valid coordinates
     * @return true if both latitude and longitude are not null
     */
    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }
}
