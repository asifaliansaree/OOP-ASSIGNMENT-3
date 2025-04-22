package notifier;

/**
 * Sends notifications like emails or SMS to keep folks informed.
 */
public interface Notifiable {
    /**
     * Sends a notification to the recipient.
     * @param to Recipient (e.g., email or phone).
     * @param subject Notification subject.
     * @param message Message content.
     * @throws RpmsException If sending fails.
     */
    void sendNotification(String to, String subject, String message) throws RpmsException;
}