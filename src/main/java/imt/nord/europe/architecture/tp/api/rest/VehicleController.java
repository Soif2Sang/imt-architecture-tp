package imt.nord.europe.architecture.tp.api.rest;

import imt.nord.europe.architecture.tp.api.dto.vehicle.VehicleDtoMapper;
import imt.nord.europe.architecture.tp.api.dto.vehicle.request.VehicleRequestDto;
import imt.nord.europe.architecture.tp.api.dto.vehicle.response.VehicleResponseDto;
import imt.nord.europe.architecture.tp.business.vehicle.models.Vehicle;
import imt.nord.europe.architecture.tp.business.vehicle.services.VehicleService;
import imt.nord.europe.architecture.tp.common.exceptions.BusinessException;
import imt.nord.europe.architecture.tp.common.enums.VehicleStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur REST pour la gestion des véhicules.
 * Expose les endpoints pour créer, récupérer, modifier et supprimer des véhicules.
 */
@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {
    
    private final VehicleService vehicleService;
    private final VehicleDtoMapper vehicleDtoMapper;
    
    /**
     * Récupère les véhicules avec filtrage optionnel.
     * Les filtres status et brand peuvent être combinés et s'appliquent à la base de données.
     * Si aucun filtre n'est fourni, retourne tous les véhicules.
     * 
     * @param status optionnel - filtrer par statut (AVAILABLE, RENTED, BROKEN_DOWN)
     * @param brand optionnel - filtrer par marque
     * @return la liste des véhicules correspondants aux critères
     */
    @GetMapping
    public ResponseEntity<List<VehicleResponseDto>> getVehicles(
            @RequestParam(required = false) VehicleStatus status,
            @RequestParam(required = false) String brand) {
        
        List<VehicleResponseDto> vehicles = vehicleService.getVehiclesByStatusAndBrand(status, brand)
            .stream()
            .map(vehicleDtoMapper::toResponseDto)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(vehicles);
    }
    
    /**
     * Récupère un véhicule par son ID.
     * 
     * @param id l'ID du véhicule
     * @return le véhicule correspondant
     */
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDto> getVehicleById(@PathVariable Long id) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(vehicleDtoMapper.toResponseDto(vehicle));
    }
    
    /**
     * Crée un nouveau véhicule.
     * 
     * @param requestDto les données du véhicule à créer
     * @return le véhicule créé avec son ID
     */
    @PostMapping
    public ResponseEntity<VehicleResponseDto> createVehicle(@RequestBody VehicleRequestDto requestDto) {
        Vehicle vehicle = vehicleService.createVehicle(
            requestDto.getRegistrationPlate(),
            requestDto.getBrand(),
            requestDto.getModel(),
            requestDto.getMotorization(),
            requestDto.getColor(),
            requestDto.getAcquisitionDate()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleDtoMapper.toResponseDto(vehicle));
    }
    
    /**
     * Modifie un véhicule existant.
     * 
     * @param id l'ID du véhicule à modifier
     * @param requestDto les nouvelles données du véhicule
     * @return le véhicule modifié
     */
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponseDto> updateVehicle(@PathVariable Long id, @RequestBody VehicleRequestDto requestDto) {
        Vehicle vehicle = vehicleService.updateVehicle(
            id,
            requestDto.getRegistrationPlate(),
            requestDto.getBrand(),
            requestDto.getModel(),
            requestDto.getMotorization(),
            requestDto.getColor(),
            requestDto.getAcquisitionDate()
        );
        
        return ResponseEntity.ok(vehicleDtoMapper.toResponseDto(vehicle));
    }
    
    /**
     * Marque un véhicule comme en panne.
     * 
     * @param id l'ID du véhicule
     * @return le véhicule marqué comme en panne
     */
    @PostMapping("/{id}/breakdown")
    public ResponseEntity<VehicleResponseDto> markVehicleAsBrokenDown(@PathVariable Long id) {
        Vehicle vehicle = vehicleService.markAsBrokenDown(id);
        return ResponseEntity.ok(vehicleDtoMapper.toResponseDto(vehicle));
    }
    
    /**
     * Marque un véhicule comme réparé.
     * 
     * @param id l'ID du véhicule
     * @return le véhicule marqué comme réparé
     */
    @PostMapping("/{id}/repair")
    public ResponseEntity<VehicleResponseDto> markVehicleAsRepaired(@PathVariable Long id) {
        Vehicle vehicle = vehicleService.markAsAvailable(id);
        return ResponseEntity.ok(vehicleDtoMapper.toResponseDto(vehicle));
    }
    
    /**
     * Supprime un véhicule.
     * 
     * @param id l'ID du véhicule à supprimer
     * @return une réponse 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
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
