package imt.nord.europe.architecture.tp.infrastructure.db.mapper;

import imt.nord.europe.architecture.tp.business.vehicle.models.Vehicle;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.VehicleEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper pour la conversion entre VehicleEntity (JPA) et Vehicle (modèle métier).
 * Ce mapper gère la transformation entre la représentation persistante et la représentation métier.
 */
@Component
public class VehiclePersistenceMapper {
    
    /**
     * Convertit une entité JPA en modèle de domaine.
     * 
     * @param entity l'entité JPA
     * @return le modèle de domaine
     */
    public Vehicle toDomainModel(VehicleEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Vehicle.builder()
            .id(entity.getId())
            .registrationPlate(entity.getRegistrationPlate())
            .brand(entity.getBrand())
            .model(entity.getModel())
            .motorization(entity.getMotorization())
            .color(entity.getColor())
            .acquisitionDate(entity.getAcquisitionDate())
            .status(entity.getStatus())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
    
    /**
     * Convertit un modèle de domaine en entité JPA.
     * 
     * @param model le modèle de domaine
     * @return l'entité JPA
     */
    public VehicleEntity toEntity(Vehicle model) {
        if (model == null) {
            return null;
        }
        
        return VehicleEntity.builder()
            .id(model.getId())
            .registrationPlate(model.getRegistrationPlate())
            .brand(model.getBrand())
            .model(model.getModel())
            .motorization(model.getMotorization())
            .color(model.getColor())
            .acquisitionDate(model.getAcquisitionDate())
            .status(model.getStatus())
            .createdAt(model.getCreatedAt())
            .updatedAt(model.getUpdatedAt())
            .build();
    }
}
