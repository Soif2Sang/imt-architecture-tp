package imt.nord.europe.architecture.tp.infrastructure.db.repository;

import imt.nord.europe.architecture.tp.common.enums.ContractStatus;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.ContractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour l'accès aux données des contrats de location.
 */
@Repository
public interface ContractRepository extends JpaRepository<ContractEntity, Long> {

    /**
     * Recherche tous les contrats d'un client.
     *
     * @param clientId l'ID du client
     * @return la liste des contrats du client
     */
    List<ContractEntity> findByClientId(Long clientId);

    /**
     * Recherche tous les contrats d'un véhicule.
     *
     * @param vehicleId l'ID du véhicule
     * @return la liste des contrats du véhicule
     */
    List<ContractEntity> findByVehicleId(Long vehicleId);

    /**
     * Recherche tous les contrats avec un statut spécifique.
     *
     * @param status le statut du contrat
     * @return la liste des contrats avec ce statut
     */
    List<ContractEntity> findByStatus(ContractStatus status);

    /**
     * Recherche les contrats en attente (PENDING).
     *
     * @return la liste des contrats en attente
     */
    @Query("SELECT c FROM ContractEntity c WHERE c.status = 'PENDING'")
    List<ContractEntity> findPendingContracts();

    /**
     * Recherche les contrats en cours (ONGOING).
     *
     * @return la liste des contrats en cours
     */
    @Query("SELECT c FROM ContractEntity c WHERE c.status = 'ONGOING' AND c.startDate <= CURRENT_TIMESTAMP AND c.endDate > CURRENT_TIMESTAMP")
    List<ContractEntity> findOngoingContracts();

    /**
     * Recherche les contrats terminés (COMPLETED).
     *
     * @return la liste des contrats terminés
     */
    @Query("SELECT c FROM ContractEntity c WHERE c.status = 'COMPLETED'")
    List<ContractEntity> findCompletedContracts();

    /**
     * Recherche les contrats en retard (OVERDUE - endDate dépassée mais non clôturés).
     *
     * @return la liste des contrats en retard
     */
    @Query("SELECT c FROM ContractEntity c WHERE c.status = 'OVERDUE' OR (c.endDate < CURRENT_TIMESTAMP AND c.status IN ('ONGOING', 'PENDING'))")
    List<ContractEntity> findOverdueContracts();

    /**
     * Recherche les contrats annulés (CANCELLED).
     *
     * @return la liste des contrats annulés
     */
    @Query("SELECT c FROM ContractEntity c WHERE c.status = 'CANCELLED'")
    List<ContractEntity> findCancelledContracts();

    /**
     * Recherche les contrats d'un client avec un statut spécifique.
     *
     * @param clientId l'ID du client
     * @param status le statut du contrat
     * @return la liste des contrats correspondants
     */
    @Query("SELECT c FROM ContractEntity c WHERE c.client.id = :clientId AND c.status = :status")
    List<ContractEntity> findByClientIdAndStatus(@Param("clientId") Long clientId, @Param("status") ContractStatus status);

    /**
     * Recherche les contrats d'un véhicule avec un statut spécifique.
     *
     * @param vehicleId l'ID du véhicule
     * @param status le statut du contrat
     * @return la liste des contrats correspondants
     */
    @Query("SELECT c FROM ContractEntity c WHERE c.vehicle.id = :vehicleId AND c.status = :status")
    List<ContractEntity> findByVehicleIdAndStatus(@Param("vehicleId") Long vehicleId, @Param("status") ContractStatus status);

    /**
     * Recherche les contrats qui se chevauchent pour un véhicule donné.
     * Utile pour vérifier la disponibilité d'un véhicule.
     *
     * @param vehicleId l'ID du véhicule
     * @param startDate la date de début
     * @param endDate la date de fin
     * @return la liste des contrats en conflit
     */
    @Query("SELECT c FROM ContractEntity c WHERE c.vehicle.id = :vehicleId " +
           "AND c.status NOT IN ('CANCELLED', 'COMPLETED') " +
           "AND c.startDate < :endDate AND c.endDate > :startDate")
    List<ContractEntity> findConflictingContracts(@Param("vehicleId") Long vehicleId,
                                                   @Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);

    /**
     * Recherche les contrats avec filtrage optionnel par clientId, vehicleId et/ou statut.
     * 
     * @param clientId optionnel - l'ID du client
     * @param vehicleId optionnel - l'ID du véhicule
     * @param status optionnel - le statut du contrat
     * @return la liste des contrats correspondants aux critères
     */
    @Query("SELECT c FROM ContractEntity c WHERE " +
           "(:clientId IS NULL OR c.client.id = :clientId) AND " +
           "(:vehicleId IS NULL OR c.vehicle.id = :vehicleId) AND " +
           "(:status IS NULL OR c.status = :status)")
    List<ContractEntity> findByClientIdAndVehicleIdAndStatus(@Param("clientId") Long clientId,
                                                              @Param("vehicleId") Long vehicleId,
                                                              @Param("status") imt.nord.europe.architecture.tp.common.enums.ContractStatus status);

    /**
     * Recherche les contrats OVERDUE qui bloquent des contrats PENDING.
     * Un contrat OVERDUE bloque un PENDING si :
     * - Ils concernent le MÊME véhicule
     * - La date de fin du OVERDUE dépasse la date de début du PENDING
     * 
     * @return la liste des contrats OVERDUE qui bloquent des contrats PENDING
     */
    @Query("SELECT DISTINCT o FROM ContractEntity o " +
           "INNER JOIN ContractEntity p ON o.vehicle.id = p.vehicle.id " +
           "WHERE o.status = 'OVERDUE' " +
           "AND p.status = 'PENDING' " +
           "AND o.endDate > p.startDate")
    List<ContractEntity> findOverdueContractsThatBlockPendingContracts();

    /**
     * Recherche les contrats ONGOING dont la date de fin est dépassée.
     * Utilisé pour identifier les contrats qui doivent passer en OVERDUE.
     * 
     * @param now la date et heure actuelle
     * @return la liste des contrats ONGOING en retard
     */
    @Query("SELECT c FROM ContractEntity c " +
           "WHERE c.status = 'ONGOING' " +
           "AND c.endDate < :now")
    List<ContractEntity> findOverdueOngoingContracts(@Param("now") LocalDateTime now);
}
