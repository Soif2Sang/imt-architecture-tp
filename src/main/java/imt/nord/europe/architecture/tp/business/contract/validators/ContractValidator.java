package imt.nord.europe.architecture.tp.business.contract.validators;

import imt.nord.europe.architecture.tp.common.enums.ContractStatus;
import imt.nord.europe.architecture.tp.common.exceptions.ContractConflictException;
import imt.nord.europe.architecture.tp.common.exceptions.ValidationException;
import imt.nord.europe.architecture.tp.common.utils.ValidationUtils;
import imt.nord.europe.architecture.tp.common.enums.VehicleStatus;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.ClientEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.ContractEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.VehicleEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.repository.ClientRepository;
import imt.nord.europe.architecture.tp.infrastructure.db.repository.ContractRepository;
import imt.nord.europe.architecture.tp.infrastructure.db.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Validateur métier pour les contrats (locations).
 * Vérifie les règles de gestion suivantes :
 * 1. Un client peut louer plusieurs véhicules sur la même période
 * 2. Un véhicule ne peut être loué que par un seul client sur une période donnée
 * 3. Les véhicules en panne ne peuvent pas être loués
 * 4. Les dates de fin doivent être après les dates de début
 */
@Component
@RequiredArgsConstructor
public class ContractValidator {
    
    private final ContractRepository contractRepository;
    private final ClientRepository clientRepository;
    private final VehicleRepository vehicleRepository;
    
    /**
     * Valide un contrat lors de sa création.
     * 
     * @param clientId L'ID du client
     * @param vehicleId L'ID du véhicule
     * @param startDate La date de début de location
     * @param endDate La date de fin de location
     * @throws ValidationException si les données sont invalides
     * @throws ContractConflictException si un conflit de réservation est détecté
     */
    public void validateForCreation(Long clientId, Long vehicleId, LocalDateTime startDate, LocalDateTime endDate) {
        validateBasicFields(clientId, vehicleId, startDate, endDate);
        validateClientExists(clientId);
        validateVehicleExists(vehicleId);
        validateVehicleNotBrokenDown(vehicleId);
        validateNoConflictingContracts(vehicleId, startDate, endDate, null);
    }
    
    /**
     * Valide un contrat lors de sa modification.
     * 
     * @param contractId L'ID du contrat à modifier
     * @param clientId L'ID du client
     * @param vehicleId L'ID du véhicule
     * @param startDate La date de début de location
     * @param endDate La date de fin de location
     * @throws ValidationException si les données sont invalides
     * @throws ContractConflictException si un conflit de réservation est détecté
     */
    public void validateForUpdate(Long contractId, Long clientId, Long vehicleId, LocalDateTime startDate, LocalDateTime endDate) {
        ValidationUtils.notNull(contractId, "L'ID du contrat ne peut pas être null");
        validateBasicFields(clientId, vehicleId, startDate, endDate);
        validateClientExists(clientId);
        validateVehicleExists(vehicleId);
        validateVehicleNotBrokenDown(vehicleId);
        validateNoConflictingContracts(vehicleId, startDate, endDate, contractId);
    }
    
    /**
     * Valide les champs basiques du contrat.
     */
    private void validateBasicFields(Long clientId, Long vehicleId, LocalDateTime startDate, LocalDateTime endDate) {
        ValidationUtils.notNull(clientId, "L'ID du client ne peut pas être null");
        ValidationUtils.notNull(vehicleId, "L'ID du véhicule ne peut pas être null");
        ValidationUtils.notNull(startDate, "La date de début ne peut pas être null");
        ValidationUtils.notNull(endDate, "La date de fin ne peut pas être null");
        
        // Vérifier que la date de fin est après la date de début
        if (!endDate.isAfter(startDate)) {
            throw new ValidationException("La date de fin doit être après la date de début");
        }
        
        // Vérifier que les dates ne sont pas dans le passé
        LocalDateTime now = LocalDateTime.now();
        if (startDate.isBefore(now)) {
            throw new ValidationException("La date de début ne peut pas être dans le passé");
        }
    }
    
    /**
     * Valide que le client existe.
     * 
     * @param clientId L'ID du client
     * @throws ValidationException si le client n'existe pas
     */
    private void validateClientExists(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new ValidationException("Le client avec l'ID " + clientId + " n'existe pas");
        }
    }
    
    /**
     * Valide que le véhicule existe.
     * 
     * @param vehicleId L'ID du véhicule
     * @throws ValidationException si le véhicule n'existe pas
     */
    private void validateVehicleExists(Long vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new ValidationException("Le véhicule avec l'ID " + vehicleId + " n'existe pas");
        }
    }
    
    /**
     * Valide que le véhicule n'est pas en panne.
     * 
     * @param vehicleId L'ID du véhicule
     * @throws ContractConflictException si le véhicule est en panne
     */
    private void validateVehicleNotBrokenDown(Long vehicleId) {
        Optional<VehicleEntity> vehicle = vehicleRepository.findById(vehicleId);
        
        if (vehicle.isPresent() && VehicleStatus.BROKEN_DOWN.equals(vehicle.get().getStatus())) {
            throw ContractConflictException.overlappingPeriod(
                vehicle.get().getRegistrationPlate(),
                "N/A (véhicule en panne)",
                "N/A"
            );
        }
    }
    
    /**
     * Valide qu'il n'y a pas de conflit de réservation pour le véhicule sur la période demandée.
     * Un conflit existe si un contrat avec un statut PENDING, ONGOING ou OVERDUE chevauche la période.
     * 
     * @param vehicleId L'ID du véhicule
     * @param startDate La date de début demandée
     * @param endDate La date de fin demandée
     * @param excludeContractId L'ID du contrat à exclure (null si création)
     * @throws ContractConflictException si un conflit est détecté
     */
    private void validateNoConflictingContracts(Long vehicleId, LocalDateTime startDate, LocalDateTime endDate, Long excludeContractId) {
        List<ContractEntity> conflictingContracts = contractRepository.findConflictingContracts(vehicleId, startDate, endDate);
        
        for (ContractEntity contract : conflictingContracts) {
            // Si on est en modification et c'est le même contrat, c'est OK
            if (excludeContractId != null && contract.getId().equals(excludeContractId)) {
                continue;
            }
            
            Optional<VehicleEntity> vehicle = vehicleRepository.findById(vehicleId);
            if (vehicle.isPresent()) {
                throw ContractConflictException.overlappingPeriod(
                    vehicle.get().getRegistrationPlate(),
                    startDate.toString(),
                    endDate.toString()
                );
            }
        }
    }
    
    /**
     * Valide une transition de statut selon la machine d'état.
     * 
     * Machine d'état stricte :
     * - PENDING → ONGOING (approve), CANCELLED
     * - ONGOING → COMPLETED, OVERDUE
     * - OVERDUE → CANCELLED
     * - COMPLETED, CANCELLED → (terminal, pas de transitions)
     * 
     * @param currentStatus le statut actuel du contrat
     * @param newStatus le nouveau statut demandé
     * @throws ValidationException si la transition n'est pas autorisée
     */
    public void validateStatusTransition(ContractStatus currentStatus, ContractStatus newStatus) {
        // Un contrat ne peut pas garder le même statut
        if (currentStatus == newStatus) {
            throw new ValidationException("Le contrat a déjà le statut " + newStatus);
        }
        
        switch (currentStatus) {
            case PENDING:
                // PENDING peut aller vers ONGOING ou CANCELLED
                if (newStatus != ContractStatus.ONGOING && newStatus != ContractStatus.CANCELLED) {
                    throw new ValidationException(
                        "Un contrat en attente ne peut passer que à ONGOING (approuvé) ou CANCELLED (annulé). " +
                        "Transition demandée : " + newStatus);
                }
                break;
                
            case ONGOING:
                // ONGOING peut aller vers COMPLETED ou OVERDUE
                if (newStatus != ContractStatus.COMPLETED && newStatus != ContractStatus.OVERDUE) {
                    throw new ValidationException(
                        "Un contrat en cours ne peut passer que à COMPLETED (terminé) ou OVERDUE (en retard). " +
                        "Transition demandée : " + newStatus);
                }
                break;
                
            case OVERDUE:
                // OVERDUE peut seulement aller à CANCELLED
                if (newStatus != ContractStatus.CANCELLED) {
                    throw new ValidationException(
                        "Un contrat en retard ne peut être qu'annulé. " +
                        "Transition demandée : " + newStatus);
                }
                break;
                
            case COMPLETED:
            case CANCELLED:
                // Ces états sont terminaux, pas de transitions possibles
                throw new ValidationException(
                    "Un contrat avec le statut " + currentStatus + " ne peut pas être modifié. " +
                    "Cet état est terminal.");
        }
    }
}
