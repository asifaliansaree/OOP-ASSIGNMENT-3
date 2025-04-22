package smsnotifier;

import exceptionhandler.RpmsException;
import notifier.Notifiable;
import java.util.logging.Logger;

/**
 * Sends SMS notifications.
 */
public class SmsNotification implements Notifiable {
    private static final Logger LOGGER = Logger.getLogger(SmsNotification.class.getName());

    /**
     * Sends an SMS notification.
     * @param to Phone number.
     * @param subject Notification subject.
     * @param message Message content.
     * @throws RpmsException If sending fails.
     */
    @Override
    public void sendNotification(String to, String subject, String message) throws RpmsException {
        if (to == null || to.trim().isEmpty()) {
            throw new RpmsException("INVALID_INPUT", "Phone number can't be empty");
        }
        if (!to.matches("\\+?[1-9]\\d{1,14}")) {
            throw new RpmsException("INVALID_INPUT", "Invalid phone number format");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new RpmsException("INVALID_INPUT", "Message can't be empty");
        }

        // TODO: Replace with real SMS service (e.g., Twilio)
        System.out.println("SMS to " + to + ": " + message);
        LOGGER.info("SMS sent to " + to);
    }
}