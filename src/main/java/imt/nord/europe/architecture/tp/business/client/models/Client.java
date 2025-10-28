package imt.nord.europe.architecture.tp.business.client.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modèle de domaine pour un client.
 * Représente un client dans la couche métier, indépendant de la représentation JPA.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {
    
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
    
    /**
     * Retourne le nom complet du client.
     * 
     * @return le nom complet (prénom + nom)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
