package imt.nord.europe.architecture.tp.api.dto.vehicle.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de requête pour la création/modification d'un véhicule.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleRequestDto {
    
    private String registrationPlate;
    private String brand;
    private String model;
    private String motorization;
    private String color;
    private LocalDate acquisitionDate;
}
