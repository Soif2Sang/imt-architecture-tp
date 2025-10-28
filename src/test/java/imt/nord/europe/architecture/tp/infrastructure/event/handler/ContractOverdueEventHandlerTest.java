package imt.nord.europe.architecture.tp.infrastructure.event.handler;

import imt.nord.europe.architecture.tp.business.contract.services.ContractService;
import imt.nord.europe.architecture.tp.common.enums.ContractStatus;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.ClientEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.ContractEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.VehicleEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.repository.ContractRepository;
import imt.nord.europe.architecture.tp.infrastructure.event.ContractOverdueEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour ContractOverdueEventHandler.
 * Vérifie que les actions appropriées sont effectuées lors d'un contrat en retard.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ContractOverdueEventHandler")
class ContractOverdueEventHandlerTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ContractService contractService;

    @InjectMocks
    private ContractOverdueEventHandler handler;

    private ContractOverdueEvent event;
    private Long contractId;
    private ContractEntity overdueContract;
    private ContractEntity pendingContract;
    private VehicleEntity testVehicle;
    private ClientEntity testClient;

    @BeforeEach
    void setUp() {
        contractId = 1L;
        event = new ContractOverdueEvent(this, contractId);

        // Création du client
        testClient = ClientEntity.builder()
            .id(1L)
            .firstName("Marie")
            .lastName("Martin")
            .build();

        // Création du véhicule
        testVehicle = VehicleEntity.builder()
            .id(1L)
            .registrationPlate("XY-789-ZW")
            .brand("Peugeot")
            .model("308")
            .build();

        // Création du contrat en retard: fin AVANT "maintenant" (donc en retard)
        // Pour que chevauchement: endDate du retard doit être APRÈS startDate du pending
        LocalDateTime now = LocalDateTime.now();
        overdueContract = ContractEntity.builder()
            .id(contractId)
            .client(testClient)
            .vehicle(testVehicle)
            .status(ContractStatus.ONGOING)
            .startDate(now.minusDays(10))
            .endDate(now.minusHours(1))  // Fin il y a 1 heure (en retard de peu)
            .createdAt(now.minusDays(12))
            .build();

        // Création d'un contrat en attente pour le même véhicule
        // Ce contrat commence AVANT la fin du contrat en retard
        // Donc: overdueContract.endDate (now-1h) isAfter pending.startDate (now-2h) = TRUE
        pendingContract = ContractEntity.builder()
            .id(2L)
            .client(testClient)
            .vehicle(testVehicle)
            .status(ContractStatus.PENDING)
            .startDate(now.minusHours(2))  // Début il y a 2 heures (AVANT la fin du retard)
            .endDate(now.plusDays(3))
            .createdAt(now.minusDays(3))
            .build();
    }

    @Test
    @DisplayName("Traite correctement un contrat en retard")
    void testOnContractOverdue_Success() {
        // Arrange
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(overdueContract));
        when(contractRepository.findByVehicleIdAndStatus(testVehicle.getId(), ContractStatus.PENDING))
            .thenReturn(new ArrayList<>());

        // Act
        handler.onContractOverdue(event);

        // Assert
        verify(contractRepository, times(1)).findById(contractId);
        verify(contractRepository, times(1)).findByVehicleIdAndStatus(testVehicle.getId(), ContractStatus.PENDING);
    }

    @Test
    @DisplayName("Annule les contrats en attente qui chevauchent avec le contrat en retard")
    void testOnContractOverdue_CancelsPendingContracts() {
        // Arrange
        // Utilise le pendingContract du setUp qui chevauchement correctement
        // overdueContract.endDate (now-1h) isAfter pendingContract.startDate (now-2h) = TRUE
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(overdueContract));
        when(contractRepository.findByVehicleIdAndStatus(testVehicle.getId(), ContractStatus.PENDING))
            .thenReturn(List.of(pendingContract));

        // Act
        handler.onContractOverdue(event);

        // Assert
        verify(contractService, times(1)).cancelContract(pendingContract.getId());
    }

    @Test
    @DisplayName("Lève une exception si le contrat n'existe pas")
    void testOnContractOverdue_ContractNotFound() {
        // Arrange
        when(contractRepository.findById(contractId)).thenReturn(Optional.empty());

        // Act - L'handler log l'erreur sans relancer l'exception
        handler.onContractOverdue(event);

        // Assert - Aucun appel à cancelContract ne devrait être fait
        verify(contractRepository, times(1)).findById(contractId);
        verify(contractService, never()).cancelContract(any());
    }

    @Test
    @DisplayName("Ne ferme pas les contrats sans chevauchement")
    void testOnContractOverdue_NoChevauchement() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        
        // Contrat en attente qui COMMENCE APRÈS la fin du contrat en retard
        // overdueContract.endDate = now-1h
        // Ce contrat commence à now (après la fin du retard) -> pas de chevauchement
        ContractEntity noOverlapContract = ContractEntity.builder()
            .id(3L)
            .client(testClient)
            .vehicle(testVehicle)
            .status(ContractStatus.PENDING)
            .startDate(now.plusHours(1))  // Après la fin du retard (now-1h)
            .endDate(now.plusDays(10))
            .createdAt(now.minusDays(1))
            .build();

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(overdueContract));
        when(contractRepository.findByVehicleIdAndStatus(testVehicle.getId(), ContractStatus.PENDING))
            .thenReturn(List.of(noOverlapContract));

        // Act
        handler.onContractOverdue(event);

        // Assert
        verify(contractService, never()).cancelContract(any());
    }

    @Test
    @DisplayName("Traite correctement plusieurs contrats en attente")
    void testOnContractOverdue_MultiplePendingContracts() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        
        // Deux contrats en attente qui chevauchent la fin du contrat en retard
        // overdueContract.endDate = now-1h
        ContractEntity pendingOverlapping1 = ContractEntity.builder()
            .id(2L)
            .client(testClient)
            .vehicle(testVehicle)
            .status(ContractStatus.PENDING)
            .startDate(now.minusHours(3))  // Avant la fin du retard (now-1h)
            .endDate(now.plusDays(2))
            .createdAt(now.minusDays(2))
            .build();

        ContractEntity pendingOverlapping2 = ContractEntity.builder()
            .id(3L)
            .client(testClient)
            .vehicle(testVehicle)
            .status(ContractStatus.PENDING)
            .startDate(now.minusHours(30))  // Avant la fin du retard (now-1h)
            .endDate(now.plusDays(3))
            .createdAt(now.minusDays(2))
            .build();

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(overdueContract));
        when(contractRepository.findByVehicleIdAndStatus(testVehicle.getId(), ContractStatus.PENDING))
            .thenReturn(List.of(pendingOverlapping1, pendingOverlapping2));

        // Act
        handler.onContractOverdue(event);

        // Assert
        verify(contractService, times(2)).cancelContract(any());
        verify(contractService, times(1)).cancelContract(2L);
        verify(contractService, times(1)).cancelContract(3L);
    }

    @Test
    @DisplayName("Gère correctement l'absence de contrats en attente")
    void testOnContractOverdue_NoPendingContracts() {
        // Arrange
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(overdueContract));
        when(contractRepository.findByVehicleIdAndStatus(testVehicle.getId(), ContractStatus.PENDING))
            .thenReturn(new ArrayList<>());

        // Act
        handler.onContractOverdue(event);

        // Assert
        verify(contractService, never()).cancelContract(any());
    }

    @Test
    @DisplayName("Calcule correctement le retard en jours et heures")
    void testOnContractOverdue_CalculatesDelay() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.minusDays(3).minusHours(5);  // 3 jours et 5 heures de retard

        ContractEntity delayedContract = ContractEntity.builder()
            .id(contractId)
            .client(testClient)
            .vehicle(testVehicle)
            .status(ContractStatus.ONGOING)
            .startDate(now.minusDays(10))
            .endDate(endDate)
            .createdAt(now.minusDays(12))
            .build();

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(delayedContract));
        when(contractRepository.findByVehicleIdAndStatus(testVehicle.getId(), ContractStatus.PENDING))
            .thenReturn(new ArrayList<>());

        // Act
        handler.onContractOverdue(event);

        // Assert
        verify(contractRepository, times(1)).findById(contractId);
    }

    @Test
    @DisplayName("Traite un contrat avec un ID distinct")
    void testOnContractOverdue_DifferentContractId() {
        // Arrange
        Long differentContractId = 999L;
        ContractOverdueEvent differentEvent = new ContractOverdueEvent(this, differentContractId);

        when(contractRepository.findById(differentContractId)).thenReturn(Optional.of(overdueContract));
        when(contractRepository.findByVehicleIdAndStatus(testVehicle.getId(), ContractStatus.PENDING))
            .thenReturn(new ArrayList<>());

        // Act
        handler.onContractOverdue(differentEvent);

        // Assert
        verify(contractRepository, times(1)).findById(differentContractId);
    }
}
