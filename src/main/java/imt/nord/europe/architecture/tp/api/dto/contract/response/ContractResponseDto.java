package imt.nord.europe.architecture.tp.api.dto.contract.response;

import imt.nord.europe.architecture.tp.common.enums.ContractStatus;
import imt.nord.europe.architecture.tp.api.dto.client.response.ClientResponseDto;
import imt.nord.europe.architecture.tp.api.dto.vehicle.response.VehicleResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de réponse pour un contrat.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractResponseDto {
    
    private Long id;
    private ClientResponseDto client;
    private VehicleResponseDto vehicle;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ContractStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
