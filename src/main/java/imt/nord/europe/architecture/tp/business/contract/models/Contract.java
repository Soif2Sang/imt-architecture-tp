package imt.nord.europe.architecture.tp.business.contract.models;

import imt.nord.europe.architecture.tp.common.enums.ContractStatus;
import imt.nord.europe.architecture.tp.business.client.models.Client;
import imt.nord.europe.architecture.tp.business.vehicle.models.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Modèle de domaine pour un contrat de location.
 * Représente une location dans la couche métier, indépendant de la représentation JPA.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {
    
    private Long id;
    private Client client;
    private Vehicle vehicle;
    private Long clientId;
    private Long vehicleId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ContractStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Vérifie si la location est actuellement active.
     * 
     * @return true si le statut est ONGOING
     */
    public boolean isOngoing() {
        return status == ContractStatus.ONGOING;
    }
    
    /**
     * Vérifie si la location est en retard.
     * 
     * @return true si le statut est OVERDUE
     */
    public boolean isOverdue() {
        return status == ContractStatus.OVERDUE;
    }
    
    /**
     * Vérifie si la location est en attente.
     * 
     * @return true si le statut est PENDING
     */
    public boolean isPending() {
        return status == ContractStatus.PENDING;
    }
    
    /**
     * Vérifie si la location est terminée.
     * 
     * @return true si le statut est COMPLETED
     */
    public boolean isCompleted() {
        return status == ContractStatus.COMPLETED;
    }
    
    /**
     * Vérifie si la location est annulée.
     * 
     * @return true si le statut est CANCELLED
     */
    public boolean isCancelled() {
        return status == ContractStatus.CANCELLED;
    }
}
