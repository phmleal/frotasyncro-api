package br.com.frotasyncro.infrastructure.persistence.attachment.entities;

import br.com.frotasyncro.domain.contract.enums.AttachmentType;
import br.com.frotasyncro.infrastructure.persistence.generic.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "attachments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class AttachmentEntity extends BaseEntity {

    @Column(nullable = false)
    private String s3Key;

    @Column(nullable = false)
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "attachment_type")
    private AttachmentType type;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDate date;

}
