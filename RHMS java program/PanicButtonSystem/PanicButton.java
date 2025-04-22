package panicbuttonsystem;

import emergencyalertsystem.EmergencyAlert;
import exceptionhandler.RpmsException;
import notifier.NotificationService;
import java.util.logging.Logger;

/**
 * Triggers panic alerts via email and SMS.
 */
public class PanicButton extends EmergencyAlert {
    private static final Logger LOGGER = Logger.getLogger(PanicButton.class.getName());

    /**
     * Sets up panic button with notifier and contacts.
     * @param notifier Notification service.
     * @param emergencyEmail Email for alerts.
     * @param emergencyNumber Phone for alerts.
     * @throws RpmsException If inputs are invalid.
     */
    public PanicButton(NotificationService notifier, String emergencyEmail, String emergencyNumber)
            throws RpmsException {
        super(notifier, emergencyEmail, emergencyNumber);
    }

    /**
     * Sends panic alert.
     * @return True if sent, false if failed.
     * @throws RpmsException If sending fails critically.
     */
    public boolean press() throws RpmsException {
        String message = "PANIC BUTTON ACTIVATED! Immediate assistance required!";
        boolean success = true;

        try {
            getNotifier().sendEmailAlert(getEmergencyEmail(), "Panic Alert", message);
        } catch (RpmsException e) {
            e.log(LOGGER);
            success = false;
        }

        try {
            getNotifier().sendSMSAlert(getEmergencyNumber(), "Panic Alert", message);
        } catch (RpmsException e) {
            e.log(LOGGER);
            success = false;
        }

        if (success) {
            LOGGER.info("Panic alert sent at " + new CustomDate(2025, 4, 22, 9));
        } else {
            LOGGER.warning("Some panic alerts failed");
        }

        return success;
    }
}