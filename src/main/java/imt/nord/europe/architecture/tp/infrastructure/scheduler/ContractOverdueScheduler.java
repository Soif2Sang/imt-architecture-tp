package imt.nord.europe.architecture.tp.infrastructure.scheduler;

import imt.nord.europe.architecture.tp.business.contract.services.ContractService;
import imt.nord.europe.architecture.tp.common.enums.ContractStatus;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.ContractEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.repository.ContractRepository;
import imt.nord.europe.architecture.tp.infrastructure.event.SpringEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler qui exécute quotidiennement les tâches de mise à jour des contrats.
 * - Vérifie les contrats en retard (endDate dépassée mais statut ONGOING)
 * - Annule les contrats en conflit (pour éviter les chevauchements)
 */
@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ContractOverdueScheduler {
    
    private final ContractRepository contractRepository;
    private final ContractService contractService;
    private final SpringEventPublisher eventPublisher;
    
    /**
     * S'exécute tous les jours à minuit (00:00).
     * Traite les contrats en retard et annule ceux qui empêchent d'autres contrats de démarrer.
     */
    @Scheduled(cron = "0 0 0 * * *") // Minuit tous les jours
    @Transactional
    public void updateOverdueContracts() {
        log.info("=== Début du traitement quotidien des contrats en retard ===");
        
        try {
            updateContractsInRetard();
            
            cancelConflictingContracts();

            log.info("=== Fin du traitement quotidien des contrats en retard ===");
        } catch (Exception e) {
            log.error("Erreur lors du traitement des contrats en retard", e);
        }
    }
    
    /**
     * Étape 1 : Identifie et marque les contrats en retard.
     * Un contrat est en retard si :
     * - Son statut est ONGOING
     * - Sa date de fin (endDate) est dépassée
     */
    private void updateContractsInRetard() {
        LocalDateTime now = LocalDateTime.now();
        
        // Récupérer directement les contrats ONGOING avec endDate dépassée via une requête SQL optimisée
        List<ContractEntity> overdueContracts = contractRepository.findOverdueOngoingContracts(now);
        
        if (!overdueContracts.isEmpty()) {
            log.warn("Traitement de {} contrat(s) en retard", overdueContracts.size());
            
            for (ContractEntity contract : overdueContracts) {
                log.info("Passage du contrat {} au statut OVERDUE (endDate: {})", 
                         contract.getId(), contract.getEndDate());
                
                // Mettre à jour le statut à OVERDUE
                contractService.markAsOverdue(contract.getId());
                
                // Publier l'événement
                eventPublisher.publishContractOverdueEvent(contract.getId());
            }
        } else {
            log.debug("Aucun contrat en retard détecté");
        }
    }
    
    /**
     * Étape 2 : Identifie et annule les contrats en retard qui empêchent le démarrage d'autres contrats.
     * Utilise une requête SQL optimisée pour récupérer directement les contrats OVERDUE qui bloquent des PENDING.
     */
    private void cancelConflictingContracts() {
        // Récupérer directement les contrats OVERDUE qui bloquent des contrats PENDING
        List<ContractEntity> conflictingContracts = contractRepository.findOverdueContractsThatBlockPendingContracts();
        
        if (!conflictingContracts.isEmpty()) {
            log.warn("Traitement de {} contrat(s) OVERDUE bloquant des contrats PENDING", conflictingContracts.size());
            
            for (ContractEntity overdue : conflictingContracts) {
                log.warn("Annulation du contrat {} en retard qui empêche des contrats PENDING de démarrer sur le véhicule {}",
                         overdue.getId(), overdue.getVehicle().getId());
                
                contractService.cancelContract(overdue.getId());
                eventPublisher.publishContractOverdueEvent(overdue.getId());
            }
        } else {
            log.debug("Aucun contrat OVERDUE bloquant détecté");
        }
    }
}
