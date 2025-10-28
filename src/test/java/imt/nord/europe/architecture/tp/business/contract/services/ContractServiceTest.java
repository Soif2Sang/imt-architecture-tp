package imt.nord.europe.architecture.tp.business.contract.services;

import imt.nord.europe.architecture.tp.business.contract.models.Contract;
import imt.nord.europe.architecture.tp.business.contract.validators.ContractValidator;
import imt.nord.europe.architecture.tp.common.enums.ContractStatus;
import imt.nord.europe.architecture.tp.common.exceptions.ResourceNotFoundException;
import imt.nord.europe.architecture.tp.common.exceptions.ValidationException;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.ClientEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.ContractEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.VehicleEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.mapper.ContractPersistenceMapper;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests pour le service de contrats (locations).
 * Couvre la création, modification, suppression et transitions de statut.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ContractService Tests")
class ContractServiceTest {

    private ContractService contractService;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ContractPersistenceMapper contractMapper;

    @Mock
    private ContractValidator contractValidator;

    private ClientEntity testClient;
    private VehicleEntity testVehicle;
    private ContractEntity testContractEntity;
    private Contract testContract;
    private LocalDateTime now;
    private LocalDateTime future1;
    private LocalDateTime future2;

    @BeforeEach
    void setUp() {
        contractService = new ContractService(
            contractRepository,
            clientRepository,
            vehicleRepository,
            contractMapper,
            contractValidator
        );

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
            .build();

        testContractEntity = ContractEntity.builder()
            .id(1L)
            .client(testClient)
            .vehicle(testVehicle)
            .startDate(future1)
            .endDate(future2)
            .status(ContractStatus.PENDING)
            .build();

        testContract = Contract.builder()
            .id(1L)
            .clientId(1L)
            .vehicleId(1L)
            .startDate(future1)
            .endDate(future2)
            .status(ContractStatus.PENDING)
            .build();
    }

    // ============================================
    // Tests de création
    // ============================================

    @Test
    @DisplayName("Création valide d'un contrat")
    void testCreateContract_Success() {
        doNothing().when(contractValidator).validateForCreation(anyLong(), anyLong(), any(), any());
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(contractRepository.save(any(ContractEntity.class))).thenReturn(testContractEntity);
        when(contractMapper.toDomainModel(testContractEntity)).thenReturn(testContract);

        Contract result = contractService.createContract(1L, 1L, future1, future2);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(ContractStatus.PENDING, result.getStatus());
        verify(contractValidator).validateForCreation(1L, 1L, future1, future2);
        verify(contractRepository).save(any(ContractEntity.class));
    }

    @Test
    @DisplayName("Création échoue si validation échoue")
    void testCreateContract_ValidationFails() {
        doThrow(new ValidationException("Dates invalides"))
            .when(contractValidator).validateForCreation(anyLong(), anyLong(), any(), any());

        assertThrows(ValidationException.class,
            () -> contractService.createContract(1L, 1L, future1, future2));

        verify(contractRepository, never()).save(any());
    }

    // ============================================
    // Tests de récupération
    // ============================================

    @Test
    @DisplayName("Récupération d'un contrat par ID")
    void testGetContractById_Success() {
        when(contractRepository.findById(1L)).thenReturn(Optional.of(testContractEntity));
        when(contractMapper.toDomainModel(testContractEntity)).thenReturn(testContract);

        Contract result = contractService.getContractById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(contractRepository).findById(1L);
    }

    @Test
    @DisplayName("Récupération échoue si le contrat n'existe pas")
    void testGetContractById_NotFound() {
        when(contractRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> contractService.getContractById(1L));
    }

    @Test
    @DisplayName("Récupération de tous les contrats")
    void testGetAllContracts_Success() {
        when(contractRepository.findAll()).thenReturn(List.of(testContractEntity));
        when(contractMapper.toDomainModel(testContractEntity)).thenReturn(testContract);

        List<Contract> results = contractService.getAllContracts();

        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
    }

    @Test
    @DisplayName("Récupération des contrats d'un client")
    void testGetContractsByClientId_Success() {
        when(contractRepository.findByClientId(1L)).thenReturn(List.of(testContractEntity));
        when(contractMapper.toDomainModel(testContractEntity)).thenReturn(testContract);

        List<Contract> results = contractService.getContractsByClientId(1L);

        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Récupération des contrats d'un véhicule")
    void testGetContractsByVehicleId_Success() {
        when(contractRepository.findByVehicleId(1L)).thenReturn(List.of(testContractEntity));
        when(contractMapper.toDomainModel(testContractEntity)).thenReturn(testContract);

        List<Contract> results = contractService.getContractsByVehicleId(1L);

        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Récupération des contrats par statut")
    void testGetContractsByStatus_Success() {
        when(contractRepository.findByStatus(ContractStatus.PENDING))
            .thenReturn(List.of(testContractEntity));
        when(contractMapper.toDomainModel(testContractEntity)).thenReturn(testContract);

        List<Contract> results = contractService.getContractsByStatus(ContractStatus.PENDING);

        assertEquals(1, results.size());
    }

    // ============================================
    // Tests de modification
    // ============================================

    @Test
    @DisplayName("Modification valide d'un contrat")
    void testUpdateContract_Success() {
        when(contractRepository.findById(1L)).thenReturn(Optional.of(testContractEntity));
        doNothing().when(contractValidator).validateForUpdate(anyLong(), anyLong(), anyLong(), any(), any());
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(contractRepository.save(any(ContractEntity.class))).thenReturn(testContractEntity);
        when(contractMapper.toDomainModel(testContractEntity)).thenReturn(testContract);

        Contract result = contractService.updateContract(1L, 1L, 1L, future1, future2);

        assertNotNull(result);
        verify(contractValidator).validateForUpdate(1L, 1L, 1L, future1, future2);
        verify(contractRepository).save(any(ContractEntity.class));
    }

    @Test
    @DisplayName("Modification échoue si le contrat n'existe pas")
    void testUpdateContract_NotFound() {
        when(contractRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> contractService.updateContract(1L, 1L, 1L, future1, future2));
    }

    // ============================================
    // Tests de transitions de statut
    // ============================================

    @Test
    @DisplayName("Transition PENDING → ONGOING valide (approve)")
    void testApproveContract_Success() {
        ContractEntity pendingContract = ContractEntity.builder()
            .id(1L)
            .client(testClient)
            .vehicle(testVehicle)
            .startDate(future1)
            .endDate(future2)
            .status(ContractStatus.PENDING)
            .build();
        
        when(contractRepository.findById(1L)).thenReturn(Optional.of(pendingContract));
        doNothing().when(contractValidator).validateStatusTransition(ContractStatus.PENDING, ContractStatus.ONGOING);

        ContractEntity ongoingContract = ContractEntity.builder()
            .id(1L)
            .client(testClient)
            .vehicle(testVehicle)
            .startDate(future1)
            .endDate(future2)
            .status(ContractStatus.ONGOING)
            .build();
        when(contractRepository.save(any(ContractEntity.class))).thenReturn(ongoingContract);

        Contract updatedContract = Contract.builder()
            .id(1L)
            .clientId(1L)
            .vehicleId(1L)
            .startDate(future1)
            .endDate(future2)
            .status(ContractStatus.ONGOING)
            .build();
        when(contractMapper.toDomainModel(ongoingContract)).thenReturn(updatedContract);

        Contract result = contractService.approveContract(1L);

        assertEquals(ContractStatus.ONGOING, result.getStatus());
        verify(contractValidator).validateStatusTransition(ContractStatus.PENDING, ContractStatus.ONGOING);
    }

    @Test
    @DisplayName("Transition ONGOING → COMPLETED valide (complete)")
    void testCompleteContract_Success() {
        ContractEntity ongoingContract = ContractEntity.builder()
            .id(1L)
            .client(testClient)
            .vehicle(testVehicle)
            .startDate(future1)
            .endDate(future2)
            .status(ContractStatus.ONGOING)
            .build();
        
        when(contractRepository.findById(1L)).thenReturn(Optional.of(ongoingContract));
        doNothing().when(contractValidator).validateStatusTransition(ContractStatus.ONGOING, ContractStatus.COMPLETED);

        ContractEntity completedContract = ContractEntity.builder()
            .id(1L)
            .client(testClient)
            .vehicle(testVehicle)
            .startDate(future1)
            .endDate(future2)
            .status(ContractStatus.COMPLETED)
            .build();
        when(contractRepository.save(any(ContractEntity.class))).thenReturn(completedContract);

        Contract updatedContract = Contract.builder()
            .id(1L)
            .clientId(1L)
            .vehicleId(1L)
            .startDate(future1)
            .endDate(future2)
            .status(ContractStatus.COMPLETED)
            .build();
        when(contractMapper.toDomainModel(completedContract)).thenReturn(updatedContract);

        Contract result = contractService.completeContract(1L);

        assertEquals(ContractStatus.COMPLETED, result.getStatus());
    }

    @Test
    @DisplayName("Transition PENDING → CANCELLED valide (cancel)")
    void testCancelContract_Success() {
        ContractEntity pendingContract = ContractEntity.builder()
            .id(1L)
            .client(testClient)
            .vehicle(testVehicle)
            .startDate(future1)
            .endDate(future2)
            .status(ContractStatus.PENDING)
            .build();
        
        when(contractRepository.findById(1L)).thenReturn(Optional.of(pendingContract));
        doNothing().when(contractValidator).validateStatusTransition(ContractStatus.PENDING, ContractStatus.CANCELLED);

        ContractEntity cancelledContract = ContractEntity.builder()
            .id(1L)
            .client(testClient)
            .vehicle(testVehicle)
            .startDate(future1)
            .endDate(future2)
            .status(ContractStatus.CANCELLED)
            .build();
        when(contractRepository.save(any(ContractEntity.class))).thenReturn(cancelledContract);

        Contract updatedContract = Contract.builder()
            .id(1L)
            .clientId(1L)
            .vehicleId(1L)
            .startDate(future1)
            .endDate(future2)
            .status(ContractStatus.CANCELLED)
            .build();
        when(contractMapper.toDomainModel(cancelledContract)).thenReturn(updatedContract);

        Contract result = contractService.cancelContract(1L);

        assertEquals(ContractStatus.CANCELLED, result.getStatus());
    }

    @Test
    @DisplayName("Transition invalide échoue (COMPLETED → CANCELLED)")
    void testUpdateContractStatus_InvalidTransition() {
        ContractEntity completedContract = ContractEntity.builder()
            .id(1L)
            .client(testClient)
            .vehicle(testVehicle)
            .startDate(future1)
            .endDate(future2)
            .status(ContractStatus.COMPLETED)
            .build();
        
        when(contractRepository.findById(1L)).thenReturn(Optional.of(completedContract));
        doThrow(new ValidationException("État terminal"))
            .when(contractValidator).validateStatusTransition(ContractStatus.COMPLETED, ContractStatus.CANCELLED);

        assertThrows(ValidationException.class,
            () -> contractService.updateContractStatus(1L, ContractStatus.CANCELLED));

        verify(contractRepository, never()).save(any());
    }

    // ============================================
    // Tests de suppression
    // ============================================

    @Test
    @DisplayName("Suppression valide d'un contrat")
    void testDeleteContract_Success() {
        when(contractRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> contractService.deleteContract(1L));

        verify(contractRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Suppression échoue si le contrat n'existe pas")
    void testDeleteContract_NotFound() {
        when(contractRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> contractService.deleteContract(1L));

        verify(contractRepository, never()).deleteById(anyLong());
    }
}
