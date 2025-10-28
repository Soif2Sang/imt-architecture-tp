package imt.nord.europe.architecture.tp.api.dto.client;

import imt.nord.europe.architecture.tp.api.dto.client.request.ClientRequestDto;
import imt.nord.europe.architecture.tp.api.dto.client.response.ClientResponseDto;
import imt.nord.europe.architecture.tp.business.client.models.Client;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir entre les DTOs Client et le modèle métier Client.
 */
@Component
public class ClientDtoMapper {
    
    /**
     * Convertit un modèle métier en DTO de réponse.
     * 
     * @param client le modèle métier
     * @return le DTO de réponse
     */
    public ClientResponseDto toResponseDto(Client client) {
        if (client == null) {
            return null;
        }
        
        return ClientResponseDto.builder()
            .id(client.getId())
            .firstName(client.getFirstName())
            .lastName(client.getLastName())
            .dateOfBirth(client.getDateOfBirth())
            .licenseNumber(client.getLicenseNumber())
            .address(client.getAddress())
            .email(client.getEmail())
            .phone(client.getPhone())
            .createdAt(client.getCreatedAt())
            .updatedAt(client.getUpdatedAt())
            .build();
    }
    
    /**
     * Convertit un DTO de requête en modèle métier.
     * Note : L'ID sera défini par la base de données.
     * 
     * @param requestDto le DTO de requête
     * @return le modèle métier
     */
    public Client toDomainModel(ClientRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }
        
        return Client.builder()
            .firstName(requestDto.getFirstName())
            .lastName(requestDto.getLastName())
            .dateOfBirth(requestDto.getDateOfBirth())
            .licenseNumber(requestDto.getLicenseNumber())
            .address(requestDto.getAddress())
            .email(requestDto.getEmail())
            .phone(requestDto.getPhone())
            .build();
    }
}
