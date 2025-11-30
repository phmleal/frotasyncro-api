package br.com.frotasyncro.domain.attachment.mapper;

import br.com.frotasyncro.controller.contract.model.CreateContractAttachmentRequestDTO;
import br.com.frotasyncro.controller.contract.model.CreateContractAttachmentResponseDTO;
import br.com.frotasyncro.infrastructure.persistence.attachment.entities.AttachmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AttachmentMapper {

    AttachmentMapper INSTANCE = Mappers.getMapper(AttachmentMapper.class);

    AttachmentEntity toEntity(CreateContractAttachmentRequestDTO createContractAttachmentRequestDTO);

    CreateContractAttachmentResponseDTO toResponse(AttachmentEntity attachmentEntity);

}
