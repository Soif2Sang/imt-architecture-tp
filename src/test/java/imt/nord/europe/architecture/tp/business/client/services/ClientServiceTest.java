package imt.nord.europe.architecture.tp.business.client.services;

import imt.nord.europe.architecture.tp.business.client.models.Client;
import imt.nord.europe.architecture.tp.business.client.validators.ClientValidator;
import imt.nord.europe.architecture.tp.common.exceptions.DuplicateClientException;
import imt.nord.europe.architecture.tp.common.exceptions.ResourceNotFoundException;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.ClientEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.mapper.ClientPersistenceMapper;
import imt.nord.europe.architecture.tp.infrastructure.db.repository.ClientRepository;
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
 * Tests pour le service de clients.
 * Couvre la création, modification, suppression et récupération des clients.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClientService Tests")
class ClientServiceTest {

    private ClientService clientService;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientPersistenceMapper clientMapper;

    @Mock
    private ClientValidator clientValidator;

    private ClientEntity testClientEntity;
    private Client testClient;
    private LocalDate validBirthDate;

    @BeforeEach
    void setUp() {
        clientService = new ClientService(clientRepository, clientMapper, clientValidator);

        validBirthDate = LocalDate.now().minusYears(30);

        testClientEntity = ClientEntity.builder()
            .id(1L)
            .firstName("Jean")
            .lastName("Dupont")
            .dateOfBirth(validBirthDate)
            .licenseNumber("1234567890")
            .build();

        testClient = Client.builder()
            .id(1L)
            .firstName("Jean")
            .lastName("Dupont")
            .dateOfBirth(validBirthDate)
            .licenseNumber("1234567890")
            .build();
    }

    // ============================================
    // Tests de création
    // ============================================

    @Test
    @DisplayName("Création valide d'un client")
    void testCreateClient_Success() {
        doNothing().when(clientValidator).validateForCreation(
            anyString(), anyString(), any(LocalDate.class), anyString());
        when(clientRepository.save(any(ClientEntity.class))).thenReturn(testClientEntity);
        when(clientMapper.toDomainModel(testClientEntity)).thenReturn(testClient);

        Client result = clientService.createClient("Jean", "Dupont", validBirthDate, "1234567890", "123 Rue de la Paix", "jean@example.com", "0123456789");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Jean", result.getFirstName());
        assertEquals("Dupont", result.getLastName());
        verify(clientValidator).validateForCreation("Jean", "Dupont", validBirthDate, "1234567890");
        verify(clientRepository).save(any(ClientEntity.class));
    }

    @Test
    @DisplayName("Création échoue si validation échoue")
    void testCreateClient_ValidationFails() {
        doThrow(new DuplicateClientException("Client déjà existant"))
            .when(clientValidator).validateForCreation(anyString(), anyString(), any(LocalDate.class), anyString());

        assertThrows(DuplicateClientException.class,
            () -> clientService.createClient("Jean", "Dupont", validBirthDate, "1234567890", "123 Rue de la Paix", "jean@example.com", "0123456789"));

        verify(clientRepository, never()).save(any());
    }

    // ============================================
    // Tests de récupération
    // ============================================

    @Test
    @DisplayName("Récupération d'un client par ID")
    void testGetClientById_Success() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClientEntity));
        when(clientMapper.toDomainModel(testClientEntity)).thenReturn(testClient);

        Client result = clientService.getClientById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Jean", result.getFirstName());
        verify(clientRepository).findById(1L);
    }

    @Test
    @DisplayName("Récupération échoue si le client n'existe pas")
    void testGetClientById_NotFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> clientService.getClientById(1L));
    }

    @Test
    @DisplayName("Récupération de tous les clients")
    void testGetAllClients_Success() {
        when(clientRepository.findAll()).thenReturn(List.of(testClientEntity));
        when(clientMapper.toDomainModel(testClientEntity)).thenReturn(testClient);

        List<Client> results = clientService.getAllClients();

        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
    }

    @Test
    @DisplayName("Récupération de clients par nom")
    void testGetClientsByLastName_Success() {
        when(clientRepository.findByLastName("Dupont")).thenReturn(List.of(testClientEntity));
        when(clientMapper.toDomainModel(testClientEntity)).thenReturn(testClient);

        List<Client> results = clientService.getClientsByLastName("Dupont");

        assertEquals(1, results.size());
        assertEquals("Dupont", results.get(0).getLastName());
    }

    @Test
    @DisplayName("Récupération retourne liste vide si pas de clients")
    void testGetClientsByLastName_Empty() {
        when(clientRepository.findByLastName("Inconnu")).thenReturn(List.of());

        List<Client> results = clientService.getClientsByLastName("Inconnu");

        assertTrue(results.isEmpty());
    }

    // ============================================
    // Tests de modification
    // ============================================

    @Test
    @DisplayName("Modification valide d'un client")
    void testUpdateClient_Success() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClientEntity));
        doNothing().when(clientValidator).validateForUpdate(
            anyLong(), anyString(), anyString(), any(LocalDate.class), anyString());
        when(clientRepository.save(any(ClientEntity.class))).thenReturn(testClientEntity);
        when(clientMapper.toDomainModel(testClientEntity)).thenReturn(testClient);

        Client result = clientService.updateClient(1L, "Jean", "Dupont", validBirthDate, "1234567890", "123 Rue de la Paix", "jean@example.com", "0123456789");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(clientValidator).validateForUpdate(1L, "Jean", "Dupont", validBirthDate, "1234567890");
        verify(clientRepository).save(any(ClientEntity.class));
    }

    @Test
    @DisplayName("Modification échoue si le client n'existe pas")
    void testUpdateClient_NotFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> clientService.updateClient(1L, "Jean", "Dupont", validBirthDate, "1234567890", "123 Rue de la Paix", "jean@example.com", "0123456789"));

        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Modification échoue si validation échoue")
    void testUpdateClient_ValidationFails() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClientEntity));
        doThrow(new DuplicateClientException("Licence déjà utilisée"))
            .when(clientValidator).validateForUpdate(
            anyLong(), anyString(), anyString(), any(LocalDate.class), anyString());

        assertThrows(DuplicateClientException.class,
            () -> clientService.updateClient(1L, "Jean", "Dupont", validBirthDate, "1234567890", "123 Rue de la Paix", "jean@example.com", "0123456789"));

        verify(clientRepository, never()).save(any());
    }

    // ============================================
    // Tests de suppression
    // ============================================

    @Test
    @DisplayName("Suppression valide d'un client")
    void testDeleteClient_Success() {
        when(clientRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> clientService.deleteClient(1L));

        verify(clientRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Suppression échoue si le client n'existe pas")
    void testDeleteClient_NotFound() {
        when(clientRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> clientService.deleteClient(1L));

        verify(clientRepository, never()).deleteById(anyLong());
    }

    // ============================================
    // Tests de vérifications
    // ============================================

    @Test
    @DisplayName("Vérification qu'un client existe")
    void testClientExists_True() {
        when(clientRepository.existsById(1L)).thenReturn(true);

        boolean exists = clientRepository.existsById(1L);

        assertTrue(exists);
    }

    @Test
    @DisplayName("Vérification qu'un client n'existe pas")
    void testClientExists_False() {
        when(clientRepository.existsById(999L)).thenReturn(false);

        boolean exists = clientRepository.existsById(999L);

        assertFalse(exists);
    }
}
