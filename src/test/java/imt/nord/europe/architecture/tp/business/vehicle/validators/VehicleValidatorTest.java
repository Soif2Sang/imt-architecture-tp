package imt.nord.europe.architecture.tp.business.vehicle.validators;

import imt.nord.europe.architecture.tp.common.enums.VehicleStatus;
import imt.nord.europe.architecture.tp.common.exceptions.ValidationException;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.VehicleEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests pour le validateur de véhicules.
 * Couvre la validation des données et les contrôles d'existence et de statut.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleValidator Tests")
class VehicleValidatorTest {

    private VehicleValidator vehicleValidator;

    @Mock
    private VehicleRepository vehicleRepository;

    private LocalDate validAcquisitionDate;
    private LocalDate futureAcquisitionDate;

    @BeforeEach
    void setUp() {
        vehicleValidator = new VehicleValidator(vehicleRepository);
        
        validAcquisitionDate = LocalDate.now().minusYears(2);
        futureAcquisitionDate = LocalDate.now().plusDays(1);
    }

    // ============================================
    // Tests de validation pour la création
    // ============================================

    @Test
    @DisplayName("Création valide avec tous les paramètres corrects")
    void testValidateForCreation_Success() {
        assertDoesNotThrow(() -> vehicleValidator.validateForCreation(
            "AB-123-CD",
            "Peugeot",
            "3008",
            validAcquisitionDate
        ));
    }

    @Test
    @DisplayName("Création échoue avec registrationPlate vide")
    void testValidateForCreation_EmptyRegistrationPlate() {
        assertThrows(ValidationException.class,
            () -> vehicleValidator.validateForCreation(
                "",
                "Peugeot",
                "3008",
                validAcquisitionDate
            ));
    }

    @Test
    @DisplayName("Création échoue avec brand vide")
    void testValidateForCreation_EmptyBrand() {
        assertThrows(ValidationException.class,
            () -> vehicleValidator.validateForCreation(
                "AB-123-CD",
                "",
                "3008",
                validAcquisitionDate
            ));
    }

    @Test
    @DisplayName("Création échoue avec model vide")
    void testValidateForCreation_EmptyModel() {
        assertThrows(ValidationException.class,
            () -> vehicleValidator.validateForCreation(
                "AB-123-CD",
                "Peugeot",
                "",
                validAcquisitionDate
            ));
    }

    @Test
    @DisplayName("Création échoue avec acquisitionDate null")
    void testValidateForCreation_NullAcquisitionDate() {
        assertThrows(ValidationException.class,
            () -> vehicleValidator.validateForCreation(
                "AB-123-CD",
                "Peugeot",
                "3008",
                null
            ));
    }

    @Test
    @DisplayName("Création échoue si acquisitionDate est dans le futur")
    void testValidateForCreation_FutureAcquisitionDate() {
        assertThrows(ValidationException.class,
            () -> vehicleValidator.validateForCreation(
                "AB-123-CD",
                "Peugeot",
                "3008",
                futureAcquisitionDate
            ),
            "La date d'acquisition ne peut pas être dans le futur");
    }

    // ============================================
    // Tests de validation pour la modification
    // ============================================

    @Test
    @DisplayName("Modification valide avec tous les paramètres corrects")
    void testValidateForUpdate_Success() {
        assertDoesNotThrow(() -> vehicleValidator.validateForUpdate(
            1L,
            "AB-123-CD",
            "Peugeot",
            "3008",
            validAcquisitionDate
        ));
    }

    @Test
    @DisplayName("Modification échoue avec vehicleId null")
    void testValidateForUpdate_NullVehicleId() {
        assertThrows(ValidationException.class,
            () -> vehicleValidator.validateForUpdate(
                null,
                "AB-123-CD",
                "Peugeot",
                "3008",
                validAcquisitionDate
            ));
    }

    @Test
    @DisplayName("Modification échoue avec brand vide")
    void testValidateForUpdate_EmptyBrand() {
        assertThrows(ValidationException.class,
            () -> vehicleValidator.validateForUpdate(
                1L,
                "AB-123-CD",
                "",
                "3008",
                validAcquisitionDate
            ));
    }

    @Test
    @DisplayName("Modification échoue si acquisitionDate est dans le futur")
    void testValidateForUpdate_FutureAcquisitionDate() {
        assertThrows(ValidationException.class,
            () -> vehicleValidator.validateForUpdate(
                1L,
                "AB-123-CD",
                "Peugeot",
                "3008",
                futureAcquisitionDate
            ));
    }
}
