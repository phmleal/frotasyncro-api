package br.com.frotasyncro.controller.contract;

import br.com.frotasyncro.application.contract.ContractApplicationService;
import br.com.frotasyncro.controller.contract.model.*;
import br.com.frotasyncro.domain.contract.enums.AttachmentType;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/contracts")
@Tag(name = "Contract")
@RequiredArgsConstructor
public class ContractController {

    private final ContractApplicationService contractApplicationService;

    @PostMapping("/{id}/expenses")
    public ResponseEntity<CreateContractExpenseResponseDTO> createContractExpense(@PathVariable UUID id,
                                                                                  @RequestBody @Valid CreateContractExpenseRequestDTO createContractExpenseRequestDTO) {
        CreateContractExpenseResponseDTO responseDTO = contractApplicationService
                .createContractExpense(id, createContractExpenseRequestDTO);

        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/{id}/attachments")
    public ResponseEntity<CreateContractAttachmentResponseDTO> createContractAttachment(
            @PathVariable UUID id,
            @RequestParam(value = "type") String type,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "file") MultipartFile file) throws IOException {

        CreateContractAttachmentRequestDTO requestDTO = new CreateContractAttachmentRequestDTO();
        requestDTO.setType(AttachmentType.valueOf(type.toUpperCase()));
        requestDTO.setDescription(description);

        if (date != null && !date.isEmpty()) {
            requestDTO.setDate(LocalDate.parse(date));
        }

        byte[] fileContent = file.getBytes();
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();

        CreateContractAttachmentResponseDTO responseDTO = contractApplicationService
                .createContractAttachment(id, requestDTO, fileContent, fileName, contentType);

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/attachments/{attachmentId}/download")
    public ResponseEntity<AttachmentDownloadResponseDTO> getAttachmentDownloadUrl(@PathVariable UUID attachmentId) {
        AttachmentDownloadResponseDTO responseDTO = contractApplicationService
                .getAttachmentDownloadUrl(attachmentId);

        return ResponseEntity.ok(responseDTO);
    }

}
