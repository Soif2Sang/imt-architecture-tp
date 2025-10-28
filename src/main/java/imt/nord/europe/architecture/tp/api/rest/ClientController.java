package imt.nord.europe.architecture.tp.api.rest;

import imt.nord.europe.architecture.tp.api.dto.client.ClientDtoMapper;
import imt.nord.europe.architecture.tp.api.dto.client.request.ClientRequestDto;
import imt.nord.europe.architecture.tp.api.dto.client.response.ClientResponseDto;
import imt.nord.europe.architecture.tp.business.client.models.Client;
import imt.nord.europe.architecture.tp.business.client.services.ClientService;
import imt.nord.europe.architecture.tp.common.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur REST pour la gestion des clients.
 * Expose les endpoints pour créer, récupérer, modifier et supprimer des clients.
 */
@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {
    
    private final ClientService clientService;
    private final ClientDtoMapper clientDtoMapper;
    
    /**
     * Récupère les clients.
     * Si lastName est fourni, filtre par ce nom. Sinon, retourne tous les clients.
     * 
     * @param lastName optionnel - le nom du client pour filtrer
     * @return la liste des clients correspondants
     */
    @GetMapping
    public ResponseEntity<List<ClientResponseDto>> getClients(@RequestParam(required = false) String lastName) {
        List<ClientResponseDto> clients;
        
        if (lastName != null && !lastName.trim().isEmpty()) {
            clients = clientService.getClientsByLastName(lastName)
                .stream()
                .map(clientDtoMapper::toResponseDto)
                .collect(Collectors.toList());
        } else {
            clients = clientService.getAllClients()
                .stream()
                .map(clientDtoMapper::toResponseDto)
                .collect(Collectors.toList());
        }
        
        return ResponseEntity.ok(clients);
    }
    
    /**
     * Récupère un client par son ID.
     * 
     * @param id l'ID du client
     * @return le client correspondant
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDto> getClientById(@PathVariable Long id) {
        Client client = clientService.getClientById(id);
        return ResponseEntity.ok(clientDtoMapper.toResponseDto(client));
    }
    
    /**
     * Crée un nouveau client.
     * 
     * @param requestDto les données du client à créer
     * @return le client créé avec son ID
     */
    @PostMapping
    public ResponseEntity<ClientResponseDto> createClient(@RequestBody ClientRequestDto requestDto) {
        Client client = clientService.createClient(
            requestDto.getFirstName(),
            requestDto.getLastName(),
            requestDto.getDateOfBirth(),
            requestDto.getLicenseNumber(),
            requestDto.getAddress(),
            requestDto.getEmail(),
            requestDto.getPhone()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(clientDtoMapper.toResponseDto(client));
    }
    
    /**
     * Modifie un client existant.
     * 
     * @param id l'ID du client à modifier
     * @param requestDto les nouvelles données du client
     * @return le client modifié
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDto> updateClient(@PathVariable Long id, @RequestBody ClientRequestDto requestDto) {
        Client client = clientService.updateClient(
            id,
            requestDto.getFirstName(),
            requestDto.getLastName(),
            requestDto.getDateOfBirth(),
            requestDto.getLicenseNumber(),
            requestDto.getAddress(),
            requestDto.getEmail(),
            requestDto.getPhone()
        );
        
        return ResponseEntity.ok(clientDtoMapper.toResponseDto(client));
    }
    
    /**
     * Supprime un client.
     * 
     * @param id l'ID du client à supprimer
     * @return une réponse 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Gère les exceptions métier et les retourne en tant que réponses HTTP.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorResponse(e.getMessage())
        );
    }
    
    /**
     * Classe interne pour les réponses d'erreur.
     */
    public static class ErrorResponse {
        public String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}
