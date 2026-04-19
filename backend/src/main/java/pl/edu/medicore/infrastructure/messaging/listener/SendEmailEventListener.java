package pl.edu.medicore.infrastructure.messaging.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.edu.medicore.infrastructure.messaging.event.SendEmailEvent;
import pl.edu.medicore.email.service.EmailService;

@Async
@Component
@RequiredArgsConstructor
public class SendEmailEventListener {

    private final EmailService emailService;

    @Async
    @EventListener
    public void handleSendEmailEvent(SendEmailEvent<?> event) {
        emailService.sendEmail(event.to(), event.emailType(), event.dto());
    }
}
