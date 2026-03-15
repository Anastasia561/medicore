package pl.edu.medicore.email.service;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import pl.edu.medicore.email.dto.VerificationEmailDto;
import pl.edu.medicore.email.model.EmailType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SmtpEmailServiceTest {
    @Mock
    private JavaMailSender mailSender;
    @Mock
    private SpringTemplateEngine templateEngine;
    @InjectMocks
    private SmtpEmailService smtpEmailService;

    @Test
    void shouldSendEmailSuccessfully() {
        String to = "test@example.com";
        VerificationEmailDto dto = new VerificationEmailDto("Test", "Test", "url");
        EmailType type = EmailType.EMAIL_VERIFICATION;

        MimeMessage mimeMessage = new MimeMessage((Session) null);

        Mockito.when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        Mockito.when(templateEngine.process(Mockito.anyString(), Mockito.any(Context.class)))
                .thenReturn("<html>Email</html>");

        smtpEmailService.sendEmail(to, type, dto);

        Mockito.verify(mailSender).createMimeMessage();
        Mockito.verify(templateEngine).process(Mockito.eq("email/email-verification"), Mockito.any(Context.class));
        Mockito.verify(mailSender).send(Mockito.any(MimeMessage.class));
    }

    @Test
    void shouldThrowException_whenMessagingFails() {
        String to = "test@example.com";
        VerificationEmailDto dto = new VerificationEmailDto("Test", "Test", "url");
        EmailType type = EmailType.EMAIL_VERIFICATION;

        Mockito.when(mailSender.createMimeMessage()).thenThrow(new MailSendException("Failed"));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                smtpEmailService.sendEmail(to, type, dto)
        );
        assertEquals("Failed to send email", exception.getMessage());
    }
}
