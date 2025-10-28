package imt.nord.europe.architecture.tp.business.vehicle.services;

import imt.nord.europe.architecture.tp.business.vehicle.models.Vehicle;
import imt.nord.europe.architecture.tp.business.vehicle.validators.VehicleValidator;
import imt.nord.europe.architecture.tp.common.enums.VehicleStatus;
import imt.nord.europe.architecture.tp.common.exceptions.ResourceNotFoundException;
import imt.nord.europe.architecture.tp.common.exceptions.ValidationException;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.VehicleEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.mapper.VehiclePersistenceMapper;
import imt.nord.europe.architecture.tp.infrastructure.db.repository.VehicleRepository;
import imt.nord.europe.architecture.tp.infrastructure.event.SpringEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests pour le service de véhicules.
 * Couvre la création, modification, suppression et gestion des statuts.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleService Tests")
class VehicleServiceTest {

    private VehicleService vehicleService;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehiclePersistenceMapper vehicleMapper;

    @Mock
    private VehicleValidator vehicleValidator;

    @Mock
    private SpringEventPublisher eventPublisher;

    private VehicleEntity testVehicleEntity;
    private Vehicle testVehicle;
    private LocalDate validAcquisitionDate;

    @BeforeEach
    void setUp() {
        vehicleService = new VehicleService(vehicleRepository, vehicleMapper, vehicleValidator, eventPublisher);

        validAcquisitionDate = LocalDate.now().minusYears(2);

        testVehicleEntity = VehicleEntity.builder()
            .id(1L)
            .registrationPlate("AB-123-CD")
            .brand("Peugeot")
            .model("3008")
            .motorization("1.5 BlueHDi")
            .color("Noir")
            .acquisitionDate(validAcquisitionDate)
            .status(VehicleStatus.AVAILABLE)
            .build();

        testVehicle = Vehicle.builder()
            .id(1L)
            .registrationPlate("AB-123-CD")
            .brand("Peugeot")
            .model("3008")
            .motorization("1.5 BlueHDi")
            .color("Noir")
            .acquisitionDate(validAcquisitionDate)
            .status(VehicleStatus.AVAILABLE)
            .build();
    }

    // ============================================
    // Tests de création
    // ============================================

    @Test
    @DisplayName("Création valide d'un véhicule")
    void testCreateVehicle_Success() {
        doNothing().when(vehicleValidator).validateForCreation(
            anyString(), anyString(), anyString(), any(LocalDate.class));
        when(vehicleRepository.save(any(VehicleEntity.class))).thenReturn(testVehicleEntity);
        when(vehicleMapper.toDomainModel(testVehicleEntity)).thenReturn(testVehicle);

        Vehicle result = vehicleService.createVehicle(
            "AB-123-CD", "Peugeot", "3008", "1.5 BlueHDi", "Noir", validAcquisitionDate);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("AB-123-CD", result.getRegistrationPlate());
        assertEquals(VehicleStatus.AVAILABLE, result.getStatus());
        verify(vehicleValidator).validateForCreation(
            "AB-123-CD", "Peugeot", "3008", validAcquisitionDate);
        verify(vehicleRepository).save(any(VehicleEntity.class));
    }

    @Test
    @DisplayName("Création échoue si validation échoue")
    void testCreateVehicle_ValidationFails() {
        doThrow(new ValidationException("Plaque invalide"))
            .when(vehicleValidator).validateForCreation(anyString(), anyString(), anyString(), any(LocalDate.class));

        assertThrows(ValidationException.class,
            () -> vehicleService.createVehicle(
                "AB-123-CD", "Peugeot", "3008", "1.5 BlueHDi", "Noir", validAcquisitionDate));

        verify(vehicleRepository, never()).save(any());
    }

    // ============================================
    // Tests de récupération
    // ============================================

    @Test
    @DisplayName("Récupération d'un véhicule par ID")
    void testGetVehicleById_Success() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicleEntity));
        when(vehicleMapper.toDomainModel(testVehicleEntity)).thenReturn(testVehicle);

        Vehicle result = vehicleService.getVehicleById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("AB-123-CD", result.getRegistrationPlate());
        verify(vehicleRepository).findById(1L);
    }

    @Test
    @DisplayName("Récupération échoue si le véhicule n'existe pas")
    void testGetVehicleById_NotFound() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> vehicleService.getVehicleById(1L));
    }

    @Test
    @DisplayName("Récupération de tous les véhicules")
    void testGetAllVehicles_Success() {
        when(vehicleRepository.findAll()).thenReturn(List.of(testVehicleEntity));
        when(vehicleMapper.toDomainModel(testVehicleEntity)).thenReturn(testVehicle);

        List<Vehicle> results = vehicleService.getAllVehicles();

        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
    }

    @Test
    @DisplayName("Récupération des véhicules par statut et marque")
    void testGetVehiclesByStatusAndBrand_Success() {
        when(vehicleRepository.findByStatusAndBrand(VehicleStatus.AVAILABLE, "Peugeot"))
            .thenReturn(List.of(testVehicleEntity));
        when(vehicleMapper.toDomainModel(testVehicleEntity)).thenReturn(testVehicle);

        List<Vehicle> results = vehicleService.getVehiclesByStatusAndBrand(VehicleStatus.AVAILABLE, "Peugeot");

        assertEquals(1, results.size());
        assertEquals("Peugeot", results.get(0).getBrand());
    }

    @Test
    @DisplayName("Récupération avec filtres null retourne tous")
    void testGetVehiclesByStatusAndBrand_NullFilters() {
        when(vehicleRepository.findByStatusAndBrand(null, null))
            .thenReturn(List.of(testVehicleEntity));
        when(vehicleMapper.toDomainModel(testVehicleEntity)).thenReturn(testVehicle);

        List<Vehicle> results = vehicleService.getVehiclesByStatusAndBrand(null, null);

        assertEquals(1, results.size());
    }

    // ============================================
    // Tests de modification
    // ============================================

    @Test
    @DisplayName("Modification valide d'un véhicule")
    void testUpdateVehicle_Success() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicleEntity));
        doNothing().when(vehicleValidator).validateForUpdate(
            anyLong(), anyString(), anyString(), anyString(), any(LocalDate.class));
        when(vehicleRepository.save(any(VehicleEntity.class))).thenReturn(testVehicleEntity);
        when(vehicleMapper.toDomainModel(testVehicleEntity)).thenReturn(testVehicle);

        Vehicle result = vehicleService.updateVehicle(
            1L, "AB-123-CD", "Peugeot", "3008", "1.5 BlueHDi", "Noir", validAcquisitionDate);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(vehicleRepository).save(any(VehicleEntity.class));
    }

    @Test
    @DisplayName("Modification échoue si le véhicule n'existe pas")
    void testUpdateVehicle_NotFound() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> vehicleService.updateVehicle(
                1L, "AB-123-CD", "Peugeot", "3008", "1.5 BlueHDi", "Noir", validAcquisitionDate));

        verify(vehicleRepository, never()).save(any());
    }

    // ============================================
    // Tests de gestion des statuts
    // ============================================

    @Test
    @DisplayName("Marquage d'un véhicule comme en panne")
    void testMarkVehicleAsBrokenDown_Success() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicleEntity));
        when(vehicleRepository.save(any(VehicleEntity.class))).thenReturn(testVehicleEntity);
        when(vehicleMapper.toDomainModel(testVehicleEntity)).thenReturn(testVehicle);

        Vehicle result = vehicleService.markAsBrokenDown(1L);

        assertNotNull(result);
        verify(eventPublisher).publishVehicleBreakdownEvent(any());
    }

    @Test
    @DisplayName("Marquage comme réparé change le statut à AVAILABLE")
    void testMarkVehicleAsRepaired_Success() {
        testVehicleEntity.setStatus(VehicleStatus.BROKEN_DOWN);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicleEntity));
        when(vehicleRepository.save(any(VehicleEntity.class))).thenReturn(testVehicleEntity);
        when(vehicleMapper.toDomainModel(testVehicleEntity)).thenReturn(testVehicle);

        Vehicle result = vehicleService.markAsAvailable(1L);

        assertNotNull(result);
        assertEquals(VehicleStatus.AVAILABLE, testVehicleEntity.getStatus());
    }

    // ============================================
    // Tests de suppression
    // ============================================

    @Test
    @DisplayName("Suppression valide d'un véhicule")
    void testDeleteVehicle_Success() {
        when(vehicleRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> vehicleService.deleteVehicle(1L));

        verify(vehicleRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Suppression échoue si le véhicule n'existe pas")
    void testDeleteVehicle_NotFound() {
        when(vehicleRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> vehicleService.deleteVehicle(1L));

        verify(vehicleRepository, never()).deleteById(anyLong());
    }
}
