package imt.nord.europe.architecture.tp.business.vehicle.services;

import imt.nord.europe.architecture.tp.business.vehicle.models.Vehicle;
import imt.nord.europe.architecture.tp.business.vehicle.validators.VehicleValidator;
import imt.nord.europe.architecture.tp.common.enums.VehicleStatus;
import imt.nord.europe.architecture.tp.common.exceptions.ResourceNotFoundException;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.VehicleEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.mapper.VehiclePersistenceMapper;
import imt.nord.europe.architecture.tp.infrastructure.db.repository.VehicleRepository;
import imt.nord.europe.architecture.tp.infrastructure.event.SpringEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service métier pour la gestion des véhicules.
 * Applique les règles métier lors de la création, modification et gestion de la disponibilité des véhicules.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class VehicleService {
    
    private final VehicleRepository vehicleRepository;
    private final VehiclePersistenceMapper vehicleMapper;
    private final VehicleValidator vehicleValidator;
    private final SpringEventPublisher eventPublisher;
    
    /**
     * Récupère un véhicule par son ID.
     * 
     * @param vehicleId l'ID du véhicule
     * @return le véhicule correspondant
     * @throws ResourceNotFoundException si le véhicule n'existe pas
     */
    @Transactional(readOnly = true)
    public Vehicle getVehicleById(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
            .map(vehicleMapper::toDomainModel)
            .orElseThrow(() -> new ResourceNotFoundException("Le véhicule avec l'ID " + vehicleId + " n'existe pas"));
    }
    
    /**
     * Récupère un véhicule par son numéro d'immatriculation.
     * 
     * @param registrationPlate le numéro d'immatriculation
     * @return le véhicule correspondant
     * @throws ResourceNotFoundException si le véhicule n'existe pas
     */
    @Transactional(readOnly = true)
    public Vehicle getVehicleByRegistrationPlate(String registrationPlate) {
        return vehicleRepository.findByRegistrationPlate(registrationPlate)
            .map(vehicleMapper::toDomainModel)
            .orElseThrow(() -> new ResourceNotFoundException("Le véhicule avec l'immatriculation '" + registrationPlate + "' n'existe pas"));
    }
    
    /**
     * Récupère tous les véhicules.
     * 
     * @return la liste de tous les véhicules
     */
    @Transactional(readOnly = true)
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll()
            .stream()
            .map(vehicleMapper::toDomainModel)
            .collect(Collectors.toList());
    }
    
    /**
     * Récupère tous les véhicules disponibles (statut AVAILABLE).
     * 
     * @return la liste des véhicules disponibles
     */
    @Transactional(readOnly = true)
    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.findByStatus(VehicleStatus.AVAILABLE)
            .stream()
            .map(vehicleMapper::toDomainModel)
            .collect(Collectors.toList());
    }
    
    /**
     * Récupère tous les véhicules loués (statut RENTED).
     * 
     * @return la liste des véhicules loués
     */
    @Transactional(readOnly = true)
    public List<Vehicle> getRentedVehicles() {
        return vehicleRepository.findByStatus(VehicleStatus.RENTED)
            .stream()
            .map(vehicleMapper::toDomainModel)
            .collect(Collectors.toList());
    }
    
    /**
     * Récupère tous les véhicules en panne (statut BROKEN_DOWN).
     * 
     * @return la liste des véhicules en panne
     */
    @Transactional(readOnly = true)
    public List<Vehicle> getBrokenDownVehicles() {
        return vehicleRepository.findByStatus(VehicleStatus.BROKEN_DOWN)
            .stream()
            .map(vehicleMapper::toDomainModel)
            .collect(Collectors.toList());
    }
    
    /**
     * Récupère tous les véhicules d'une certaine marque.
     * 
     * @param brand la marque du véhicule
     * @return la liste des véhicules correspondants
     */
    @Transactional(readOnly = true)
    public List<Vehicle> getVehiclesByBrand(String brand) {
        return vehicleRepository.findByBrand(brand)
            .stream()
            .map(vehicleMapper::toDomainModel)
            .collect(Collectors.toList());
    }
    
    /**
     * Récupère les véhicules filtrés par statut et marque.
     * Les paramètres sont optionnels (null = pas de filtre sur ce paramètre).
     * 
     * @param status le statut du véhicule (optionnel)
     * @param brand la marque du véhicule (optionnel)
     * @return la liste des véhicules correspondants
     */
    @Transactional(readOnly = true)
    public List<Vehicle> getVehiclesByStatusAndBrand(VehicleStatus status, String brand) {
        return vehicleRepository.findByStatusAndBrand(status, brand)
            .stream()
            .map(vehicleMapper::toDomainModel)
            .collect(Collectors.toList());
    }
    
    /**
     * Crée un nouveau véhicule avec validation métier.
     * 
     * @param registrationPlate le numéro d'immatriculation
     * @param brand la marque
     * @param model le modèle
     * @param motorization la motorisation
     * @param color la couleur
     * @param acquisitionDate la date d'acquisition
     * @return le véhicule créé
     * @throws ValidationException si les données ne respectent pas les règles métier
     */
    public Vehicle createVehicle(String registrationPlate, String brand, String model,
                                String motorization, String color, LocalDate acquisitionDate) {
        // Validation métier
        vehicleValidator.validateForCreation(registrationPlate, brand, model, acquisitionDate);
        
        // Création de l'entité
        VehicleEntity entity = VehicleEntity.builder()
            .registrationPlate(registrationPlate)
            .brand(brand)
            .model(model)
            .motorization(motorization)
            .color(color)
            .acquisitionDate(acquisitionDate)
            .status(VehicleStatus.AVAILABLE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        // Sauvegarde
        VehicleEntity savedEntity = vehicleRepository.save(entity);
        return vehicleMapper.toDomainModel(savedEntity);
    }
    
    /**
     * Modifie un véhicule existant avec validation métier.
     * 
     * @param vehicleId l'ID du véhicule à modifier
     * @param registrationPlate le nouveau numéro d'immatriculation
     * @param brand la nouvelle marque
     * @param model le nouveau modèle
     * @param motorization la nouvelle motorisation
     * @param color la nouvelle couleur
     * @param acquisitionDate la nouvelle date d'acquisition
     * @return le véhicule modifié
     * @throws ResourceNotFoundException si le véhicule n'existe pas
     * @throws ValidationException si les données ne respectent pas les règles métier
     */
    public Vehicle updateVehicle(Long vehicleId, String registrationPlate, String brand, String model,
                                String motorization, String color, LocalDate acquisitionDate) {
        // Récupération du véhicule existant
        VehicleEntity entity = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new ResourceNotFoundException("Le véhicule avec l'ID " + vehicleId + " n'existe pas"));
        
        // Validation métier
        vehicleValidator.validateForUpdate(vehicleId, registrationPlate, brand, model, acquisitionDate);
        
        // Mise à jour des champs
        entity.setRegistrationPlate(registrationPlate);
        entity.setBrand(brand);
        entity.setModel(model);
        entity.setMotorization(motorization);
        entity.setColor(color);
        entity.setAcquisitionDate(acquisitionDate);
        entity.setUpdatedAt(LocalDateTime.now());
        
        // Sauvegarde
        VehicleEntity updatedEntity = vehicleRepository.save(entity);
        return vehicleMapper.toDomainModel(updatedEntity);
    }
    
    /**
     * Change le statut d'un véhicule.
     * 
     * @param vehicleId l'ID du véhicule
     * @param newStatus le nouveau statut
     * @return le véhicule avec son nouveau statut
     * @throws ResourceNotFoundException si le véhicule n'existe pas
     */
    public Vehicle updateVehicleStatus(Long vehicleId, VehicleStatus newStatus) {
        VehicleEntity entity = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new ResourceNotFoundException("Le véhicule avec l'ID " + vehicleId + " n'existe pas"));
        
        entity.setStatus(newStatus);
        entity.setUpdatedAt(LocalDateTime.now());
        
        VehicleEntity updatedEntity = vehicleRepository.save(entity);
        return vehicleMapper.toDomainModel(updatedEntity);
    }
    
    /**
     * Marque un véhicule comme en panne.
     * Déclenche automatiquement l'annulation des contrats en attente associés à ce véhicule.
     * 
     * @param vehicleId l'ID du véhicule
     * @return le véhicule marqué comme en panne
     * @throws ResourceNotFoundException si le véhicule n'existe pas
     */
    public Vehicle markAsBrokenDown(Long vehicleId) {
        Vehicle vehicle = updateVehicleStatus(vehicleId, VehicleStatus.BROKEN_DOWN);
        // Publier l'événement pour que les contrats en attente soient annulés
        eventPublisher.publishVehicleBreakdownEvent(vehicleId);
        return vehicle;
    }
    
    /**
     * Marque un véhicule comme disponible.
     * 
     * @param vehicleId l'ID du véhicule
     * @return le véhicule marqué comme disponible
     * @throws ResourceNotFoundException si le véhicule n'existe pas
     */
    public Vehicle markAsAvailable(Long vehicleId) {
        return updateVehicleStatus(vehicleId, VehicleStatus.AVAILABLE);
    }
    
    /**
     * Marque un véhicule comme loué.
     * 
     * @param vehicleId l'ID du véhicule
     * @return le véhicule marqué comme loué
     * @throws ResourceNotFoundException si le véhicule n'existe pas
     */
    public Vehicle markAsRented(Long vehicleId) {
        return updateVehicleStatus(vehicleId, VehicleStatus.RENTED);
    }
    
    /**
     * Supprime un véhicule.
     * 
     * @param vehicleId l'ID du véhicule à supprimer
     * @throws ResourceNotFoundException si le véhicule n'existe pas
     */
    public void deleteVehicle(Long vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new ResourceNotFoundException("Le véhicule avec l'ID " + vehicleId + " n'existe pas");
        }
        vehicleRepository.deleteById(vehicleId);
    }
}
