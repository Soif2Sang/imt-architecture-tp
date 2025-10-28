package imt.nord.europe.architecture.tp.infrastructure.db.repository;

import imt.nord.europe.architecture.tp.infrastructure.db.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'accès aux données des clients.
 */
@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    /**
     * Recherche un client par son email.
     *
     * @param email l'email du client
     * @return le client trouvé
     */
    Optional<ClientEntity> findByEmail(String email);

    /**
     * Recherche un client par son numéro de permis.
     *
     * @param licenseNumber le numéro de permis
     * @return le client trouvé
     */
    Optional<ClientEntity> findByLicenseNumber(String licenseNumber);

    /**
     * Recherche tous les clients par leur nom.
     *
     * @param lastName le nom du client
     * @return la liste des clients correspondants
     */
    List<ClientEntity> findByLastName(String lastName);

    /**
     * Recherche tous les clients par prénom et nom.
     *
     * @param firstName le prénom du client
     * @param lastName le nom du client
     * @return la liste des clients correspondants
     */
    List<ClientEntity> findByFirstNameAndLastName(String firstName, String lastName);

    /**
     * Recherche les clients par email contenant.
     *
     * @param emailPart partie de l'email
     * @return la liste des clients correspondants
     */
    @Query("SELECT c FROM ClientEntity c WHERE LOWER(c.email) LIKE LOWER(CONCAT('%', :emailPart, '%'))")
    List<ClientEntity> findByEmailContaining(@Param("emailPart") String emailPart);

    /**
     * Recherche un client par prénom, nom et date de naissance (identité composée).
     * Cette méthode est utilisée pour vérifier l'unicité composite d'un client.
     *
     * @param firstName le prénom du client
     * @param lastName le nom du client
     * @param dateOfBirth la date de naissance
     * @return le client trouvé avec cette identité composite
     */
    Optional<ClientEntity> findByFirstNameAndLastNameAndDateOfBirth(String firstName, String lastName, java.time.LocalDate dateOfBirth);
}
