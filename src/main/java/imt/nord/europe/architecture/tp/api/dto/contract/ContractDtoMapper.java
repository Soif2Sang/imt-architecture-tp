package imt.nord.europe.architecture.tp.api.dto.contract;

import imt.nord.europe.architecture.tp.api.dto.contract.request.ContractRequestDto;
import imt.nord.europe.architecture.tp.api.dto.contract.response.ContractResponseDto;
import imt.nord.europe.architecture.tp.api.dto.client.ClientDtoMapper;
import imt.nord.europe.architecture.tp.api.dto.vehicle.VehicleDtoMapper;
import imt.nord.europe.architecture.tp.business.contract.models.Contract;
import imt.nord.europe.architecture.tp.common.enums.ContractStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir entre les DTOs Contract et le modèle métier Contract.
 */
@Component
@RequiredArgsConstructor
public class ContractDtoMapper {
    
    private final ClientDtoMapper clientDtoMapper;
    private final VehicleDtoMapper vehicleDtoMapper;
    
    /**
     * Convertit un modèle métier en DTO de réponse.
     * 
     * @param contract le modèle métier
     * @return le DTO de réponse
     */
    public ContractResponseDto toResponseDto(Contract contract) {
        if (contract == null) {
            return null;
        }
        
        return ContractResponseDto.builder()
            .id(contract.getId())
            .client(clientDtoMapper.toResponseDto(contract.getClient()))
            .vehicle(vehicleDtoMapper.toResponseDto(contract.getVehicle()))
            .startDate(contract.getStartDate())
            .endDate(contract.getEndDate())
            .status(contract.getStatus())
            .createdAt(contract.getCreatedAt())
            .updatedAt(contract.getUpdatedAt())
            .build();
    }
    
    /**
     * Convertit un DTO de requête en modèle métier.
     * Note : L'ID et les dates de création seront définis par la base de données.
     * Le statut sera défini à PENDING par défaut.
     * 
     * @param requestDto le DTO de requête
     * @return le modèle métier
     */
    public Contract toDomainModel(ContractRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }
        
        return Contract.builder()
            .startDate(requestDto.getStartDate())
            .endDate(requestDto.getEndDate())
            .status(ContractStatus.PENDING)
            .build();
    }
}
