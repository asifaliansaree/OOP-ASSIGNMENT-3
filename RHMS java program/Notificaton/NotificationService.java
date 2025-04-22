package notifier;

import exceptionhandler.RpmsException;
import java.util.logging.Logger;

/**
 * Sends email and SMS alerts.
 */
public class NotificationService {
    private static final Logger LOGGER = Logger.getLogger(NotificationService.class.getName());
    private final Notifiable emailNotifier;
    private final Notifiable smsNotifier;

    /**
     * Sets up email and SMS notifiers.
     * @param emailNotifier Email notification service.
     * @param smsNotifier SMS notification service.
     * @throws RpmsException If notifiers are null.
     */
    public NotificationService(Notifiable emailNotifier, Notifiable smsNotifier) throws RpmsException {
        if (emailNotifier == null) {
            throw new RpmsException("INVALID_INPUT", "Email notifier can't be null");
        }
        if (smsNotifier == null) {
            throw new RpmsException("INVALID_INPUT", "SMS notifier can't be null");
        }
        this.emailNotifier = emailNotifier;
        this.smsNotifier = smsNotifier;
    }

    /**
     * Sends an email alert.
     * @param email Recipient's email.
     * @param subject Alert subject.
     * @param message Alert message.
     * @throws RpmsException If sending fails.
     */
    public void sendEmailAlert(String email, String subject, String message) throws RpmsException {
        if (email == null || email.trim().isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RpmsException("INVALID_INPUT", "Invalid email address");
        }
        try {
            emailNotifier.sendNotification(email, subject, message);
        } catch (RpmsException e) {
            e.log(LOGGER);
            throw e;
        }
    }

    /**
     * Sends an SMS alert.
     * @param phoneNumber Recipient's phone number.
     * @param subject Alert subject.
     * @param message Alert message.
     * @throws RpmsException If sending fails.
     */
    public void sendSMSAlert(String phoneNumber, String subject, String message) throws RpmsException {
        if (phoneNumber == null || phoneNumber.trim().isEmpty() || !phoneNumber.matches("\\+?[1-9]\\d{1,14}")) {
            throw new RpmsException("INVALID_INPUT", "Invalid phone number");
        }
        try {
            smsNotifier.sendNotification(phoneNumber, subject, message);
        } catch (RpmsException e) {
            e.log(LOGGER);
            throw e;
        }
    }
}