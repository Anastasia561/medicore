package pl.edu.medicore.application.email;

public interface EmailService {
    <T> void sendEmail(String to, EmailType emailType, T dto);
}
