package imt.nord.europe.architecture.tp.api.dto.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de réponse pour un client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponseDto {
    
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String licenseNumber;
    private String address;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
