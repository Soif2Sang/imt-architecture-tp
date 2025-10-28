package imt.nord.europe.architecture.tp.infrastructure.event.handler;

import imt.nord.europe.architecture.tp.business.contract.services.ContractService;
import imt.nord.europe.architecture.tp.common.enums.ContractStatus;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.ContractEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.repository.ContractRepository;
import imt.nord.europe.architecture.tp.infrastructure.event.ContractOverdueEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Gère les événements de contrat en retard.
 * Effectue les actions nécessaires lors du passage en retard d'un contrat :
 * - Journalisation du retard
 * - Détection des impacts sur d'autres contrats
 * - Potentiellement calcul des frais de retard (extensible)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ContractOverdueEventHandler {
    
    private final ContractRepository contractRepository;
    private final ContractService contractService;
    
    /**
     * Écoute les événements ContractOverdueEvent.
     * Effectue les actions suivantes :
     * - Récupère les détails du contrat en retard
     * - Calcule le délai de retard
     * - Détecte les contrats suivants impactés
     * - Log les informations et impacts
     * 
     * @param event l'événement de contrat en retard
     */
    @EventListener
    @Transactional
    public void onContractOverdue(ContractOverdueEvent event) {
        Long contractId = event.getContractId();
        
        try {
            ContractEntity overdueContract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contrat non trouvé : " + contractId));
            
            // Calcul du retard
            LocalDateTime endDate = overdueContract.getEndDate();
            LocalDateTime now = LocalDateTime.now();
            long daysOverdue = ChronoUnit.DAYS.between(endDate, now);
            long hoursOverdue = ChronoUnit.HOURS.between(endDate, now);
            
            log.warn("⚠️ CONTRAT EN RETARD - ID: {} | Client: {} | Véhicule: {} | Retard: {} jours {} heures",
                     contractId,
                     overdueContract.getClient().getId(),
                     overdueContract.getVehicle().getId(),
                     daysOverdue,
                     hoursOverdue % 24);
            
            // Détails du contrat
            log.info("   - Date de fin attendue: {}", endDate);
            log.info("   - Date actuelle: {}", now);
            log.info("   - Statut: {}", overdueContract.getStatus());
            
            log.debug("🔍 Vérification des impacts du retard du contrat {}...", overdueContract.getId());
            
            var pendingVehicleContracts = contractRepository.findByVehicleIdAndStatus(overdueContract.getVehicle().getId(), ContractStatus.PENDING);
            
            for (ContractEntity pending : pendingVehicleContracts) {
                if (overdueContract.getEndDate().isAfter(pending.getStartDate())) {
                    log.warn("   ❌ Le contrat {} du client {} est bloqué - chevauchement avec le retard",
                            pending.getId(), pending.getClient().getId());
                    contractService.cancelContract(pending.getId());
                }
            }
            
        } catch (IllegalArgumentException e) {
            log.error("Erreur: contrat non trouvé lors du traitement du retard", e);
        } catch (Exception e) {
            log.error("Erreur lors de la gestion de l'événement de retard du contrat {}", contractId, e);
        }
    }
}
