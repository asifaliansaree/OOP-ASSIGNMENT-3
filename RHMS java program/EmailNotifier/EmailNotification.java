package emailnotifier;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.logging.Logger;
import notifier.Notifiable;

/**
 * Sends email notifications using the Jakarta Mail API.
 */
public class EmailNotification implements Notifiable {
    private static final Logger LOGGER = Logger.getLogger(EmailNotification.class.getName());
    private final Session mailSession;
    private final String from ;

    /**
     * Constructs an EmailNotification instance with the specified sender credentials and SMTP settings.
     * @param from The sender's email address.
     * @param password The sender's password or app-specific password.
     * @param smtpHost The SMTP server host (e.g., smtp.gmail.com).
     * @param smtpPort The SMTP server port (e.g., 587 for TLS).
     */
    public EmailNotification(String from, String password, String smtpHost, String smtpPort) {
        this.from = from;
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);

        this.mailSession = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });
    }

    /**
     * Sends an email notification to the specified recipient.
     * @param to The recipient's email address.
     * @param subject The subject of the email.
     * @param messageText The body of the email.
     * @throws NotificationException If the email cannot be sent.
     */
    @Override
    public void sendNotification(String to, String subject, String messageText) throws NotificationException {
        if (to == null || to.trim().isEmpty()) {
            throw new NotificationException("Recipient email cannot be null or empty", null);
        }
        if (subject == null || subject.trim().isEmpty()) {
            throw new NotificationException("Subject cannot be null or empty", null);
        }
        if (messageText == null || messageText.trim().isEmpty()) {
            throw new NotificationException("Message text cannot be null or empty", null);
        }

        try {
            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(messageText);

            Transport.send(message);
            LOGGER.info("Email sent to " + to + " successfully!");
        } catch (MessagingException e) {
            LOGGER.severe("Failed to send email to " + to + ": " + e.getMessage());
            throw new NotificationException("Failed to send email", e);
        }
    }
}

/**
 * Custom exception for notification-related errors.
 */
class NotificationException extends RuntimeException {
    public NotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}