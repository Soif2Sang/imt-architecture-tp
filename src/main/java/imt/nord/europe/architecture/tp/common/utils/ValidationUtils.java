package imt.nord.europe.architecture.tp.common.utils;

import imt.nord.europe.architecture.tp.common.exceptions.ValidationException;

/**
 * Utilitaires pour la validation des données.
 * Fournit des méthodes statiques pour vérifier les conditions courantes.
 */
public class ValidationUtils {
    
    private ValidationUtils() {
        // Classe utilitaire, ne pas instancier
    }
    
    /**
     * Vérifie qu'un objet n'est pas null.
     * 
     * @param object l'objet à vérifier
     * @param message le message d'erreur si null
     * @throws ValidationException si l'objet est null
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new ValidationException(message);
        }
    }
    
    /**
     * Vérifie qu'une chaîne n'est pas vide.
     * 
     * @param value la chaîne à vérifier
     * @param message le message d'erreur si vide ou null
     * @throws ValidationException si la chaîne est vide ou null
     */
    public static void notEmpty(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(message);
        }
    }
    
    /**
     * Vérifie une condition booléenne.
     * 
     * @param condition la condition à vérifier
     * @param message le message d'erreur si faux
     * @throws ValidationException si la condition est fausse
     */
    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            throw new ValidationException(message);
        }
    }
    
    /**
     * Vérifie une condition booléenne (négative).
     * 
     * @param condition la condition à vérifier (doit être faux)
     * @param message le message d'erreur si vrai
     * @throws ValidationException si la condition est vraie
     */
    public static void isFalse(boolean condition, String message) {
        if (condition) {
            throw new ValidationException(message);
        }
    }
}
