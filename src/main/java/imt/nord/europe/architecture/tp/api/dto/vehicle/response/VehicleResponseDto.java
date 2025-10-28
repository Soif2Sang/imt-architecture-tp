package imt.nord.europe.architecture.tp.api.dto.vehicle.response;

import imt.nord.europe.architecture.tp.common.enums.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de réponse pour un véhicule.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponseDto {
    
    private Long id;
    private String registrationPlate;
    private String brand;
    private String model;
    private String motorization;
    private String color;
    private LocalDate acquisitionDate;
    private VehicleStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
