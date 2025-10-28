package imt.nord.europe.architecture.tp.common.exceptions;

/**
 * Exception levée lorsqu'il existe un conflit de contrat.
 * Cela se produit lorsqu'on tente de créer un contrat pour une période
 * qui chevauche une période déjà loué pour le même véhicule.
 */
public class ContractConflictException extends BusinessException {
    
    public ContractConflictException(String message) {
        super(message);
    }
    
    public static ContractConflictException overlappingPeriod(String vehicleRegistration, String startDate, String endDate) {
        return new ContractConflictException(
            String.format("Le véhicule '%s' est déjà réservé ou loué pour la période du %s au %s.", 
                vehicleRegistration, startDate, endDate)
        );
    }
}
