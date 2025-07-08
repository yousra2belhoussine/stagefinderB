package ma.stagefinder.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void sendVerificationEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Verify Your Email - StageFinder");

            String verificationLink = frontendUrl + "/verify-email?token=" + token;
            String content = "Welcome to StageFinder!\n\n" +
                    "Please click the following link to verify your email:\n" +
                    verificationLink + "\n\n" +
                    "If you didn't create an account, please ignore this email.";

            message.setText(content);
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Reset Your Password - StageFinder");

            String resetLink = frontendUrl + "/reset-password?token=" + token;
            String content = "Hello,\n\n" +
                    "You have requested to reset your password for your StageFinder account.\n\n" +
                    "Please click the following link to reset your password:\n" +
                    resetLink + "\n\n" +
                    "This link will expire in 1 hour.\n\n" +
                    "If you didn't request a password reset, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "StageFinder Team";

            message.setText(content);
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    @Async // Hada howa l'sarout: kaygoul l Spring khdem had la méthode f thread bo7dha
    public void sendNotificationEmail(String toEmail, String subject, String body) {
        try {
            // Log bach n3erfo l'khadma bdat
            System.out.println("Début de l'envoi d'email en arrière-plan à: " + toEmail);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            // Log bach n3erfo l'khadma salat
            System.out.println("Email envoyé avec succès en arrière-plan à: " + toEmail);
        } catch (Exception e) {
            System.err.println("Échec de l'envoi de l'email asynchrone à " + toEmail + ". Erreur: " + e.getMessage());
        }
    }


}
