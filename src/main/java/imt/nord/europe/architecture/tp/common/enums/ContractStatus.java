package imt.nord.europe.architecture.tp.common.enums;

/**
 * Énumération des statuts possibles d'un contrat de location.
 */
public enum ContractStatus {
    
    /**
     * En attente - Le contrat vient d'être créé mais n'a pas encore commencé
     */
    PENDING("En attente"),
    
    /**
     * En cours - Le contrat est actif (startDate <= now <= endDate)
     */
    ONGOING("En cours"),
    
    /**
     * Terminé - Le contrat s'est terminé normalement
     */
    COMPLETED("Terminé"),
    
    /**
     * En retard - La date de fin a été dépassée mais le contrat n'a pas été clôturé
     */
    OVERDUE("En retard"),
    
    /**
     * Annulé - Le contrat a été annulé
     */
    CANCELLED("Annulé");
    
    private final String label;
    
    ContractStatus(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}
