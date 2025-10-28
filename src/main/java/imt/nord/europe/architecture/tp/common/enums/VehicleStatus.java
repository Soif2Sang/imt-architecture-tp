package imt.nord.europe.architecture.tp.common.enums;

/**
 * Énumération des statuts possibles d'un véhicule.
 */
public enum VehicleStatus {
    
    /**
     * Disponible - Le véhicule est disponible pour la location
     */
    AVAILABLE("Disponible"),
    
    /**
     * En location - Le véhicule est actuellement loué
     */
    RENTED("En location"),
    
    /**
     * En panne - Le véhicule est actuellement en réparation
     */
    BROKEN_DOWN("En panne");
    
    private final String label;
    
    VehicleStatus(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}
