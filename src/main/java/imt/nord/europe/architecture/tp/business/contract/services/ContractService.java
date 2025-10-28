package imt.nord.europe.architecture.tp.business.contract.services;

import imt.nord.europe.architecture.tp.business.contract.models.Contract;
import imt.nord.europe.architecture.tp.business.contract.validators.ContractValidator;
import imt.nord.europe.architecture.tp.common.enums.ContractStatus;
import imt.nord.europe.architecture.tp.common.exceptions.ResourceNotFoundException;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.ClientEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.ContractEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.VehicleEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.mapper.ContractPersistenceMapper;
import imt.nord.europe.architecture.tp.infrastructure.db.repository.ClientRepository;
import imt.nord.europe.architecture.tp.infrastructure.db.repository.ContractRepository;
import imt.nord.europe.architecture.tp.infrastructure.db.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service métier pour la gestion des contrats (locations).
 * Applique les règles métier lors de la création, modification et gestion du cycle de vie des contrats.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ContractService {
    
    private final ContractRepository contractRepository;
    private final ClientRepository clientRepository;
    private final VehicleRepository vehicleRepository;
    private final ContractPersistenceMapper contractMapper;
    private final ContractValidator contractValidator;
    
    /**
     * Récupère un contrat par son ID.
     * 
     * @param contractId l'ID du contrat
     * @return le contrat correspondant
     * @throws ResourceNotFoundException si le contrat n'existe pas
     */
    @Transactional(readOnly = true)
    public Contract getContractById(Long contractId) {
        return contractRepository.findById(contractId)
            .map(contractMapper::toDomainModel)
            .orElseThrow(() -> new ResourceNotFoundException("Le contrat avec l'ID " + contractId + " n'existe pas"));
    }
    
    /**
     * Récupère tous les contrats.
     * 
     * @return la liste de tous les contrats
     */
    @Transactional(readOnly = true)
    public List<Contract> getAllContracts() {
        return contractRepository.findAll()
            .stream()
            .map(contractMapper::toDomainModel)
            .collect(Collectors.toList());
    }
    
    /**
     * Récupère tous les contrats d'un client.
     * 
     * @param clientId l'ID du client
     * @return la liste des contrats du client
     */
    @Transactional(readOnly = true)
    public List<Contract> getContractsByClientId(Long clientId) {
        return contractRepository.findByClientId(clientId)
            .stream()
            .map(contractMapper::toDomainModel)
            .collect(Collectors.toList());
    }
    
    /**
     * Récupère tous les contrats d'un véhicule.
     * 
     * @param vehicleId l'ID du véhicule
     * @return la liste des contrats du véhicule
     */
    @Transactional(readOnly = true)
    public List<Contract> getContractsByVehicleId(Long vehicleId) {
        return contractRepository.findByVehicleId(vehicleId)
            .stream()
            .map(contractMapper::toDomainModel)
            .collect(Collectors.toList());
    }
    
    /**
     * Récupère tous les contrats avec un statut donné.
     * 
     * @param status le statut des contrats
     * @return la liste des contrats avec ce statut
     */
    @Transactional(readOnly = true)
    public List<Contract> getContractsByStatus(ContractStatus status) {
        return contractRepository.findByStatus(status)
            .stream()
            .map(contractMapper::toDomainModel)
            .collect(Collectors.toList());
    }
    
    /**
     * Récupère les contrats filtrés par clientId, vehicleId et statut.
     * Les paramètres sont optionnels (null = pas de filtre sur ce paramètre).
     * 
     * @param clientId l'ID du client (optionnel)
     * @param vehicleId l'ID du véhicule (optionnel)
     * @param status le statut du contrat (optionnel)
     * @return la liste des contrats correspondants
     */
    @Transactional(readOnly = true)
    public List<Contract> getContractsByFilters(Long clientId, Long vehicleId, ContractStatus status) {
        return contractRepository.findByClientIdAndVehicleIdAndStatus(clientId, vehicleId, status)
            .stream()
            .map(contractMapper::toDomainModel)
            .collect(Collectors.toList());
    }
    
    /**
     * Récupère tous les contrats en attente.
     * 
     * @return la liste des contrats en attente
     */
    @Transactional(readOnly = true)
    public List<Contract> getPendingContracts() {
        return contractRepository.findPendingContracts()
            .stream()
            .map(contractMapper::toDomainModel)
            .collect(Collectors.toList());
    }
    
    /**
     * Récupère tous les contrats en cours.
     * 
     * @return la liste des contrats en cours
     */
    @Transactional(readOnly = true)
    public List<Contract> getOngoingContracts() {
        return contractRepository.findOngoingContracts()
            .stream()
            .map(contractMapper::toDomainModel)
            .collect(Collectors.toList());
    }
    
    /**
     * Récupère tous les contrats en retard.
     * 
     * @return la liste des contrats en retard
     */
    @Transactional(readOnly = true)
    public List<Contract> getOverdueContracts() {
        return contractRepository.findOverdueContracts()
            .stream()
            .map(contractMapper::toDomainModel)
            .collect(Collectors.toList());
    }
    
    /**
     * Crée un nouveau contrat avec validation métier complète.
     * Vérifie :
     * - Le client existe
     * - Le véhicule existe et n'est pas en panne
     * - Pas de conflit de réservation pour le véhicule
     * 
     * @param clientId l'ID du client
     * @param vehicleId l'ID du véhicule
     * @param startDate la date de début de location
     * @param endDate la date de fin de location
     * @return le contrat créé
     * @throws ValidationException si les données ne respectent pas les règles métier
     */
    public Contract createContract(Long clientId, Long vehicleId, LocalDateTime startDate, LocalDateTime endDate) {
        // Validation métier complète
        contractValidator.validateForCreation(clientId, vehicleId, startDate, endDate);
        
        // Récupération des entités (validation nous assure qu'elles existent)
        ClientEntity client = clientRepository.findById(clientId).orElseThrow();
        VehicleEntity vehicle = vehicleRepository.findById(vehicleId).orElseThrow();
        
        // Création de l'entité contrat
        ContractEntity entity = ContractEntity.builder()
            .client(client)
            .vehicle(vehicle)
            .startDate(startDate)
            .endDate(endDate)
            .status(ContractStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        // Sauvegarde
        ContractEntity savedEntity = contractRepository.save(entity);
        return contractMapper.toDomainModel(savedEntity);
    }
    
    /**
     * Modifie un contrat existant avec validation métier.
     * 
     * @param contractId l'ID du contrat à modifier
     * @param clientId l'ID du client
     * @param vehicleId l'ID du véhicule
     * @param startDate la nouvelle date de début
     * @param endDate la nouvelle date de fin
     * @return le contrat modifié
     * @throws ResourceNotFoundException si le contrat n'existe pas
     * @throws ValidationException si les données ne respectent pas les règles métier
     */
    public Contract updateContract(Long contractId, Long clientId, Long vehicleId, 
                                  LocalDateTime startDate, LocalDateTime endDate) {
        // Récupération du contrat existant
        ContractEntity entity = contractRepository.findById(contractId)
            .orElseThrow(() -> new ResourceNotFoundException("Le contrat avec l'ID " + contractId + " n'existe pas"));
        
        // Validation métier
        contractValidator.validateForUpdate(contractId, clientId, vehicleId, startDate, endDate);
        
        // Récupération des entités
        ClientEntity client = clientRepository.findById(clientId).orElseThrow();
        VehicleEntity vehicle = vehicleRepository.findById(vehicleId).orElseThrow();
        
        // Mise à jour des champs
        entity.setClient(client);
        entity.setVehicle(vehicle);
        entity.setStartDate(startDate);
        entity.setEndDate(endDate);
        entity.setUpdatedAt(LocalDateTime.now());
        
        // Sauvegarde
        ContractEntity updatedEntity = contractRepository.save(entity);
        return contractMapper.toDomainModel(updatedEntity);
    }
    
    /**
     * Change le statut d'un contrat avec validation métier des transitions.
     * 
     * @param contractId l'ID du contrat
     * @param newStatus le nouveau statut
     * @return le contrat avec son nouveau statut
     * @throws ResourceNotFoundException si le contrat n'existe pas
     * @throws ValidationException si la transition n'est pas autorisée
     */
    public Contract updateContractStatus(Long contractId, ContractStatus newStatus) {
        ContractEntity entity = contractRepository.findById(contractId)
            .orElseThrow(() -> new ResourceNotFoundException("Le contrat avec l'ID " + contractId + " n'existe pas"));
        
        // Valider la transition via le validator
        contractValidator.validateStatusTransition(entity.getStatus(), newStatus);
        
        entity.setStatus(newStatus);
        entity.setUpdatedAt(LocalDateTime.now());
        
        ContractEntity updatedEntity = contractRepository.save(entity);
        return contractMapper.toDomainModel(updatedEntity);
    }
    
    /**
     * Valide un contrat en attente (passe le statut à ONGOING).
     * 
     * @param contractId l'ID du contrat à valider
     * @return le contrat validé
     * @throws ResourceNotFoundException si le contrat n'existe pas
     */
    public Contract approveContract(Long contractId) {
        return updateContractStatus(contractId, ContractStatus.ONGOING);
    }
    
    /**
     * Termine un contrat en cours (passe le statut à COMPLETED).
     * 
     * @param contractId l'ID du contrat à terminer
     * @return le contrat terminé
     * @throws ResourceNotFoundException si le contrat n'existe pas
     */
    public Contract completeContract(Long contractId) {
        return updateContractStatus(contractId, ContractStatus.COMPLETED);
    }
    
    /**
     * Marque un contrat comme en retard (passe le statut à OVERDUE).
     * 
     * @param contractId l'ID du contrat
     * @return le contrat marqué comme en retard
     * @throws ResourceNotFoundException si le contrat n'existe pas
     */
    public Contract markAsOverdue(Long contractId) {
        return updateContractStatus(contractId, ContractStatus.OVERDUE);
    }
    
    /**
     * Annule un contrat (passe le statut à CANCELLED).
     * 
     * @param contractId l'ID du contrat à annuler
     * @return le contrat annulé
     * @throws ResourceNotFoundException si le contrat n'existe pas
     */
    public Contract cancelContract(Long contractId) {
        return updateContractStatus(contractId, ContractStatus.CANCELLED);
    }
    
    /**
     * Supprime un contrat.
     * 
     * @param contractId l'ID du contrat à supprimer
     * @throws ResourceNotFoundException si le contrat n'existe pas
     */
    public void deleteContract(Long contractId) {
        if (!contractRepository.existsById(contractId)) {
            throw new ResourceNotFoundException("Le contrat avec l'ID " + contractId + " n'existe pas");
        }
        contractRepository.deleteById(contractId);
    }
}
