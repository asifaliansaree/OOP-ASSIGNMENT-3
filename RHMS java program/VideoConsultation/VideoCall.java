package videoconsultation;

import exceptionhandler.RpmsException;
import notifier.NotificationService;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Manages video consultations with notifications.
 */
public class VideoCall {
    private static final Logger LOGGER = Logger.getLogger(VideoCall.class.getName());
    private final String platform;
    private final NotificationService notifier;
    private final String recipientEmail;
    private final String recipientPhone;
    private String currentMeetingLink;

    /**
     * Sets up a video call with notifications.
     * @param platform Video platform (Zoom or Google Meet).
     * @param notifier Notification service.
     * @param recipientEmail Email for meeting links.
     * @param recipientPhone Phone for meeting links.
     * @throws RpmsException If inputs are invalid.
     */
    public VideoCall(String platform, NotificationService notifier, String recipientEmail, String recipientPhone)
            throws RpmsException {
        if (platform == null || platform.trim().isEmpty()) {
            throw new RpmsException("INVALID_INPUT", "Platform can't be empty");
        }
        String trimmedPlatform = platform.trim();
        if (!trimmedPlatform.equalsIgnoreCase("GoogleMeet") && !trimmedPlatform.equalsIgnoreCase("Zoom")) {
            throw new RpmsException("INVALID_PLATFORM", "Unsupported platform: " + trimmedPlatform);
        }
        if (notifier == null) {
            throw new RpmsException("INVALID_INPUT", "Notifier can't be null");
        }
        if (recipientEmail == null || recipientEmail.trim().isEmpty() || !recipientEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RpmsException("INVALID_INPUT", "Invalid recipient email");
        }
        if (recipientPhone == null || recipientPhone.trim().isEmpty() || !recipientPhone.matches("\\+?[1-9]\\d{1,14}")) {
            throw new RpmsException("INVALID_INPUT", "Invalid recipient phone");
        }
        this.platform = trimmedPlatform;
        this.notifier = notifier;
        this.recipientEmail = recipientEmail;
        this.recipientPhone = recipientPhone;
    }

    /**
     * Starts a new consultation and sends links.
     * @return Meeting link.
     * @throws RpmsException If starting or notifying fails.
     */
    public String startConsultation() throws RpmsException {
        if (currentMeetingLink != null) {
            endConsultation();
        }
        String meetingId = UUID.randomUUID().toString().substring(0, 8);
        currentMeetingLink = generatePlatformLink(meetingId);
        LOGGER.info("Started " + platform + " consultation: " + currentMeetingLink);

        String message = "Join your " + platform + " consultation: " + currentMeetingLink;
        try {
            notifier.sendEmailAlert(recipientEmail, "Video Consultation", message);
            notifier.sendSMSAlert(recipientPhone, "Video Consultation", message);
        } catch (RpmsException e) {
            e.log(LOGGER);
            throw new RpmsException("NOTIFICATION_FAILED", "Failed to send meeting link", e);
        }

        return currentMeetingLink;
    }

    /**
     * Generates a platform-specific meeting link.
     * @param meetingId Unique meeting ID.
     * @return Meeting link.
     */
    private String generatePlatformLink(String meetingId) {
        return platform.equalsIgnoreCase("GoogleMeet")
                ? "http://meet.google.com/" + meetingId
                : "http://zoom.us/j/" + meetingId;
    }

    /**
     * Ends the active consultation.
     * @throws RpmsException If no active consultation.
     */
    public void endConsultation() throws RpmsException {
        if (currentMeetingLink == null) {
            throw new RpmsException("NO_ACTIVE_CONSULTATION", "No active consultation");
        }
        LOGGER.info("Ended " + platform + " consultation: " + currentMeetingLink);
        currentMeetingLink = null;
    }
}