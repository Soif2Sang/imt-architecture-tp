package imt.nord.europe.architecture.tp.infrastructure.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour VehicleBreakdownEvent.
 * Vérifie que l'événement stocke et récupère correctement les données.
 */
@DisplayName("VehicleBreakdownEvent")
class VehicleBreakdownEventTest {

    @Test
    @DisplayName("Crée un événement avec le bon vehicleId")
    void testVehicleBreakdownEvent_Creation() {
        // Arrange
        Long vehicleId = 1L;
        Object source = new Object();

        // Act
        VehicleBreakdownEvent event = new VehicleBreakdownEvent(source, vehicleId);

        // Assert
        assertNotNull(event);
        assertEquals(vehicleId, event.getVehicleId());
        assertEquals(source, event.getSource());
    }

    @Test
    @DisplayName("Récupère correctement le vehicleId")
    void testVehicleBreakdownEvent_GetVehicleId() {
        // Arrange
        Long vehicleId = 42L;

        // Act
        VehicleBreakdownEvent event = new VehicleBreakdownEvent(this, vehicleId);

        // Assert
        assertEquals(42L, event.getVehicleId());
    }

    @Test
    @DisplayName("Stocke la source de l'événement")
    void testVehicleBreakdownEvent_Source() {
        // Arrange
        Object source = new Object();
        Long vehicleId = 1L;

        // Act
        VehicleBreakdownEvent event = new VehicleBreakdownEvent(source, vehicleId);

        // Assert
        assertEquals(source, event.getSource());
    }

    @Test
    @DisplayName("Gère les IDs de véhicule distincts")
    void testVehicleBreakdownEvent_DifferentIds() {
        // Arrange & Act
        VehicleBreakdownEvent event1 = new VehicleBreakdownEvent(this, 1L);
        VehicleBreakdownEvent event2 = new VehicleBreakdownEvent(this, 2L);

        // Assert
        assertNotEquals(event1.getVehicleId(), event2.getVehicleId());
    }

    @Test
    @DisplayName("Accepte les grands IDs")
    void testVehicleBreakdownEvent_LargeId() {
        // Arrange
        Long largeId = Long.MAX_VALUE;

        // Act
        VehicleBreakdownEvent event = new VehicleBreakdownEvent(this, largeId);

        // Assert
        assertEquals(Long.MAX_VALUE, event.getVehicleId());
    }

    @Test
    @DisplayName("Accepte l'ID zéro")
    void testVehicleBreakdownEvent_ZeroId() {
        // Arrange & Act
        VehicleBreakdownEvent event = new VehicleBreakdownEvent(this, 0L);

        // Assert
        assertEquals(0L, event.getVehicleId());
    }
}
