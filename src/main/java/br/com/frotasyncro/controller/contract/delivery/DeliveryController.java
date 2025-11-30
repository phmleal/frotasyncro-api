package br.com.frotasyncro.controller.contract.delivery;

import br.com.frotasyncro.application.contract.delivery.DeliveryApplicationService;
import br.com.frotasyncro.controller.contract.delivery.model.*;
import br.com.frotasyncro.controller.model.FilteredAndPageableRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/deliveries")
@Tag(name = "Delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryApplicationService deliveryApplicationService;

    @PostMapping
    @Operation(summary = "Create delivery")
    public ResponseEntity<DeliveryResponseDTO> createDelivery(@RequestBody @Valid
                                                              CreateDeliveryRequestDTO createDeliveryRequestDTO) {
        DeliveryResponseDTO deliveryResponseDTO = deliveryApplicationService.createDelivery(createDeliveryRequestDTO);

        return new ResponseEntity<>(deliveryResponseDTO, CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get delivery detail")
    public ResponseEntity<DeliveryResponseDTO> getDeliveryDetail(@PathVariable UUID id) {
        DeliveryResponseDTO deliveryResponseDTO =
                deliveryApplicationService.getDeliveryDetail(id);

        return new ResponseEntity<>(deliveryResponseDTO, OK);
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "Start delivery")
    public ResponseEntity<Void> startDelivery(@PathVariable UUID id,
                                              @RequestBody @Valid StartDeliveryRequestDTO startDeliveryRequestDTO) {
        deliveryApplicationService.startDelivery(id, startDeliveryRequestDTO);

        return new ResponseEntity<>(NO_CONTENT);
    }

    @PostMapping("/{id}/finish")
    @Operation(summary = "Finish delivery")
    public ResponseEntity<Void> finishDelivery(@PathVariable UUID id,
                                               @RequestBody @Valid ClosingDeliveryRequestDTO closingDeliveryRequestDTO) {
        deliveryApplicationService.closingDelivery(id, closingDeliveryRequestDTO);

        return new ResponseEntity<>(NO_CONTENT);
    }

    @PostMapping("/{id}/finalize")
    @Operation(summary = "Finalize delivery")
    public ResponseEntity<Void> finalizeDelivery(@PathVariable UUID id) {
        deliveryApplicationService.finishDelivery(id);

        return new ResponseEntity<>(NO_CONTENT);
    }


    @PostMapping("/list")
    @Operation(summary = "List deliveries")
    public ResponseEntity<Page<SummaryDeliveryResponseDTO>> getDeliveries(@RequestBody @Valid FilteredAndPageableRequestDTO filters) {
        Page<SummaryDeliveryResponseDTO> deliveryResponseDTOS =
                deliveryApplicationService.getDeliveries(filters);

        return new ResponseEntity<>(deliveryResponseDTOS, OK);
    }

}
