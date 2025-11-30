package br.com.frotasyncro.application.contract;

import br.com.frotasyncro.controller.contract.model.*;
import br.com.frotasyncro.core.configuration.AppConfiguration;
import br.com.frotasyncro.domain.attachment.mapper.AttachmentMapper;
import br.com.frotasyncro.domain.attachment.mapper.AttachmentService;
import br.com.frotasyncro.domain.attachment.service.AttachmentS3Service;
import br.com.frotasyncro.domain.contract.delivery.ContractService;
import br.com.frotasyncro.domain.contract.enums.ExpenseType;
import br.com.frotasyncro.domain.expense.mapper.ExpenseMapper;
import br.com.frotasyncro.domain.expense.mapper.ExpenseService;
import br.com.frotasyncro.infrastructure.persistence.attachment.entities.AttachmentEntity;
import br.com.frotasyncro.infrastructure.persistence.contract.entities.ContractEntity;
import br.com.frotasyncro.infrastructure.persistence.expense.entities.ExpenseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.isNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContractApplicationService {

    private final ContractService contractService;
    private final ExpenseService expenseService;
    private final AttachmentService attachmentService;
    private final AttachmentS3Service attachmentS3Service;
    private final AppConfiguration appConfiguration;

    @Transactional
    public CreateContractExpenseResponseDTO createContractExpense(UUID id,
                                                                  CreateContractExpenseRequestDTO createContractExpenseRequestDTO) {

        if (createContractExpenseRequestDTO.getExpenseType().equals(ExpenseType.FUEL_SUPPLY)
                && isNull(createContractExpenseRequestDTO.getQuantity())) {
            throw new IllegalArgumentException("Quantity must be provided for FUEL_SUPPLY expenses.");
        }

        ContractEntity contractEntity = contractService.findContractById(id);

        ExpenseEntity expense =
                ExpenseMapper.INSTANCE.toEntity(createContractExpenseRequestDTO);

        if (contractEntity.getExpenses() == null) {
            contractEntity.setExpenses(new HashSet<>());
        }

        expenseService.saveExpense(expense);

        contractEntity.getExpenses().add(expense);
        contractService.saveContract(contractEntity);

        log.info("Expense successfully added to contract {}", id);

        return ExpenseMapper.INSTANCE.toResponse(expense);
    }

    @Transactional
    public CreateContractAttachmentResponseDTO createContractAttachment(UUID contractId,
                                                                        CreateContractAttachmentRequestDTO createContractAttachmentRequestDTO,
                                                                        byte[] fileContent,
                                                                        String fileName,
                                                                        String contentType) {
        ContractEntity contractEntity = contractService.findContractById(contractId);

        String s3Key = generateS3Key(contractId, fileName);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("attachment-type", createContractAttachmentRequestDTO.getType().getCode());
        metadata.put("contract-id", contractId.toString());
        metadata.put("original-filename", fileName);

        // Upload para S3 e recebe presigned URL (a S3 key continua sendo a mesma que geramos)
        attachmentS3Service.uploadFileAndGetPresignedUrl(
                fileContent,
                appConfiguration.getApiBucketName(),
                s3Key,
                contentType,
                metadata
        );

        AttachmentEntity attachment = AttachmentEntity.builder()
                .type(createContractAttachmentRequestDTO.getType())
                .description(createContractAttachmentRequestDTO.getDescription())
                .date(createContractAttachmentRequestDTO.getDate())
                .s3Key(s3Key)  // Armazenamos a key para futuras gerações de presigned URL
                .fileName(fileName)
                .build();

        AttachmentEntity savedAttachment = attachmentService.saveAttachment(attachment);

        if (contractEntity.getAttachments() == null) {
            contractEntity.setAttachments(new HashSet<>());
        }

        contractEntity.getAttachments().add(savedAttachment);
        contractService.saveContract(contractEntity);

        log.info("Attachment successfully added to contract {}", contractId);

        return AttachmentMapper.INSTANCE.toResponse(savedAttachment);
    }

    public AttachmentDownloadResponseDTO getAttachmentDownloadUrl(UUID attachmentId) {
        AttachmentEntity attachment = attachmentService.findAttachmentById(attachmentId);

        String presignedUrl = attachmentS3Service.generatePresignedUrl(attachment.getS3Key());

        log.info("Presigned URL generated for attachment {}", attachmentId);

        AttachmentDownloadResponseDTO response = new AttachmentDownloadResponseDTO(presignedUrl);
        response.setId(attachment.getId());
        response.setType(attachment.getType());
        response.setDescription(attachment.getDescription());
        response.setDate(attachment.getDate());
        response.setFileName(attachment.getFileName());
        response.setCreatedAt(attachment.getCreatedAt());

        return response;
    }

    private String generateS3Key(UUID contractId, String fileName) {
        long timestamp = System.currentTimeMillis();
        return String.format("contracts/%s/attachments/%d_%s", contractId, timestamp, fileName);
    }

}
