package imt.nord.europe.architecture.tp.common.exceptions;

/**
 * Exception levée lorsqu'un véhicule ne peut pas être loué.
 * Les raisons peuvent être :
 * - Le véhicule est en panne (status BROKEN_DOWN)
 * - Le véhicule est déjà loué pour la période demandée
 * - Le véhicule n'existe pas
 */
public class VehicleNotAvailableException extends BusinessException {
    
    public VehicleNotAvailableException(String message) {
        super(message);
    }
    
    public static VehicleNotAvailableException brokenDown(String registrationPlate) {
        return new VehicleNotAvailableException(
            String.format("Le véhicule '%s' est en panne et ne peut pas être loué.", registrationPlate)
        );
    }
    
    public static VehicleNotAvailableException alreadyRented(String registrationPlate) {
        return new VehicleNotAvailableException(
            String.format("Le véhicule '%s' est déjà loué pour la période demandée.", registrationPlate)
        );
    }
}
