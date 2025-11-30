package br.com.frotasyncro.domain.authentication;

import br.com.frotasyncro.core.configuration.AppConfiguration;
import br.com.frotasyncro.domain.authentication.enums.OtpStatus;
import br.com.frotasyncro.domain.authentication.exception.OtpErrorException;
import br.com.frotasyncro.domain.authentication.mapper.OtpMapper;
import br.com.frotasyncro.infrastructure.authentication.provider.OtpProvider;
import br.com.frotasyncro.infrastructure.messaging.EmailSenderProvider;
import br.com.frotasyncro.infrastructure.messaging.model.EmailDTO;
import br.com.frotasyncro.infrastructure.persistence.authentication.OtpRepository;
import br.com.frotasyncro.infrastructure.persistence.authentication.entities.OtpEntity;
import br.com.frotasyncro.infrastructure.persistence.authentication.entities.UserEntity;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static br.com.frotasyncro.domain.authentication.enums.OtpStatus.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;

@Service
@Slf4j
@RequiredArgsConstructor
public class OtpService {

    private static final String PASSWORD_RECOVERY_OTP_SUBJECT = "Transportadora TL - Recuperação de senha";
    private static final String CODE = "code";
    private static final String FORGET_PASSWORD_TEMPLATE = "forget-password-email";


    private final OtpRepository otpRepository;
    private final OtpProvider otpProvider;
    private final EmailSenderProvider emailSenderProvider;
    private final AppConfiguration appConfiguration;

    public void createOtp(UserEntity user) {
        Optional<OtpEntity> otpEntityOptional = findActiveByUser(user);

        if (otpEntityOptional.isPresent()) {
            log.error("OTP already exists for user id {}", user.getId());

            throw new EntityExistsException("OTP already exists for user id " + user.getId());
        } else {
            try {
                log.info("Generating OTP for user id {}", user.getId());

                String code = otpProvider.generateOtpCode();

                String salt = otpProvider.generateSalt();

                String hash = otpProvider.hashOtpCode(code, salt);

                generateOtpEntity(user, hash, salt);

                sentOtpEmail(user, code);
            } catch (Exception e) {
                log.error("OTP generation failed for user id {}", user.getId());

                throw new OtpErrorException(
                        String.format("OTP generation failed for user id %s", user.getId())
                );
            }
        }
    }

    public Boolean verifyOtpCode(UserEntity user, String otpCode) {
        Optional<OtpEntity> otpEntityOptional = findActiveByUser(user);

        if (otpEntityOptional.isPresent()) {

            OtpEntity otp = otpEntityOptional.get();

            return checkOtp(user, otpCode, otp);
        } else {
            log.error("No active OTP found for user id {}", user.getId());

            throw new EntityNotFoundException("No active OTP found for user id " + user.getId());
        }
    }

    private Boolean checkOtp(UserEntity user, String otpCode, OtpEntity otp) {
        try {

            checkIfOtpHasExpired(user, otp);

            checkIfOtpHasExceededAttempts(user, otp);

            flagOtpWithNewAttempt(otp);

            Boolean validOtp = otpProvider.verifyOtpCode(otpCode, otp.getHash(), otp.getSalt());

            if (TRUE.equals(validOtp)) {
                log.info("OTP verified successfully for user id {}", user.getId());

                otp.setOtpStatus(CONFIRMED);

                return TRUE;
            } else {
                log.error("Invalid OTP code provided for user id {}", user.getId());

                return FALSE;
            }

        } catch (Exception e) {
            log.error("OTP verification failed for user id {}", user.getId());

            throw new OtpErrorException(
                    e.getMessage()
            );
        } finally {
            save(otp);
        }
    }

    private void flagOtpWithNewAttempt(OtpEntity otp) {
        otp.setAttempts(otp.getAttempts() + 1);
        otp.setLastAttemptAt(now());
    }

    private void checkIfOtpHasExceededAttempts(UserEntity user, OtpEntity otp) {
        if (otp.getAttempts() >= appConfiguration.getOtpMaxAttempts()) {
            log.error("Maximum OTP verification attempts exceeded for user id {}", user.getId());

            otp.setOtpStatus(EXHAUSTED_ATTEMPTS);
            save(otp);

            throw new OtpErrorException(
                    format("Maximum OTP verification attempts exceeded for user id %s", user.getId())
            );
        }
    }

    private void checkIfOtpHasExpired(UserEntity user, OtpEntity otp) {
        if (otp.getExpiresAt().isBefore(now())) {
            log.error("Otp expired for user id {}", user.getId());

            otp.setOtpStatus(EXPIRED);
            save(otp);

            throw new OtpErrorException(
                    format("Otp expired for user id %s", user.getId())
            );
        }
    }

    private void generateOtpEntity(UserEntity user, String hash, String salt) {
        OtpEntity otpEntity = OtpMapper.INSTANCE.createInitialOtpEntity(user, hash, salt);

        save(otpEntity);
    }

    private void sentOtpEmail(UserEntity user, String code) {
        EmailDTO emailDTO = new EmailDTO(
                user.getEmail(),
                PASSWORD_RECOVERY_OTP_SUBJECT,
                FORGET_PASSWORD_TEMPLATE,
                Map.of(CODE, code)
        );

        emailSenderProvider.sendEmail(emailDTO);
    }

    public Optional<OtpEntity> findActiveByUser(UserEntity user) {
        return otpRepository.findFirstByUserAndUsedIsFalseOrderByCreatedAtDesc(user, OtpStatus.PENDING);
    }

    private void save(OtpEntity otpEntity) {
        otpRepository.save(otpEntity);
    }

}
