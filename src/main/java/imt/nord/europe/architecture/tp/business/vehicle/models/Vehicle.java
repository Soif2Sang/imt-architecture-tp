package imt.nord.europe.architecture.tp.business.vehicle.models;

import imt.nord.europe.architecture.tp.common.enums.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modèle de domaine pour un véhicule.
 * Représente un véhicule dans la couche métier, indépendant de la représentation JPA.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {
    
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
    
    /**
     * Retourne une description complète du véhicule.
     * 
     * @return la description (marque + modèle + immatriculation)
     */
    public String getDescription() {
        return brand + " " + model + " (" + registrationPlate + ")";
    }
    
    /**
     * Vérifie si le véhicule est disponible à la location.
     * 
     * @return true si le véhicule n'est pas en panne
     */
    public boolean isAvailableForRental() {
        return status != VehicleStatus.BROKEN_DOWN;
    }
}
