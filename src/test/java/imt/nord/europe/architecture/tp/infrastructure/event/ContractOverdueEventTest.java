package imt.nord.europe.architecture.tp.infrastructure.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour ContractOverdueEvent.
 * Vérifie que l'événement stocke et récupère correctement les données.
 */
@DisplayName("ContractOverdueEvent")
class ContractOverdueEventTest {

    @Test
    @DisplayName("Crée un événement avec le bon contractId")
    void testContractOverdueEvent_Creation() {
        // Arrange
        Long contractId = 1L;
        Object source = new Object();

        // Act
        ContractOverdueEvent event = new ContractOverdueEvent(source, contractId);

        // Assert
        assertNotNull(event);
        assertEquals(contractId, event.getContractId());
        assertEquals(source, event.getSource());
    }

    @Test
    @DisplayName("Récupère correctement le contractId")
    void testContractOverdueEvent_GetContractId() {
        // Arrange
        Long contractId = 99L;

        // Act
        ContractOverdueEvent event = new ContractOverdueEvent(this, contractId);

        // Assert
        assertEquals(99L, event.getContractId());
    }

    @Test
    @DisplayName("Stocke la source de l'événement")
    void testContractOverdueEvent_Source() {
        // Arrange
        Object source = new Object();
        Long contractId = 5L;

        // Act
        ContractOverdueEvent event = new ContractOverdueEvent(source, contractId);

        // Assert
        assertEquals(source, event.getSource());
    }

    @Test
    @DisplayName("Gère les IDs de contrat distincts")
    void testContractOverdueEvent_DifferentIds() {
        // Arrange & Act
        ContractOverdueEvent event1 = new ContractOverdueEvent(this, 1L);
        ContractOverdueEvent event2 = new ContractOverdueEvent(this, 2L);

        // Assert
        assertNotEquals(event1.getContractId(), event2.getContractId());
    }

    @Test
    @DisplayName("Accepte les grands IDs")
    void testContractOverdueEvent_LargeId() {
        // Arrange
        Long largeId = Long.MAX_VALUE;

        // Act
        ContractOverdueEvent event = new ContractOverdueEvent(this, largeId);

        // Assert
        assertEquals(Long.MAX_VALUE, event.getContractId());
    }

    @Test
    @DisplayName("Accepte l'ID zéro")
    void testContractOverdueEvent_ZeroId() {
        // Arrange & Act
        ContractOverdueEvent event = new ContractOverdueEvent(this, 0L);

        // Assert
        assertEquals(0L, event.getContractId());
    }

    @Test
    @DisplayName("Hérite correctement d'ApplicationEvent")
    void testContractOverdueEvent_ExtendsApplicationEvent() {
        // Arrange & Act
        ContractOverdueEvent event = new ContractOverdueEvent(this, 1L);

        // Assert
        assertNotNull(event.getSource());
        assertTrue(event instanceof org.springframework.context.ApplicationEvent);
    }
}
