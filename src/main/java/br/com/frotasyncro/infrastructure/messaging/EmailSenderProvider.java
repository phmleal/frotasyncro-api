package br.com.frotasyncro.infrastructure.messaging;

import br.com.frotasyncro.infrastructure.messaging.model.EmailDTO;

public interface EmailSenderProvider {

    void sendEmail(EmailDTO emailDTO);

}
