package pl.edu.medicore.email.model;

import lombok.Getter;

@Getter
public enum EmailType {
    PASSWORD_RESET_REQUEST("email/reset-password", "Password reset request"),
    DOCTOR_INVITE("email/doctor-invite", "Doctor Registration Invitation"),
    APPOINTMENT_CANCELLATION("email/appointment-cancellation", "Appointment Cancellation"),
    APPOINTMENT_SCHEDULED("email/appointment-scheduled", "Appointment Scheduled"),
    SCHEDULE_UPDATE("email/schedule-update", "Schedule Update"),
    UPCOMING_REMINDER("email/upcoming-reminder", "Upcoming Appointment Reminder"),
    REGISTRATION_CONFIRMATION("email/registration-confirmation", "Registration Confirmation"),
    PASSWORD_RESET_CONFIRM("email/password-reset-confirmation", "Password Reset Confirmation"),
    EMAIL_VERIFICATION("email/email-verification", "Email Verification");

    private final String template;
    private final String subject;

    EmailType(String template, String subject) {
        this.template = template;
        this.subject = subject;
    }
}
