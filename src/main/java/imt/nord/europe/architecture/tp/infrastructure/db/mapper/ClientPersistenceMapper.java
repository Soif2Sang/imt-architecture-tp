package imt.nord.europe.architecture.tp.infrastructure.db.mapper;

import imt.nord.europe.architecture.tp.business.client.models.Client;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.ClientEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper pour la conversion entre ClientEntity (JPA) et Client (modèle métier).
 * Ce mapper gère la transformation entre la représentation persistante et la représentation métier.
 */
@Component
public class ClientPersistenceMapper {
    
    /**
     * Convertit une entité JPA en modèle de domaine.
     * 
     * @param entity l'entité JPA
     * @return le modèle de domaine
     */
    public Client toDomainModel(ClientEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Client.builder()
            .id(entity.getId())
            .firstName(entity.getFirstName())
            .lastName(entity.getLastName())
            .dateOfBirth(entity.getDateOfBirth())
            .licenseNumber(entity.getLicenseNumber())
            .address(entity.getAddress())
            .email(entity.getEmail())
            .phone(entity.getPhone())
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
    public ClientEntity toEntity(Client model) {
        if (model == null) {
            return null;
        }
        
        return ClientEntity.builder()
            .id(model.getId())
            .firstName(model.getFirstName())
            .lastName(model.getLastName())
            .dateOfBirth(model.getDateOfBirth())
            .licenseNumber(model.getLicenseNumber())
            .address(model.getAddress())
            .email(model.getEmail())
            .phone(model.getPhone())
            .createdAt(model.getCreatedAt())
            .updatedAt(model.getUpdatedAt())
            .build();
    }
}
