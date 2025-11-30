package br.com.frotasyncro.controller.machine;

import br.com.frotasyncro.application.machine.TruckApplicationService;
import br.com.frotasyncro.controller.machine.model.CreateTruckRequestDTO;
import br.com.frotasyncro.controller.machine.model.SummaryMachineResponseDTO;
import br.com.frotasyncro.controller.machine.model.TruckResponseDTO;
import br.com.frotasyncro.controller.machine.model.UpdateTruckRequestDTO;
import br.com.frotasyncro.controller.model.FilteredAndPageableRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/trucks")
@RequiredArgsConstructor
@Tag(name = "Truck")
public class TruckController {

    private final TruckApplicationService truckApplicationService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create truck")
    public ResponseEntity<TruckResponseDTO> createTruck(@RequestBody @Valid CreateTruckRequestDTO createTruckRequestDTO) {
        TruckResponseDTO truckResponseDTO = truckApplicationService.createTruck(createTruckRequestDTO);

        return new ResponseEntity<>(truckResponseDTO, CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get truck detail")
    public ResponseEntity<TruckResponseDTO> getTruckDetail(@PathVariable UUID id) {
        TruckResponseDTO truckResponseDTO = truckApplicationService.getTruckDetail(id);

        return new ResponseEntity<>(truckResponseDTO, OK);
    }

    @PostMapping("/list")
    @Operation(summary = "List trucks")
    public ResponseEntity<Page<TruckResponseDTO>> getTrucks(@RequestBody @Valid FilteredAndPageableRequestDTO filters) {
        Page<TruckResponseDTO> truckResponseDTOS = truckApplicationService.getTrucks(filters);

        return new ResponseEntity<>(truckResponseDTOS, OK);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update truck")
    public ResponseEntity<Void> updateTruck(@PathVariable UUID id,
                                            @RequestBody @Valid UpdateTruckRequestDTO updateTruckRequestDTO) {
        truckApplicationService.updateTruck(id, updateTruckRequestDTO);

        return new ResponseEntity<>(NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete truck")
    public ResponseEntity<Void> deleteTruck(@PathVariable UUID id) {
        truckApplicationService.deleteTruck(id);

        return new ResponseEntity<>(NO_CONTENT);
    }

    @GetMapping("/summary/all")
    @Operation(summary = "Get all trucks summary")
    public ResponseEntity<List<SummaryMachineResponseDTO>> getAllTrucksSummary() {
        List<SummaryMachineResponseDTO> summaryList = truckApplicationService.getAllTrucksSummary();

        return new ResponseEntity<>(summaryList, OK);
    }

}
