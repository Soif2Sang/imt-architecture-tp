package imt.nord.europe.architecture.tp.infrastructure.event.handler;

import imt.nord.europe.architecture.tp.business.contract.services.ContractService;
import imt.nord.europe.architecture.tp.common.enums.ContractStatus;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.ContractEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.repository.ContractRepository;
import imt.nord.europe.architecture.tp.infrastructure.event.VehicleBreakdownEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Gère les événements de rupture de véhicule.
 * Lorsqu'un véhicule est déclaré en panne, tous ses contrats en attente sont automatiquement annulés.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VehicleBreakdownEventHandler {
    
    private final ContractRepository contractRepository;
    private final ContractService contractService;
    
    /**
     * Écoute les événements VehicleBreakdownEvent et annule les contrats en attente.
     * 
     * @param event l'événement de rupture de véhicule
     */
    @EventListener
    @Transactional
    public void onVehicleBreakdown(VehicleBreakdownEvent event) {
        Long vehicleId = event.getVehicleId();
        
        log.info("Traitement de la rupture du véhicule {}", vehicleId);
        
        // Récupérer tous les contrats en attente pour ce véhicule
        var pendingContracts = contractRepository.findByVehicleIdAndStatus(vehicleId, ContractStatus.PENDING);
        
        // Annuler tous les contrats en attente
        for (var contract : pendingContracts) {
            log.info("Annulation du contrat {} en attente pour le véhicule en panne {}", 
                     contract.getId(), vehicleId);
            contractService.cancelContract(contract.getId());
        }
        
        if (!pendingContracts.isEmpty()) {
            log.warn("{} contrat(s) annulé(s) suite à la panne du véhicule {}", 
                     pendingContracts.size(), vehicleId);
        }
    }
}
