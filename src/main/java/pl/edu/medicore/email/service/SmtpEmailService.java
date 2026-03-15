package pl.edu.medicore.email.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import pl.edu.medicore.email.model.EmailType;

import java.io.File;
import java.util.Map;

@Service
@RequiredArgsConstructor
class SmtpEmailService implements EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    @Async
    public <T> void sendEmail(String to, EmailType emailType, T dto) {
        Context context = new Context();
        context.setVariables(convertDtoToMap(dto));
        String htmlContent = templateEngine.process(emailType.getTemplate(), context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(emailType.getSubject());
            helper.setText(htmlContent, true);
            ClassPathResource logo = new ClassPathResource("static/logo.png");
            helper.addInline("logoImage", logo);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email");
        }
    }

    private <T> Map<String, Object> convertDtoToMap(T dto) {
        return new ObjectMapper().convertValue(dto, new TypeReference<>() {
        });
    }
}
