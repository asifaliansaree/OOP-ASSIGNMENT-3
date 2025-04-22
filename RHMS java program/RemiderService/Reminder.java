package reminderservice;

import emergencyalertsystem.CustomDate;
import exceptionhandler.RpmsException;
import notifier.NotificationService;
import java.util.logging.Logger;

/**
 * Sends reminders via email and SMS.
 */
public class Reminder {
    private static final Logger LOGGER = Logger.getLogger(Reminder.class.getName());
    private final NotificationService notifier;
    private final String reminderMessage;
    private final String receiverEmail;
    private final String receiverPhone;
    private final CustomDate dueDate;

    /**
     * Sets up a reminder with recipient, message, and due date.
     * @param receiverEmail Recipient's email.
     * @param receiverPhone Recipient's phone.
     * @param reminderMessage Reminder message.
     * @param dueDate Due date for reminder.
     * @param notifier Notification service.
     * @throws RpmsException If inputs are invalid.
     */
    public Reminder(String receiverEmail, String receiverPhone, String reminderMessage,
                    CustomDate dueDate, NotificationService notifier) throws RpmsException {
        if (notifier == null) {
            throw new RpmsException("INVALID_INPUT", "Notifier can't be null");
        }
        if (receiverEmail == null || receiverEmail.trim().isEmpty() || !receiverEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RpmsException("INVALID_INPUT", "Invalid recipient email");
        }
        if (receiverPhone == null || receiverPhone.trim().isEmpty() || !receiverPhone.matches("\\+?[1-9]\\d{1,14}")) {
            throw new RpmsException("INVALID_INPUT", "Invalid recipient phone");
        }
        if (reminderMessage == null || reminderMessage.trim().isEmpty()) {
            throw new RpmsException("INVALID_INPUT", "Reminder message can't be empty");
        }
        if (dueDate == null) {
            throw new RpmsException("INVALID_INPUT", "Due date can't be null");
        }
        this.notifier = notifier;
        this.reminderMessage = reminderMessage;
        this.receiverEmail = receiverEmail;
        this.receiverPhone = receiverPhone;
        this.dueDate = dueDate;
    }

    /**
     * Sends the reminder via email and SMS.
     * @return True if both sent, false if either failed.
     */
    public boolean sendReminder() {
        String message = String.format("REMINDER: %s | Due Date: %s", reminderMessage, dueDate);
        boolean success = true;

        try {
            notifier.sendEmailAlert(receiverEmail, "Reminder", message);
            LOGGER.info("Email reminder sent to " + receiverEmail);
        } catch (RpmsException e) {
            e.log(LOGGER);
            success = false;
        }

        try {
            notifier.sendSMSAlert(receiverPhone, "Reminder", message);
            LOGGER.info("SMS reminder sent to " + receiverPhone);
        } catch (RpmsException e) {
            e.log(LOGGER);
            success = false;
        }

        return success;
    }
}