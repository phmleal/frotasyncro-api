package br.com.frotasyncro.controller.machine;

import br.com.frotasyncro.application.machine.TruckTrailerCombinationApplicationService;
import br.com.frotasyncro.controller.machine.model.*;
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
@RequestMapping("/trucks-trailers-combinations")
@RequiredArgsConstructor
@Tag(name = "Truck Trailer Combination")
public class TruckTrailerCombinationController {

    private final TruckTrailerCombinationApplicationService truckTrailerCombinationApplicationService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create truck-trailer combination")
    public ResponseEntity<TruckTrailerCombinationResponseDTO> createTruckTrailerCombination(@RequestBody @Valid
                                                                                            CreateTruckTrailerCombinationRequestDTO createTruckTrailerCombinationRequestDTO) {
        TruckTrailerCombinationResponseDTO truckTrailerCombinationResponseDTO = truckTrailerCombinationApplicationService.createTruckTrailerCombination(createTruckTrailerCombinationRequestDTO);

        return new ResponseEntity<>(truckTrailerCombinationResponseDTO, CREATED);
    }

    @GetMapping("/{truckTrailerCombinationId}")
    @Operation(summary = "Get trailer detail")
    public ResponseEntity<TruckTrailerCombinationResponseDTO> getTruckTrailerCombinationDetail(@PathVariable UUID truckTrailerCombinationId) {
        TruckTrailerCombinationResponseDTO truckTrailerCombinationResponseDTO = truckTrailerCombinationApplicationService.getTruckTrailerCombinationDetail(truckTrailerCombinationId);

        return new ResponseEntity<>(truckTrailerCombinationResponseDTO, OK);
    }

    @PostMapping("/list")
    @Operation(summary = "List trailers")
    public ResponseEntity<Page<TruckTrailerCombinationResponseDTO>> getTruckTrailersCombinations(@RequestBody @Valid FilteredAndPageableRequestDTO filters) {
        Page<TruckTrailerCombinationResponseDTO> truckTrailerCombinationResponseDTOS = truckTrailerCombinationApplicationService.getTruckTrailersCombinations(filters);

        return new ResponseEntity<>(truckTrailerCombinationResponseDTOS, OK);
    }

    @PatchMapping("/{truckTrailerCombinationId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update truck-trailer combination")
    public ResponseEntity<Void> updateTruckTrailerCombination(@PathVariable UUID truckTrailerCombinationId,
                                                              @RequestBody @Valid UpdateTruckTrailerCombinationRequestDTO updateTruckTrailerCombinationRequestDTO) {
        truckTrailerCombinationApplicationService.updateTruckTrailerCombination(truckTrailerCombinationId, updateTruckTrailerCombinationRequestDTO);

        return new ResponseEntity<>(NO_CONTENT);
    }

    @PostMapping("/{truckTrailerCombinationId}/finish")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Finish truck-trailer combination")
    public ResponseEntity<Page<TrailerResponseDTO>> finishTruckTrailerCombination(@PathVariable UUID truckTrailerCombinationId, @RequestBody @Valid FinishTruckTrailerCombinationRequestDTO finishTruckTrailerCombinationRequestDTO) {
        truckTrailerCombinationApplicationService.finishTruckTrailerCombination(truckTrailerCombinationId, finishTruckTrailerCombinationRequestDTO);

        return new ResponseEntity<>(NO_CONTENT);
    }

    @GetMapping("/trucks/available")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get trucks available to couple")
    public ResponseEntity<List<SummaryMachineResponseDTO>> getAvailableTrucks() {
        List<SummaryMachineResponseDTO> summaryEmployerResponseDTOList =
                truckTrailerCombinationApplicationService.getAvailableTrucks();

        return new ResponseEntity<>(summaryEmployerResponseDTOList, OK);
    }

    @GetMapping("/trailers/available")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get trailers available to couple")
    public ResponseEntity<List<SummaryMachineResponseDTO>> getAvailableTrailers() {
        List<SummaryMachineResponseDTO> summaryEmployerResponseDTOList =
                truckTrailerCombinationApplicationService.getAvailableTrailers();

        return new ResponseEntity<>(summaryEmployerResponseDTOList, OK);
    }

}
