package imt.nord.europe.architecture.tp.business.vehicle.validators;

import imt.nord.europe.architecture.tp.common.exceptions.ValidationException;
import imt.nord.europe.architecture.tp.common.exceptions.VehicleNotAvailableException;
import imt.nord.europe.architecture.tp.common.utils.ValidationUtils;
import imt.nord.europe.architecture.tp.common.enums.VehicleStatus;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.VehicleEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Validateur métier pour les véhicules.
 * Vérifie les règles de gestion suivantes :
 * 1. Un véhicule doit être unique par numéro d'immatriculation
 * 2. Un véhicule en panne ne peut pas être loué
 */
@Component
@RequiredArgsConstructor
public class VehicleValidator {
    
    private final VehicleRepository vehicleRepository;
    
    /**
     * Valide un véhicule lors de sa création.
     * 
     * @param registrationPlate Le numéro d'immatriculation
     * @param brand La marque du véhicule
     * @param model Le modèle du véhicule
     * @param acquisitionDate La date d'acquisition
     * @throws ValidationException si les données sont invalides
     * @throws VehicleNotAvailableException si un conflit est détecté
     */
    public void validateForCreation(String registrationPlate, String brand, String model, LocalDate acquisitionDate) {
        validateBasicFields(registrationPlate, brand, model, acquisitionDate);
        validateRegistrationPlateUniqueness(registrationPlate, null);
    }
    
    /**
     * Valide un véhicule lors de sa modification.
     * 
     * @param vehicleId L'ID du véhicule à modifier
     * @param registrationPlate Le numéro d'immatriculation
     * @param brand La marque du véhicule
     * @param model Le modèle du véhicule
     * @param acquisitionDate La date d'acquisition
     * @throws ValidationException si les données sont invalides
     * @throws VehicleNotAvailableException si un conflit est détecté
     */
    public void validateForUpdate(Long vehicleId, String registrationPlate, String brand, String model, LocalDate acquisitionDate) {
        ValidationUtils.notNull(vehicleId, "L'ID du véhicule ne peut pas être null");
        validateBasicFields(registrationPlate, brand, model, acquisitionDate);
        validateRegistrationPlateUniqueness(registrationPlate, vehicleId);
    }
    
    /**
     * Valide qu'un véhicule est disponible pour la location.
     * Un véhicule est disponible si son statut n'est pas BROKEN_DOWN.
     * 
     * @param vehicleId L'ID du véhicule
     * @throws ResourceNotFoundException si le véhicule n'existe pas
     * @throws VehicleNotAvailableException si le véhicule est en panne
     */
    public void validateAvailabilityForRental(Long vehicleId) {
        ValidationUtils.notNull(vehicleId, "L'ID du véhicule ne peut pas être null");
        
        Optional<VehicleEntity> vehicle = vehicleRepository.findById(vehicleId);
        if (vehicle.isEmpty()) {
            throw new ValidationException("Le véhicule avec l'ID " + vehicleId + " n'existe pas");
        }
        
        VehicleEntity vehicleEntity = vehicle.get();
        if (VehicleStatus.BROKEN_DOWN.equals(vehicleEntity.getStatus())) {
            throw VehicleNotAvailableException.brokenDown(vehicleEntity.getRegistrationPlate());
        }
    }
    
    /**
     * Valide les champs basiques du véhicule.
     */
    private void validateBasicFields(String registrationPlate, String brand, String model, LocalDate acquisitionDate) {
        ValidationUtils.notEmpty(registrationPlate, "Le numéro d'immatriculation ne peut pas être vide");
        ValidationUtils.notEmpty(brand, "La marque du véhicule ne peut pas être vide");
        ValidationUtils.notEmpty(model, "Le modèle du véhicule ne peut pas être vide");
        ValidationUtils.notNull(acquisitionDate, "La date d'acquisition ne peut pas être null");
        
        // Vérifier que la date d'acquisition n'est pas dans le futur
        if (acquisitionDate.isAfter(LocalDate.now())) {
            throw new ValidationException("La date d'acquisition ne peut pas être dans le futur");
        }
    }
    
    /**
     * Valide l'unicité du numéro d'immatriculation.
     * 
     * @param registrationPlate Le numéro d'immatriculation
     * @param excludeVehicleId L'ID du véhicule à exclure (null si création)
     * @throws VehicleNotAvailableException si le numéro d'immatriculation existe déjà
     */
    private void validateRegistrationPlateUniqueness(String registrationPlate, Long excludeVehicleId) {
        Optional<VehicleEntity> existingVehicle = vehicleRepository.findByRegistrationPlate(registrationPlate);
        
        if (existingVehicle.isPresent()) {
            // Si on est en modification et c'est le même véhicule, c'est OK
            if (excludeVehicleId != null && existingVehicle.get().getId().equals(excludeVehicleId)) {
                return;
            }
            throw new VehicleNotAvailableException(
                "Un véhicule avec le numéro d'immatriculation '" + registrationPlate + "' existe déjà"
            );
        }
    }
}
