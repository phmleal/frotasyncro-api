package br.com.frotasyncro.domain.attachment.mapper;

import br.com.frotasyncro.infrastructure.persistence.attachment.AttachmentRepository;
import br.com.frotasyncro.infrastructure.persistence.attachment.entities.AttachmentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;

    public AttachmentEntity saveAttachment(AttachmentEntity attachmentEntity) {
        return attachmentRepository.save(attachmentEntity);
    }

    public AttachmentEntity findAttachmentById(UUID attachmentId) {
        return attachmentRepository
                .findById(attachmentId).orElseThrow(() -> new RuntimeException("Attachment not found"));
    }

}

