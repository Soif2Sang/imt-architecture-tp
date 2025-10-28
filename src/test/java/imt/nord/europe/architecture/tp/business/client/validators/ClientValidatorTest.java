package imt.nord.europe.architecture.tp.business.client.validators;

import imt.nord.europe.architecture.tp.common.exceptions.ValidationException;
import imt.nord.europe.architecture.tp.infrastructure.db.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour le validateur de clients.
 * Couvre la validation des données personnelles et les contrôles d'âge.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClientValidator Tests")
class ClientValidatorTest {

    private ClientValidator clientValidator;

    @Mock
    private ClientRepository clientRepository;

    private LocalDate validBirthDate;
    private LocalDate futureBirthDate;
    private LocalDate tooYoungBirthDate;

    @BeforeEach
    void setUp() {
        clientValidator = new ClientValidator(clientRepository);
        
        validBirthDate = LocalDate.now().minusYears(30);
        futureBirthDate = LocalDate.now().plusDays(1);
        tooYoungBirthDate = LocalDate.now().minusYears(17);
    }

    // ============================================
    // Tests de validation pour la création
    // ============================================

    @Test
    @DisplayName("Création valide avec tous les paramètres corrects")
    void testValidateForCreation_Success() {
        assertDoesNotThrow(() -> clientValidator.validateForCreation(
            "Jean",
            "Dupont",
            validBirthDate,
            "1234567890"
        ));
    }

    @Test
    @DisplayName("Création échoue avec firstName vide")
    void testValidateForCreation_EmptyFirstName() {
        assertThrows(ValidationException.class,
            () -> clientValidator.validateForCreation(
                "",
                "Dupont",
                validBirthDate,
                "1234567890"
            ),
            "Le prénom du client ne peut pas être vide");
    }

    @Test
    @DisplayName("Création échoue avec lastName vide")
    void testValidateForCreation_EmptyLastName() {
        assertThrows(ValidationException.class,
            () -> clientValidator.validateForCreation(
                "Jean",
                "",
                validBirthDate,
                "1234567890"
            ),
            "Le nom du client ne peut pas être vide");
    }

    @Test
    @DisplayName("Création échoue avec dateOfBirth null")
    void testValidateForCreation_NullBirthDate() {
        assertThrows(ValidationException.class,
            () -> clientValidator.validateForCreation(
                "Jean",
                "Dupont",
                null,
                "1234567890"
            ),
            "La date de naissance ne peut pas être null");
    }

    @Test
    @DisplayName("Création échoue avec licenseNumber vide")
    void testValidateForCreation_EmptyLicenseNumber() {
        assertThrows(ValidationException.class,
            () -> clientValidator.validateForCreation(
                "Jean",
                "Dupont",
                validBirthDate,
                ""
            ),
            "Le numéro de permis ne peut pas être vide");
    }

    @Test
    @DisplayName("Création échoue si dateOfBirth est dans le futur")
    void testValidateForCreation_FutureBirthDate() {
        assertThrows(ValidationException.class,
            () -> clientValidator.validateForCreation(
                "Jean",
                "Dupont",
                futureBirthDate,
                "1234567890"
            ),
            "La date de naissance ne peut pas être dans le futur");
    }

    @Test
    @DisplayName("Création échoue si le client est trop jeune (< 18 ans)")
    void testValidateForCreation_TooYoung() {
        assertThrows(ValidationException.class,
            () -> clientValidator.validateForCreation(
                "Jean",
                "Dupont",
                tooYoungBirthDate,
                "1234567890"
            ),
            "L'âge du client doit être >= 18 ans");
    }

    // ============================================
    // Tests de validation pour la modification
    // ============================================

    @Test
    @DisplayName("Modification valide avec tous les paramètres corrects")
    void testValidateForUpdate_Success() {
        assertDoesNotThrow(() -> clientValidator.validateForUpdate(
            1L,
            "Jean",
            "Dupont",
            validBirthDate,
            "1234567890"
        ));
    }

    @Test
    @DisplayName("Modification échoue avec clientId null")
    void testValidateForUpdate_NullClientId() {
        assertThrows(ValidationException.class,
            () -> clientValidator.validateForUpdate(
                null,
                "Jean",
                "Dupont",
                validBirthDate,
                "1234567890"
            ),
            "L'ID du client ne peut pas être null");
    }

    @Test
    @DisplayName("Modification échoue avec firstName vide")
    void testValidateForUpdate_EmptyFirstName() {
        assertThrows(ValidationException.class,
            () -> clientValidator.validateForUpdate(
                1L,
                "",
                "Dupont",
                validBirthDate,
                "1234567890"
            ));
    }

    @Test
    @DisplayName("Modification échoue si le client est trop jeune")
    void testValidateForUpdate_TooYoung() {
        assertThrows(ValidationException.class,
            () -> clientValidator.validateForUpdate(
                1L,
                "Jean",
                "Dupont",
                tooYoungBirthDate,
                "1234567890"
            ));
    }
}
