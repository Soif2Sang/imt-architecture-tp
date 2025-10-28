package imt.nord.europe.architecture.tp.common.exceptions;

/**
 * Exception levée lors d'une violation de règles de validation métier.
 * Utilisée pour signaler des erreurs de validation des données d'entrée.
 */
public class ValidationException extends BusinessException {
    
    /**
     * Crée une nouvelle exception de validation avec un message.
     * 
     * @param message le message d'erreur
     */
    public ValidationException(String message) {
        super(message);
    }
}
