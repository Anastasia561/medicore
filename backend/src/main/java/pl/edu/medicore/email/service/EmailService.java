package pl.edu.medicore.email.service;

import pl.edu.medicore.email.model.EmailType;

public interface EmailService {
    <T> void sendEmail(String to, EmailType emailType, T dto);
}
