package imt.nord.europe.architecture.tp.business.contract.validators;

import imt.nord.europe.architecture.tp.common.enums.ContractStatus;
import imt.nord.europe.architecture.tp.common.enums.VehicleStatus;
import imt.nord.europe.architecture.tp.common.exceptions.ContractConflictException;
import imt.nord.europe.architecture.tp.common.exceptions.ValidationException;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.ClientEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.ContractEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.VehicleEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.repository.ClientRepository;
import imt.nord.europe.architecture.tp.infrastructure.db.repository.ContractRepository;
import imt.nord.europe.architecture.tp.infrastructure.db.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests pour le validateur de contrats (locations).
 * Couvre la machine d'état, les validations de dates et les conflits de réservation.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ContractValidator Tests")
class ContractValidatorTest {

    private ContractValidator contractValidator;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    private ClientEntity testClient;
    private VehicleEntity testVehicle;
    private LocalDateTime now;
    private LocalDateTime future1;
    private LocalDateTime future2;

    @BeforeEach
    void setUp() {
        contractValidator = new ContractValidator(contractRepository, clientRepository, vehicleRepository);
        
        now = LocalDateTime.now();
        future1 = now.plusDays(1);
        future2 = now.plusDays(8);
        
        testClient = ClientEntity.builder()
            .id(1L)
            .firstName("Jean")
            .lastName("Dupont")
            .build();
        
        testVehicle = VehicleEntity.builder()
            .id(1L)
            .registrationPlate("AB-123-CD")
            .brand("Peugeot")
            .status(VehicleStatus.AVAILABLE)
            .build();
    }

    // ============================================
    // Tests de validation pour la création
    // ============================================

    @Test
    @DisplayName("Création valide avec tous les paramètres corrects")
    void testValidateForCreation_Success() {
        when(clientRepository.existsById(1L)).thenReturn(true);
        when(vehicleRepository.existsById(1L)).thenReturn(true);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(contractRepository.findConflictingContracts(1L, future1, future2)).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> contractValidator.validateForCreation(1L, 1L, future1, future2));
        
        verify(clientRepository).existsById(1L);
        verify(vehicleRepository).findById(1L);
        verify(contractRepository).findConflictingContracts(1L, future1, future2);
    }

    @Test
    @DisplayName("Création échoue avec clientId null")
    void testValidateForCreation_NullClientId() {
        assertThrows(ValidationException.class, 
            () -> contractValidator.validateForCreation(null, 1L, future1, future2),
            "L'ID du client ne peut pas être null");
    }

    @Test
    @DisplayName("Création échoue avec vehicleId null")
    void testValidateForCreation_NullVehicleId() {
        assertThrows(ValidationException.class,
            () -> contractValidator.validateForCreation(1L, null, future1, future2),
            "L'ID du véhicule ne peut pas être null");
    }

    @Test
    @DisplayName("Création échoue avec startDate null")
    void testValidateForCreation_NullStartDate() {
        assertThrows(ValidationException.class,
            () -> contractValidator.validateForCreation(1L, 1L, null, future2),
            "La date de début ne peut pas être null");
    }

    @Test
    @DisplayName("Création échoue avec endDate null")
    void testValidateForCreation_NullEndDate() {
        assertThrows(ValidationException.class,
            () -> contractValidator.validateForCreation(1L, 1L, future1, null),
            "La date de fin ne peut pas être null");
    }

    @Test
    @DisplayName("Création échoue si endDate ≤ startDate")
    void testValidateForCreation_InvalidDateRange() {
        assertThrows(ValidationException.class,
            () -> contractValidator.validateForCreation(1L, 1L, future1, future1),
            "La date de fin doit être après la date de début");
    }

    @Test
    @DisplayName("Création échoue si startDate est dans le passé")
    void testValidateForCreation_PastStartDate() {
        LocalDateTime past = now.minusDays(1);

        assertThrows(ValidationException.class,
            () -> contractValidator.validateForCreation(1L, 1L, past, future1),
            "La date de début ne peut pas être dans le passé");
    }

    @Test
    @DisplayName("Création échoue si le client n'existe pas")
    void testValidateForCreation_ClientNotFound() {
        when(clientRepository.existsById(1L)).thenReturn(false);

        assertThrows(ValidationException.class,
            () -> contractValidator.validateForCreation(1L, 1L, future1, future2),
            "Le client avec l'ID 1 n'existe pas");
    }

    @Test
    @DisplayName("Création échoue si le véhicule n'existe pas")
    void testValidateForCreation_VehicleNotFound() {
        when(clientRepository.existsById(1L)).thenReturn(true);
        when(vehicleRepository.existsById(1L)).thenReturn(false);

        assertThrows(ValidationException.class,
            () -> contractValidator.validateForCreation(1L, 1L, future1, future2),
            "Le véhicule avec l'ID 1 n'existe pas");
    }

    @Test
    @DisplayName("Création échoue si le véhicule est en panne")
    void testValidateForCreation_VehicleBrokenDown() {
        VehicleEntity brokenDownVehicle = VehicleEntity.builder()
            .id(1L)
            .registrationPlate("AB-123-CD")
            .brand("Peugeot")
            .status(VehicleStatus.BROKEN_DOWN)
            .build();
        
        when(clientRepository.existsById(1L)).thenReturn(true);
        when(vehicleRepository.existsById(1L)).thenReturn(true);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(brokenDownVehicle));

        assertThrows(ContractConflictException.class,
            () -> contractValidator.validateForCreation(1L, 1L, future1, future2));
    }

    @Test
    @DisplayName("Création échoue en cas de conflit de réservation")
    void testValidateForCreation_ConflictingContract() {
        ContractEntity conflictingContract = ContractEntity.builder()
            .id(99L)
            .status(ContractStatus.ONGOING)
            .startDate(future1.minusHours(1))
            .endDate(future1.plusHours(1))
            .build();
        
        when(clientRepository.existsById(1L)).thenReturn(true);
        when(vehicleRepository.existsById(1L)).thenReturn(true);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(contractRepository.findConflictingContracts(1L, future1, future2))
            .thenReturn(List.of(conflictingContract));
        
        assertThrows(ContractConflictException.class,
            () -> contractValidator.validateForCreation(1L, 1L, future1, future2));
    }

    // ============================================
    // Tests de transitions de statut (machine d'état)
    // ============================================

    @Test
    @DisplayName("Transition PENDING → ONGOING valide")
    void testStatusTransition_PendingToOngoing_Success() {
        assertDoesNotThrow(() -> contractValidator.validateStatusTransition(ContractStatus.PENDING, ContractStatus.ONGOING));
    }

    @Test
    @DisplayName("Transition PENDING → CANCELLED valide")
    void testStatusTransition_PendingToCancelled_Success() {
        assertDoesNotThrow(() -> contractValidator.validateStatusTransition(ContractStatus.PENDING, ContractStatus.CANCELLED));
    }

    @Test
    @DisplayName("Transition PENDING → COMPLETED invalide")
    void testStatusTransition_PendingToCompleted_Fails() {
        ValidationException exception = assertThrows(ValidationException.class,
            () -> contractValidator.validateStatusTransition(ContractStatus.PENDING, ContractStatus.COMPLETED));
        
        assertTrue(exception.getMessage().contains("ne peut passer que à ONGOING"));
    }

    @Test
    @DisplayName("Transition ONGOING → COMPLETED valide")
    void testStatusTransition_OngoingToCompleted_Success() {
        assertDoesNotThrow(() -> contractValidator.validateStatusTransition(ContractStatus.ONGOING, ContractStatus.COMPLETED));
    }

    @Test
    @DisplayName("Transition ONGOING → OVERDUE valide")
    void testStatusTransition_OngoingToOverdue_Success() {
        assertDoesNotThrow(() -> contractValidator.validateStatusTransition(ContractStatus.ONGOING, ContractStatus.OVERDUE));
    }

    @Test
    @DisplayName("Transition ONGOING → PENDING invalide")
    void testStatusTransition_OngoingToPending_Fails() {
        ValidationException exception = assertThrows(ValidationException.class,
            () -> contractValidator.validateStatusTransition(ContractStatus.ONGOING, ContractStatus.PENDING));
        
        assertTrue(exception.getMessage().contains("ne peut passer que à COMPLETED"));
    }

    @Test
    @DisplayName("Transition OVERDUE → CANCELLED valide")
    void testStatusTransition_OverdueToCancelled_Success() {
        assertDoesNotThrow(() -> contractValidator.validateStatusTransition(ContractStatus.OVERDUE, ContractStatus.CANCELLED));
    }

    @Test
    @DisplayName("Transition OVERDUE → ONGOING invalide")
    void testStatusTransition_OverdueToOngoing_Fails() {
        ValidationException exception = assertThrows(ValidationException.class,
            () -> contractValidator.validateStatusTransition(ContractStatus.OVERDUE, ContractStatus.ONGOING));
        
        assertTrue(exception.getMessage().contains("ne peut être qu'annulé"));
    }

    @Test
    @DisplayName("Transition COMPLETED → * invalide (état terminal)")
    void testStatusTransition_CompletedTerminal() {
        ValidationException exception = assertThrows(ValidationException.class,
            () -> contractValidator.validateStatusTransition(ContractStatus.COMPLETED, ContractStatus.CANCELLED));
        
        assertTrue(exception.getMessage().contains("état est terminal"));
    }

    @Test
    @DisplayName("Transition CANCELLED → * invalide (état terminal)")
    void testStatusTransition_CancelledTerminal() {
        ValidationException exception = assertThrows(ValidationException.class,
            () -> contractValidator.validateStatusTransition(ContractStatus.CANCELLED, ContractStatus.COMPLETED));
        
        assertTrue(exception.getMessage().contains("état est terminal"));
    }

    @Test
    @DisplayName("Transition vers le même statut invalide")
    void testStatusTransition_SameStatus_Fails() {
        ValidationException exception = assertThrows(ValidationException.class,
            () -> contractValidator.validateStatusTransition(ContractStatus.PENDING, ContractStatus.PENDING));
        
        assertTrue(exception.getMessage().contains("déjà le statut"));
    }
}
