package imt.nord.europe.architecture.tp.infrastructure.event;

import org.springframework.context.ApplicationEvent;

/**
 * Événement déclenché lorsqu'un véhicule est déclaré en panne.
 * Utilisé pour annuler automatiquement les contrats en attente associés au véhicule.
 */
public class VehicleBreakdownEvent extends ApplicationEvent {
    
    private final Long vehicleId;
    
    public VehicleBreakdownEvent(Object source, Long vehicleId) {
        super(source);
        this.vehicleId = vehicleId;
    }
    
    public Long getVehicleId() {
        return vehicleId;
    }
}
