package imt.nord.europe.architecture.tp.infrastructure.db.repository;

import imt.nord.europe.architecture.tp.common.enums.VehicleStatus;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'accès aux données des véhicules.
 */
@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {

    /**
     * Recherche un véhicule par sa plaque d'immatriculation.
     *
     * @param registrationPlate la plaque d'immatriculation
     * @return le véhicule trouvé
     */
    Optional<VehicleEntity> findByRegistrationPlate(String registrationPlate);

    /**
     * Recherche tous les véhicules d'une marque.
     *
     * @param brand la marque du véhicule
     * @return la liste des véhicules de cette marque
     */
    List<VehicleEntity> findByBrand(String brand);

    /**
     * Recherche tous les véhicules d'une couleur.
     *
     * @param color la couleur du véhicule
     * @return la liste des véhicules de cette couleur
     */
    List<VehicleEntity> findByColor(String color);

    /**
     * Recherche tous les véhicules avec un statut spécifique.
     *
     * @param status le statut du véhicule
     * @return la liste des véhicules avec ce statut
     */
    List<VehicleEntity> findByStatus(VehicleStatus status);

    /**
     * Recherche tous les véhicules disponibles à la location.
     *
     * @return la liste des véhicules disponibles
     */
    @Query("SELECT v FROM VehicleEntity v WHERE v.status = 'AVAILABLE'")
    List<VehicleEntity> findAvailableVehicles();

    /**
     * Recherche tous les véhicules actuellement en location.
     *
     * @return la liste des véhicules en location
     */
    @Query("SELECT v FROM VehicleEntity v WHERE v.status = 'RENTED'")
    List<VehicleEntity> findRentedVehicles();

    /**
     * Recherche tous les véhicules en panne.
     *
     * @return la liste des véhicules en panne
     */
    @Query("SELECT v FROM VehicleEntity v WHERE v.status = 'BROKEN_DOWN'")
    List<VehicleEntity> findBrokenDownVehicles();

    /**
     * Recherche les véhicules par marque et modèle.
     *
     * @param brand la marque
     * @param model le modèle
     * @return la liste des véhicules correspondants
     */
    @Query("SELECT v FROM VehicleEntity v WHERE v.brand = :brand AND v.model = :model")
    List<VehicleEntity> findByBrandAndModel(@Param("brand") String brand, @Param("model") String model);

    /**
     * Recherche les véhicules par motorisation.
     *
     * @param motorization la motorisation
     * @return la liste des véhicules avec cette motorisation
     */
    List<VehicleEntity> findByMotorization(String motorization);

    /**
     * Recherche les véhicules avec filtrage optionnel par statut et/ou marque.
     * 
     * @param status optionnel - le statut du véhicule
     * @param brand optionnel - la marque du véhicule
     * @return la liste des véhicules correspondants aux critères
     */
    @Query("SELECT v FROM VehicleEntity v WHERE " +
           "(:status IS NULL OR v.status = :status) AND " +
           "(:brand IS NULL OR v.brand ILIKE :brand)")
    List<VehicleEntity> findByStatusAndBrand(@Param("status") imt.nord.europe.architecture.tp.common.enums.VehicleStatus status, 
                                              @Param("brand") String brand);
}
