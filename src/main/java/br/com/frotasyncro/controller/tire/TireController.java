package br.com.frotasyncro.controller.tire;

import br.com.frotasyncro.application.tire.TireApplicationService;
import br.com.frotasyncro.controller.model.FilteredAndPageableRequestDTO;
import br.com.frotasyncro.controller.tire.model.*;
import br.com.frotasyncro.domain.report.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/tires")
@RequiredArgsConstructor
@Tag(name = "Tire")
public class TireController {

    private final TireApplicationService tireApplicationService;

    private final ReportService reportService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create tire")
    public ResponseEntity<TireResponseDTO> createTire(@RequestBody @Valid CreateTireRequestDTO createTireRequestDTO) {
        TireResponseDTO tireResponseDTO = tireApplicationService.createTire(createTireRequestDTO);

        return new ResponseEntity<>(tireResponseDTO, CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tire detail")
    public ResponseEntity<TireResponseDTO> getTireDetail(@PathVariable UUID id) {
        TireResponseDTO tireResponseDTO = tireApplicationService.getTireDetail(id);

        return new ResponseEntity<>(tireResponseDTO, OK);
    }

    @PostMapping("/list")
    @Operation(summary = "List tires")
    public ResponseEntity<Page<SummaryTireResponseDTO>> getTires(@RequestBody @Valid FilteredAndPageableRequestDTO filters) {
        Page<SummaryTireResponseDTO> tireResponseDTOS = tireApplicationService.getSummaryTires(filters);

        return new ResponseEntity<>(tireResponseDTOS, OK);
    }

    @PostMapping("/available-for-new-positions")
    @Operation(summary = "List available tires for new positions")
    public ResponseEntity<Page<SummaryTireResponseDTO>> getAvailableTiresForNewPositions(@RequestBody @Valid FilteredAndPageableRequestDTO filters) {
        Page<SummaryTireResponseDTO> tireResponseDTOS = tireApplicationService.getAvailableTiresForNewPositions(filters);

        return new ResponseEntity<>(tireResponseDTOS, OK);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update tire")
    public ResponseEntity<Void> updateTire(@PathVariable UUID id,
                                           @RequestBody @Valid UpdateTireRequestDTO updateTireRequestDTO) {
        tireApplicationService.updateTire(id, updateTireRequestDTO);

        return new ResponseEntity<>(NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete employer")
    public ResponseEntity<Void> deleteTire(@PathVariable UUID id) {
        tireApplicationService.deleteTire(id);

        return new ResponseEntity<>(NO_CONTENT);
    }

    @PostMapping("/tires-positions")
    @Operation(summary = "Add tire position to machine")
    public ResponseEntity<TirePositionResponseDTO> addTirePositionToMachine(@RequestBody @Valid CreateTirePositionRequestDTO createTirePositionRequestDTO) {
        TirePositionResponseDTO tirePositionResponseDTO = tireApplicationService.addTirePositionToMachine(createTirePositionRequestDTO);

        return new ResponseEntity<>(tirePositionResponseDTO, OK);
    }

    @GetMapping("/tires-positions/machines/{machineId}")
    @Operation(summary = "Get tires positions in use by machine")
    public ResponseEntity<TirePositionByMachineResponseDTO> getTiresPositionsInUseByMachine(@PathVariable UUID machineId) {
        TirePositionByMachineResponseDTO tirePositionByMachineResponseDTO = tireApplicationService.getTiresPositionsInUseByMachine(machineId);

        return new ResponseEntity<>(tirePositionByMachineResponseDTO, OK);
    }

    @PatchMapping("/tires-positions/{tirePositionId}/inactivate")
    @Operation(summary = "Inactivate tire position")
    public ResponseEntity<Void> inactivateTirePosition(@PathVariable UUID tirePositionId,
                                                       @RequestBody @Valid InactivateTirePositionRequestDTO inactivateTirePositionRequestDTO) {
        tireApplicationService.inactivateTirePosition(tirePositionId, inactivateTirePositionRequestDTO);

        return new ResponseEntity<>(NO_CONTENT);
    }

    @PostMapping("/reports")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Tires Report")
    public ResponseEntity<Resource> generateTiresReport(@RequestBody FilteredAndPageableRequestDTO filteredAndPageableRequestDTO) {
        byte[] fileBytes = tireApplicationService.generateTiresReport(filteredAndPageableRequestDTO);

        return ResponseEntity.accepted()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pneus.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(fileBytes.length)
                .body(new ByteArrayResource(fileBytes));
    }

    @PostMapping("/{tireId}/history")
    @Operation(summary = "Add event to tire history")
    public ResponseEntity<Void> addEventToTireHistory(@PathVariable UUID tireId, @RequestBody @Valid CreateTireHistoryRequestDTO createTireHistoryRequestDTO) {
        tireApplicationService.addEventToTireHistory(tireId, createTireHistoryRequestDTO);

        return new ResponseEntity<>(NO_CONTENT);
    }
}
