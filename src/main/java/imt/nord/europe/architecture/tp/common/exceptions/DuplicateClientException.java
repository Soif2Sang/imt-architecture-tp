package imt.nord.europe.architecture.tp.common.exceptions;

/**
 * Exception levée lorsqu'un client en double est détecté.
 * Un client est considéré en double s'il existe déjà avec le même (firstName, lastName, dateOfBirth)
 * ou le même licenseNumber.
 */
public class DuplicateClientException extends BusinessException {


    public DuplicateClientException(String message) {
        super(message);
    }
    
    public static DuplicateClientException byIdentity(String firstName, String lastName, String dateOfBirth) {
        return new DuplicateClientException(
            String.format("Un client avec l'identité '%s %s' (date de naissance: %s) existe déjà.", 
                firstName, lastName, dateOfBirth)
        );
    }
    
    public static DuplicateClientException byLicenseNumber(String licenseNumber) {
        return new DuplicateClientException(
            String.format("Un client avec le numéro de permis '%s' existe déjà.", licenseNumber)
        );
    }
}
