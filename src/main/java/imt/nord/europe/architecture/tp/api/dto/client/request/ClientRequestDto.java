package imt.nord.europe.architecture.tp.api.dto.client.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de requête pour la création/modification d'un client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientRequestDto {
    
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String licenseNumber;
    private String address;
    private String email;
    private String phone;
}
