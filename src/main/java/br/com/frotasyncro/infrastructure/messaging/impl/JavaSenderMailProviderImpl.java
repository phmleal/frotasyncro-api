package br.com.frotasyncro.infrastructure.messaging.impl;

import br.com.frotasyncro.core.configuration.AppConfiguration;
import br.com.frotasyncro.infrastructure.messaging.EmailSenderProvider;
import br.com.frotasyncro.infrastructure.messaging.model.EmailDTO;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

import static java.lang.Boolean.TRUE;

@Slf4j
@Component
public record JavaSenderMailProviderImpl(JavaMailSender javaMailSender,
                                         SpringTemplateEngine springTemplateEngine,
                                         AppConfiguration appConfiguration) implements EmailSenderProvider {

    private static final String UTF_8 = "UTF-8";

    @Override
    @SneakyThrows
    @Async
    public void sendEmail(EmailDTO emailDTO) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, UTF_8);

        String message = buildMessageByStaticTemplate(emailDTO.template(), emailDTO.data());

        helper.setFrom(appConfiguration().getApiMailFrom());
        helper.setSubject(emailDTO.subject());
        helper.setText(message, TRUE);
        helper.setTo(emailDTO.to());

        log.info("Sending mail to {}", emailDTO.to());

        javaMailSender.send(mimeMessage);
    }

    private String buildMessageByStaticTemplate(String template, Map<String, Object> data) {
        Context context = new Context();

        if (data != null && !data.isEmpty()) {
            context.setVariables(data);
        }

        return springTemplateEngine.process(template, context);
    }

}
