package imt.nord.europe.architecture.tp.infrastructure.event;

import org.springframework.context.ApplicationEvent;

/**
 * Événement déclenché lorsqu'un contrat passe en retard.
 * Utilisé par le système de planification pour gérer les contrats expirés.
 */
public class ContractOverdueEvent extends ApplicationEvent {
    
    private final Long contractId;
    
    public ContractOverdueEvent(Object source, Long contractId) {
        super(source);
        this.contractId = contractId;
    }
    
    public Long getContractId() {
        return contractId;
    }
}
