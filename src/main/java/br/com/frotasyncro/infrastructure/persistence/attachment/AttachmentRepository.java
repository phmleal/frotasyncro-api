package br.com.frotasyncro.infrastructure.persistence.attachment;

import br.com.frotasyncro.infrastructure.persistence.attachment.entities.AttachmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AttachmentRepository extends JpaRepository<AttachmentEntity, UUID>, JpaSpecificationExecutor<AttachmentEntity> {
}
