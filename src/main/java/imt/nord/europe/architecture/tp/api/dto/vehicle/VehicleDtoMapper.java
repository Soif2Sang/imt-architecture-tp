package imt.nord.europe.architecture.tp.api.dto.vehicle;

import imt.nord.europe.architecture.tp.api.dto.vehicle.request.VehicleRequestDto;
import imt.nord.europe.architecture.tp.api.dto.vehicle.response.VehicleResponseDto;
import imt.nord.europe.architecture.tp.business.vehicle.models.Vehicle;
import imt.nord.europe.architecture.tp.common.enums.VehicleStatus;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir entre les DTOs Vehicle et le modèle métier Vehicle.
 */
@Component
public class VehicleDtoMapper {
    
    /**
     * Convertit un modèle métier en DTO de réponse.
     * 
     * @param vehicle le modèle métier
     * @return le DTO de réponse
     */
    public VehicleResponseDto toResponseDto(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }
        
        return VehicleResponseDto.builder()
            .id(vehicle.getId())
            .registrationPlate(vehicle.getRegistrationPlate())
            .brand(vehicle.getBrand())
            .model(vehicle.getModel())
            .motorization(vehicle.getMotorization())
            .color(vehicle.getColor())
            .acquisitionDate(vehicle.getAcquisitionDate())
            .status(vehicle.getStatus())
            .createdAt(vehicle.getCreatedAt())
            .updatedAt(vehicle.getUpdatedAt())
            .build();
    }
    
    /**
     * Convertit un DTO de requête en modèle métier.
     * Note : L'ID et le statut seront définis par la base de données (AVAILABLE par défaut).
     * 
     * @param requestDto le DTO de requête
     * @return le modèle métier
     */
    public Vehicle toDomainModel(VehicleRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }
        
        return Vehicle.builder()
            .registrationPlate(requestDto.getRegistrationPlate())
            .brand(requestDto.getBrand())
            .model(requestDto.getModel())
            .motorization(requestDto.getMotorization())
            .color(requestDto.getColor())
            .acquisitionDate(requestDto.getAcquisitionDate())
            .status(VehicleStatus.AVAILABLE)
            .build();
    }
}
