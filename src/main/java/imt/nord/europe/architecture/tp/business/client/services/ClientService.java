package imt.nord.europe.architecture.tp.business.client.services;

import imt.nord.europe.architecture.tp.business.client.models.Client;
import imt.nord.europe.architecture.tp.business.client.validators.ClientValidator;
import imt.nord.europe.architecture.tp.common.exceptions.ResourceNotFoundException;
import imt.nord.europe.architecture.tp.infrastructure.db.entity.ClientEntity;
import imt.nord.europe.architecture.tp.infrastructure.db.mapper.ClientPersistenceMapper;
import imt.nord.europe.architecture.tp.infrastructure.db.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service métier pour la gestion des clients.
 * Applique les règles métier lors de la création, modification et suppression de clients.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {
    
    private final ClientRepository clientRepository;
    private final ClientPersistenceMapper clientMapper;
    private final ClientValidator clientValidator;
    
    /**
     * Récupère un client par son ID.
     * 
     * @param clientId l'ID du client
     * @return le client correspondant
     * @throws ResourceNotFoundException si le client n'existe pas
     */
    @Transactional(readOnly = true)
    public Client getClientById(Long clientId) {
        return clientRepository.findById(clientId)
            .map(clientMapper::toDomainModel)
            .orElseThrow(() -> new ResourceNotFoundException("Le client avec l'ID " + clientId + " n'existe pas"));
    }
    
    /**
     * Récupère un client par son email.
     * 
     * @param email l'email du client
     * @return le client correspondant
     * @throws ResourceNotFoundException si le client n'existe pas
     */
    @Transactional(readOnly = true)
    public Client getClientByEmail(String email) {
        return clientRepository.findByEmail(email)
            .map(clientMapper::toDomainModel)
            .orElseThrow(() -> new ResourceNotFoundException("Le client avec l'email '" + email + "' n'existe pas"));
    }
    
    /**
     * Récupère tous les clients.
     * 
     * @return la liste de tous les clients
     */
    @Transactional(readOnly = true)
    public List<Client> getAllClients() {
        return clientRepository.findAll()
            .stream()
            .map(clientMapper::toDomainModel)
            .collect(Collectors.toList());
    }
    
    /**
     * Récupère tous les clients avec un certain nom.
     * 
     * @param lastName le nom du client
     * @return la liste des clients correspondants
     */
    @Transactional(readOnly = true)
    public List<Client> getClientsByLastName(String lastName) {
        return clientRepository.findByLastName(lastName)
            .stream()
            .map(clientMapper::toDomainModel)
            .collect(Collectors.toList());
    }
    
    /**
     * Crée un nouveau client avec validation métier.
     * 
     * @param firstName le prénom du client
     * @param lastName le nom du client
     * @param dateOfBirth la date de naissance
     * @param licenseNumber le numéro de permis
     * @param address l'adresse
     * @param email l'email
     * @param phone le téléphone
     * @return le client créé
     * @throws ValidationException si les données ne respectent pas les règles métier
     */
    public Client createClient(String firstName, String lastName, LocalDate dateOfBirth,
                               String licenseNumber, String address, String email, String phone) {
        // Validation métier
        clientValidator.validateForCreation(firstName, lastName, dateOfBirth, licenseNumber);
        
        // Création de l'entité
        ClientEntity entity = ClientEntity.builder()
            .firstName(firstName)
            .lastName(lastName)
            .dateOfBirth(dateOfBirth)
            .licenseNumber(licenseNumber)
            .address(address)
            .email(email)
            .phone(phone)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        // Sauvegarde
        ClientEntity savedEntity = clientRepository.save(entity);
        return clientMapper.toDomainModel(savedEntity);
    }
    
    /**
     * Modifie un client existant avec validation métier.
     * 
     * @param clientId l'ID du client à modifier
     * @param firstName le nouveau prénom
     * @param lastName le nouveau nom
     * @param dateOfBirth la nouvelle date de naissance
     * @param licenseNumber le nouveau numéro de permis
     * @param address la nouvelle adresse
     * @param email le nouvel email
     * @param phone le nouveau téléphone
     * @return le client modifié
     * @throws ResourceNotFoundException si le client n'existe pas
     * @throws ValidationException si les données ne respectent pas les règles métier
     */
    public Client updateClient(Long clientId, String firstName, String lastName, LocalDate dateOfBirth,
                               String licenseNumber, String address, String email, String phone) {
        // Récupération du client existant
        ClientEntity entity = clientRepository.findById(clientId)
            .orElseThrow(() -> new ResourceNotFoundException("Le client avec l'ID " + clientId + " n'existe pas"));
        
        // Validation métier
        clientValidator.validateForUpdate(clientId, firstName, lastName, dateOfBirth, licenseNumber);
        
        // Mise à jour des champs
        entity.setFirstName(firstName);
        entity.setLastName(lastName);
        entity.setDateOfBirth(dateOfBirth);
        entity.setLicenseNumber(licenseNumber);
        entity.setAddress(address);
        entity.setEmail(email);
        entity.setPhone(phone);
        entity.setUpdatedAt(LocalDateTime.now());
        
        // Sauvegarde
        ClientEntity updatedEntity = clientRepository.save(entity);
        return clientMapper.toDomainModel(updatedEntity);
    }
    
    /**
     * Supprime un client.
     * 
     * @param clientId l'ID du client à supprimer
     * @throws ResourceNotFoundException si le client n'existe pas
     */
    public void deleteClient(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Le client avec l'ID " + clientId + " n'existe pas");
        }
        clientRepository.deleteById(clientId);
    }
}
