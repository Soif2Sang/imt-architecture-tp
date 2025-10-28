package imt.nord.europe.architecture.tp.infrastructure.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour SpringEventPublisher.
 * Vérifie que les événements métier sont correctement publiés.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SpringEventPublisher")
class SpringEventPublisherTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private SpringEventPublisher springEventPublisher;

    @Test
    @DisplayName("Publie un événement VehicleBreakdownEvent avec le bon ID")
    void testPublishVehicleBreakdownEvent() {
        // Arrange
        Long vehicleId = 1L;
        ArgumentCaptor<VehicleBreakdownEvent> captor = ArgumentCaptor.forClass(VehicleBreakdownEvent.class);

        // Act
        springEventPublisher.publishVehicleBreakdownEvent(vehicleId);

        // Assert
        verify(applicationEventPublisher, times(1)).publishEvent(captor.capture());
        VehicleBreakdownEvent event = captor.getValue();
        assertNotNull(event);
        assertEquals(vehicleId, event.getVehicleId());
    }

    @Test
    @DisplayName("Publie un événement ContractOverdueEvent avec le bon ID")
    void testPublishContractOverdueEvent() {
        // Arrange
        Long contractId = 2L;
        ArgumentCaptor<ContractOverdueEvent> captor = ArgumentCaptor.forClass(ContractOverdueEvent.class);

        // Act
        springEventPublisher.publishContractOverdueEvent(contractId);

        // Assert
        verify(applicationEventPublisher, times(1)).publishEvent(captor.capture());
        ContractOverdueEvent event = captor.getValue();
        assertNotNull(event);
        assertEquals(contractId, event.getContractId());
    }

    @Test
    @DisplayName("Publie correctement plusieurs événements")
    void testPublishMultipleEvents() {
        // Arrange
        Long vehicleId1 = 1L;
        Long vehicleId2 = 2L;
        Long contractId = 3L;

        // Act
        springEventPublisher.publishVehicleBreakdownEvent(vehicleId1);
        springEventPublisher.publishVehicleBreakdownEvent(vehicleId2);
        springEventPublisher.publishContractOverdueEvent(contractId);

        // Assert
        verify(applicationEventPublisher, times(3)).publishEvent(any());
    }

    @Test
    @DisplayName("Accepte null comme vehicleId (pas de validation)")
    void testPublishVehicleBreakdownEvent_WithNullId() {
        // Arrange
        ArgumentCaptor<VehicleBreakdownEvent> captor = ArgumentCaptor.forClass(VehicleBreakdownEvent.class);

        // Act
        springEventPublisher.publishVehicleBreakdownEvent(null);

        // Assert
        verify(applicationEventPublisher, times(1)).publishEvent(captor.capture());
        VehicleBreakdownEvent event = captor.getValue();
        assertNotNull(event);
        assertNull(event.getVehicleId());
    }

    @Test
    @DisplayName("Accepte null comme contractId (pas de validation)")
    void testPublishContractOverdueEvent_WithNullId() {
        // Arrange
        ArgumentCaptor<ContractOverdueEvent> captor = ArgumentCaptor.forClass(ContractOverdueEvent.class);

        // Act
        springEventPublisher.publishContractOverdueEvent(null);

        // Assert
        verify(applicationEventPublisher, times(1)).publishEvent(captor.capture());
        ContractOverdueEvent event = captor.getValue();
        assertNotNull(event);
        assertNull(event.getContractId());
    }
}
