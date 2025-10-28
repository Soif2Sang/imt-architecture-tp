package imt.nord.europe.architecture.tp.api.dto.contract.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de requête pour la création/modification d'un contrat.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractRequestDto {
    
    private Long clientId;
    private Long vehicleId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
