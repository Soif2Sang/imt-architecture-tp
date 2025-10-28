package imt.nord.europe.architecture.tp.api.rest;

import imt.nord.europe.architecture.tp.api.dto.contract.ContractDtoMapper;
import imt.nord.europe.architecture.tp.api.dto.contract.request.ContractRequestDto;
import imt.nord.europe.architecture.tp.api.dto.contract.response.ContractResponseDto;
import imt.nord.europe.architecture.tp.business.contract.models.Contract;
import imt.nord.europe.architecture.tp.business.contract.services.ContractService;
import imt.nord.europe.architecture.tp.common.exceptions.BusinessException;
import imt.nord.europe.architecture.tp.common.enums.ContractStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur REST pour la gestion des contrats (locations).
 * Expose les endpoints pour créer, récupérer, modifier et gérer les statuts des contrats.
 */
@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ContractController {
    
    private final ContractService contractService;
    private final ContractDtoMapper contractDtoMapper;
    
    /**
     * Récupère les contrats avec filtrage optionnel.
     * Les filtres clientId, vehicleId et status peuvent être combinés et s'appliquent à la base de données.
     * Si aucun filtre n'est fourni, retourne tous les contrats.
     * 
     * @param clientId optionnel - filtrer par ID du client
     * @param vehicleId optionnel - filtrer par ID du véhicule
     * @param status optionnel - filtrer par statut (PENDING, ONGOING, COMPLETED, OVERDUE, CANCELLED)
     * @return la liste des contrats correspondants aux critères
     */
    @GetMapping
    public ResponseEntity<List<ContractResponseDto>> getContracts(
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) Long vehicleId,
            @RequestParam(required = false) ContractStatus status) {
        
        List<ContractResponseDto> contracts = contractService.getContractsByFilters(clientId, vehicleId, status)
            .stream()
            .map(contractDtoMapper::toResponseDto)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(contracts);
    }
    
    /**
     * Récupère un contrat par son ID.
     * 
     * @param id l'ID du contrat
     * @return le contrat correspondant
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContractResponseDto> getContractById(@PathVariable Long id) {
        Contract contract = contractService.getContractById(id);
        return ResponseEntity.ok(contractDtoMapper.toResponseDto(contract));
    }
    
    /**
     * Crée un nouveau contrat.
     * 
     * @param requestDto les données du contrat à créer
     * @return le contrat créé avec son ID
     */
    @PostMapping
    public ResponseEntity<ContractResponseDto> createContract(@RequestBody ContractRequestDto requestDto) {
        Contract contract = contractService.createContract(
            requestDto.getClientId(),
            requestDto.getVehicleId(),
            requestDto.getStartDate(),
            requestDto.getEndDate()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(contractDtoMapper.toResponseDto(contract));
    }
    
    /**
     * Modifie un contrat existant.
     * 
     * @param id l'ID du contrat à modifier
     * @param requestDto les nouvelles données du contrat
     * @return le contrat modifié
     */
    @PutMapping("/{id}")
    public ResponseEntity<ContractResponseDto> updateContract(@PathVariable Long id, @RequestBody ContractRequestDto requestDto) {
        Contract contract = contractService.updateContract(
            id,
            requestDto.getClientId(),
            requestDto.getVehicleId(),
            requestDto.getStartDate(),
            requestDto.getEndDate()
        );
        
        return ResponseEntity.ok(contractDtoMapper.toResponseDto(contract));
    }
    
    /**
     * Valide un contrat en attente (passe au statut ONGOING).
     * 
     * @param id l'ID du contrat à valider
     * @return le contrat validé
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<ContractResponseDto> approveContract(@PathVariable Long id) {
        Contract contract = contractService.approveContract(id);
        return ResponseEntity.ok(contractDtoMapper.toResponseDto(contract));
    }
    
    /**
     * Termine un contrat en cours (passe au statut COMPLETED).
     * 
     * @param id l'ID du contrat à terminer
     * @return le contrat terminé
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<ContractResponseDto> completeContract(@PathVariable Long id) {
        Contract contract = contractService.completeContract(id);
        return ResponseEntity.ok(contractDtoMapper.toResponseDto(contract));
    }
    
    /**
     * Marque un contrat comme en retard (passe au statut OVERDUE).
     * 
     * @param id l'ID du contrat
     * @return le contrat marqué comme en retard
     */
    @PostMapping("/{id}/overdue")
    public ResponseEntity<ContractResponseDto> markAsOverdue(@PathVariable Long id) {
        Contract contract = contractService.markAsOverdue(id);
        return ResponseEntity.ok(contractDtoMapper.toResponseDto(contract));
    }
    
    /**
     * Annule un contrat (passe au statut CANCELLED).
     * 
     * @param id l'ID du contrat à annuler
     * @return le contrat annulé
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ContractResponseDto> cancelContract(@PathVariable Long id) {
        Contract contract = contractService.cancelContract(id);
        return ResponseEntity.ok(contractDtoMapper.toResponseDto(contract));
    }
    
    /**
     * Supprime un contrat.
     * 
     * @param id l'ID du contrat à supprimer
     * @return une réponse 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        contractService.deleteContract(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Gère les exceptions métier et les retourne en tant que réponses HTTP.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ClientController.ErrorResponse> handleBusinessException(BusinessException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ClientController.ErrorResponse(e.getMessage())
        );
    }
}
