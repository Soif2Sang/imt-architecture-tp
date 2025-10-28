package imt.nord.europe.architecture.tp.infrastructure.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Publie les événements métier vers le contexte Spring pour que les handlers les reçoivent.
 */
@Component
@RequiredArgsConstructor
public class SpringEventPublisher {
    
    private final ApplicationEventPublisher applicationEventPublisher;
    
    /**
     * Publie un événement de rupture de véhicule.
     * 
     * @param vehicleId l'ID du véhicule en panne
     */
    public void publishVehicleBreakdownEvent(Long vehicleId) {
        applicationEventPublisher.publishEvent(new VehicleBreakdownEvent(this, vehicleId));
    }
    
    /**
     * Publie un événement de contrat en retard ou annulé.
     * 
     * @param contractId l'ID du contrat
     */
    public void publishContractOverdueEvent(Long contractId) {
        applicationEventPublisher.publishEvent(new ContractOverdueEvent(this, contractId));
    }
}
