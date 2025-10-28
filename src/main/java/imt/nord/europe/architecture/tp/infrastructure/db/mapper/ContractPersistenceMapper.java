package imt.nord.europe.architecture.tp.infrastructure.db.mapper;

import imt.nord.europe.architecture.tp.business.contract.models.Contract;
import imt.nord.europe.architecture.tp.business.client.models.Client;
import imt.nord.europe.architecture.tp.business.vehicle.models.Vehicle;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.ContractEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper pour la conversion entre ContractEntity (JPA) et Contract (modèle métier).
 * Ce mapper gère la transformation entre la représentation persistante et la représentation métier.
 * Il utilise les autres mappers (ClientPersistenceMapper et VehiclePersistenceMapper) pour convertir les relations.
 */
@Component
@RequiredArgsConstructor
public class ContractPersistenceMapper {
    
    private final ClientPersistenceMapper clientMapper;
    private final VehiclePersistenceMapper vehicleMapper;
    
    /**
     * Convertit une entité JPA en modèle de domaine.
     * 
     * @param entity l'entité JPA
     * @return le modèle de domaine
     */
    public Contract toDomainModel(ContractEntity entity) {
        if (entity == null) {
            return null;
        }
        
        // Conversion du client et du véhicule associés
        Client client = entity.getClient() != null ? clientMapper.toDomainModel(entity.getClient()) : null;
        Vehicle vehicle = entity.getVehicle() != null ? vehicleMapper.toDomainModel(entity.getVehicle()) : null;
        
        return Contract.builder()
            .id(entity.getId())
            .client(client)
            .vehicle(vehicle)
            .startDate(entity.getStartDate())
            .endDate(entity.getEndDate())
            .status(entity.getStatus())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
    
    /**
     * Convertit un modèle de domaine en entité JPA.
     * Note : Les relations client et véhicule doivent déjà exister en base.
     * 
     * @param model le modèle de domaine
     * @return l'entité JPA
     */
    public ContractEntity toEntity(Contract model) {
        if (model == null) {
            return null;
        }
        
        return ContractEntity.builder()
            .id(model.getId())
            .client(model.getClient() != null ? clientMapper.toEntity(model.getClient()) : null)
            .vehicle(model.getVehicle() != null ? vehicleMapper.toEntity(model.getVehicle()) : null)
            .startDate(model.getStartDate())
            .endDate(model.getEndDate())
            .status(model.getStatus())
            .createdAt(model.getCreatedAt())
            .updatedAt(model.getUpdatedAt())
            .build();
    }
}
