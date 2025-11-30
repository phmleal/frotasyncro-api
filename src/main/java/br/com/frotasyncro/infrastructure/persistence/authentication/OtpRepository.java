package br.com.frotasyncro.infrastructure.persistence.authentication;

import br.com.frotasyncro.domain.authentication.enums.OtpStatus;
import br.com.frotasyncro.infrastructure.persistence.authentication.entities.OtpEntity;
import br.com.frotasyncro.infrastructure.persistence.authentication.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, UUID> {

    @Query("SELECT o FROM OtpEntity o WHERE o.user = :user AND o.otpStatus = :otpStatus ORDER BY o.createdAt DESC")
    Optional<OtpEntity> findFirstByUserAndUsedIsFalseOrderByCreatedAtDesc(UserEntity user, OtpStatus otpStatus);
}
