package imt.nord.europe.architecture.tp.business.client.validators;

import imt.nord.europe.architecture.tp.common.exceptions.DuplicateClientException;
import imt.nord.europe.architecture.tp.common.exceptions.ValidationException;
import imt.nord.europe.architecture.tp.common.utils.ValidationUtils;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.ClientEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Validateur métier pour les clients.
 * Vérifie les règles de gestion suivantes :
 * 1. Un client doit être unique par (firstName, lastName, dateOfBirth)
 * 2. Deux clients distincts ne peuvent pas avoir le même numéro de permis
 */
@Component
@RequiredArgsConstructor
public class ClientValidator {
    
    private final ClientRepository clientRepository;
    
    /**
     * Valide un client lors de sa création.
     * 
     * @param firstName Le prénom du client
     * @param lastName Le nom du client
     * @param dateOfBirth La date de naissance du client
     * @param licenseNumber Le numéro de permis
     * @throws ValidationException si les données sont invalides
     * @throws DuplicateClientException si un client en double est détecté
     */
    public void validateForCreation(String firstName, String lastName, LocalDate dateOfBirth, String licenseNumber) {
        validateBasicFields(firstName, lastName, dateOfBirth, licenseNumber);
        validateClientUniqueness(firstName, lastName, dateOfBirth, null);
        validateLicenseNumberUniqueness(licenseNumber, null);
    }
    
    /**
     * Valide un client lors de sa modification.
     * 
     * @param clientId L'ID du client à modifier
     * @param firstName Le prénom du client
     * @param lastName Le nom du client
     * @param dateOfBirth La date de naissance du client
     * @param licenseNumber Le numéro de permis
     * @throws ValidationException si les données sont invalides
     * @throws DuplicateClientException si un autre client en double est détecté
     */
    public void validateForUpdate(Long clientId, String firstName, String lastName, LocalDate dateOfBirth, String licenseNumber) {
        ValidationUtils.notNull(clientId, "L'ID du client ne peut pas être null");
        validateBasicFields(firstName, lastName, dateOfBirth, licenseNumber);
        validateClientUniqueness(firstName, lastName, dateOfBirth, clientId);
        validateLicenseNumberUniqueness(licenseNumber, clientId);
    }
    
    /**
     * Valide les champs basiques du client.
     */
    private void validateBasicFields(String firstName, String lastName, LocalDate dateOfBirth, String licenseNumber) {
        ValidationUtils.notEmpty(firstName, "Le prénom du client ne peut pas être vide");
        ValidationUtils.notEmpty(lastName, "Le nom du client ne peut pas être vide");
        ValidationUtils.notNull(dateOfBirth, "La date de naissance ne peut pas être null");
        ValidationUtils.notEmpty(licenseNumber, "Le numéro de permis ne peut pas être vide");
        
        // Vérifier que la date de naissance n'est pas dans le futur
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new ValidationException("La date de naissance ne peut pas être dans le futur");
        }
        
        // Vérifier que le client a au moins 18 ans
        LocalDate minBirthDate = LocalDate.now().minusYears(18);
        if (dateOfBirth.isAfter(minBirthDate)) {
            throw new ValidationException("L'âge du client doit être >= 18 ans");
        }
    }
    
    /**
     * Valide l'unicité d'un client par (firstName, lastName, dateOfBirth).
     * Un client peut avoir un certain ID pour exclure le client lui-même de la vérification (en cas de modification).
     * 
     * @param firstName Le prénom
     * @param lastName Le nom
     * @param dateOfBirth La date de naissance
     * @param excludeClientId L'ID du client à exclure (null si création)
     * @throws DuplicateClientException si un client en double est détecté
     */
    private void validateClientUniqueness(String firstName, String lastName, LocalDate dateOfBirth, Long excludeClientId) {
        Optional<ClientEntity> existingClient = clientRepository.findByFirstNameAndLastNameAndDateOfBirth(firstName, lastName, dateOfBirth);
        
        if (existingClient.isPresent()) {
            // Si on est en modification et c'est le même client, c'est OK
            if (excludeClientId != null && existingClient.get().getId().equals(excludeClientId)) {
                return;
            }
            throw DuplicateClientException.byIdentity(firstName, lastName, dateOfBirth.toString());
        }
    }
    
    /**
     * Valide l'unicité du numéro de permis.
     * 
     * @param licenseNumber Le numéro de permis
     * @param excludeClientId L'ID du client à exclure (null si création)
     * @throws DuplicateClientException si le numéro de permis existe déjà pour un autre client
     */
    private void validateLicenseNumberUniqueness(String licenseNumber, Long excludeClientId) {
        Optional<ClientEntity> existingClient = clientRepository.findByLicenseNumber(licenseNumber);
        
        if (existingClient.isPresent()) {
            // Si on est en modification et c'est le même client, c'est OK
            if (excludeClientId != null && existingClient.get().getId().equals(excludeClientId)) {
                return;
            }
            throw DuplicateClientException.byLicenseNumber(licenseNumber);
        }
    }
}
