package imt.nord.europe.architecture.tp.infrastructure.event.handler;

import imt.nord.europe.architecture.tp.business.contract.services.ContractService;
import imt.nord.europe.architecture.tp.common.enums.ContractStatus;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.ClientEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.ContractEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.VehicleEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.repository.ContractRepository;
import imt.nord.europe.architecture.tp.infrastructure.event.VehicleBreakdownEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour VehicleBreakdownEventHandler.
 * Vérifie que les contrats en attente sont annulés lors d'une panne de véhicule.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleBreakdownEventHandler")
class VehicleBreakdownEventHandlerTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ContractService contractService;

    @InjectMocks
    private VehicleBreakdownEventHandler handler;

    private VehicleBreakdownEvent event;
    private Long vehicleId;
    private VehicleEntity testVehicle;
    private ClientEntity testClient;
    private ContractEntity pendingContract1;
    private ContractEntity pendingContract2;

    @BeforeEach
    void setUp() {
        vehicleId = 1L;
        event = new VehicleBreakdownEvent(this, vehicleId);

        // Création du véhicule
        testVehicle = VehicleEntity.builder()
            .id(vehicleId)
            .registrationPlate("AB-123-CD")
            .brand("Toyota")
            .model("Corolla")
            .status(null)
            .build();

        // Création du client
        testClient = ClientEntity.builder()
            .id(1L)
            .firstName("Jean")
            .lastName("Dupont")
            .build();

        // Création de contrats en attente
        pendingContract1 = ContractEntity.builder()
            .id(1L)
            .client(testClient)
            .vehicle(testVehicle)
            .status(ContractStatus.PENDING)
            .startDate(LocalDateTime.now().plusDays(1))
            .endDate(LocalDateTime.now().plusDays(5))
            .createdAt(LocalDateTime.now())
            .build();

        pendingContract2 = ContractEntity.builder()
            .id(2L)
            .client(testClient)
            .vehicle(testVehicle)
            .status(ContractStatus.PENDING)
            .startDate(LocalDateTime.now().plusDays(10))
            .endDate(LocalDateTime.now().plusDays(15))
            .createdAt(LocalDateTime.now())
            .build();
    }

    @Test
    @DisplayName("Annule tous les contrats en attente du véhicule en panne")
    void testOnVehicleBreakdown_CancelsPendingContracts() {
        // Arrange
        List<ContractEntity> pendingContracts = List.of(pendingContract1, pendingContract2);
        when(contractRepository.findByVehicleIdAndStatus(vehicleId, ContractStatus.PENDING))
            .thenReturn(pendingContracts);

        // Act
        handler.onVehicleBreakdown(event);

        // Assert
        verify(contractService, times(2)).cancelContract(any(Long.class));
        verify(contractService, times(1)).cancelContract(1L);
        verify(contractService, times(1)).cancelContract(2L);
    }

    @Test
    @DisplayName("Ne fait rien s'il n'y a pas de contrats en attente")
    void testOnVehicleBreakdown_NoContracts() {
        // Arrange
        when(contractRepository.findByVehicleIdAndStatus(vehicleId, ContractStatus.PENDING))
            .thenReturn(new ArrayList<>());

        // Act
        handler.onVehicleBreakdown(event);

        // Assert
        verify(contractService, never()).cancelContract(any());
    }

    @Test
    @DisplayName("N'annule que les contrats en attente, pas les autres statuts")
    void testOnVehicleBreakdown_OnlyPendingContracts() {
        // Arrange
        List<ContractEntity> pendingContracts = List.of(pendingContract1);
        when(contractRepository.findByVehicleIdAndStatus(vehicleId, ContractStatus.PENDING))
            .thenReturn(pendingContracts);

        // Act
        handler.onVehicleBreakdown(event);

        // Assert
        verify(contractService, times(1)).cancelContract(1L);
        verify(contractService, never()).cancelContract(2L);
    }

    @Test
    @DisplayName("Récupère correctement les contrats par vehicleId et statut PENDING")
    void testOnVehicleBreakdown_FetchesCorrectly() {
        // Arrange
        when(contractRepository.findByVehicleIdAndStatus(vehicleId, ContractStatus.PENDING))
            .thenReturn(new ArrayList<>());

        // Act
        handler.onVehicleBreakdown(event);

        // Assert
        verify(contractRepository, times(1)).findByVehicleIdAndStatus(vehicleId, ContractStatus.PENDING);
    }

    @Test
    @DisplayName("Traite un événement avec un véhicule ayant un ID distinct")
    void testOnVehicleBreakdown_DifferentVehicleIds() {
        // Arrange
        Long differentVehicleId = 99L;
        VehicleBreakdownEvent differentEvent = new VehicleBreakdownEvent(this, differentVehicleId);
        
        when(contractRepository.findByVehicleIdAndStatus(differentVehicleId, ContractStatus.PENDING))
            .thenReturn(List.of(pendingContract1));

        // Act
        handler.onVehicleBreakdown(differentEvent);

        // Assert
        verify(contractRepository, times(1)).findByVehicleIdAndStatus(differentVehicleId, ContractStatus.PENDING);
        verify(contractService, times(1)).cancelContract(1L);
    }

    @Test
    @DisplayName("Gère les exceptions lors de l'annulation d'un contrat")
    void testOnVehicleBreakdown_HandlesCancelException() {
        // Arrange
        List<ContractEntity> pendingContracts = List.of(pendingContract1);
        when(contractRepository.findByVehicleIdAndStatus(vehicleId, ContractStatus.PENDING))
            .thenReturn(pendingContracts);
        doThrow(new RuntimeException("Erreur annulation")).when(contractService).cancelContract(1L);

        // Act & Assert
        try {
            handler.onVehicleBreakdown(event);
        } catch (RuntimeException e) {
            // L'exception doit remonter
        }
        verify(contractService, times(1)).cancelContract(1L);
    }
}
