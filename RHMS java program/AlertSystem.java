package alertsystem;

import chatserver.ChatServer;
import clientside.Client;
import emergencyalertsystem.CustomDate;
import emailnotifier.EmailNotification;
import emergencyalertsystem.EmergencyAlert;
import notifier.Notifiable;
import notifier.NotificationService;
import panicbuttonsystem.PanicButton;
import reminderservice.Reminder;
import smsnotifier.SmsNotification;
import videoconsultation.VideoCall;
import exceptionhandler.RpmsException;
import java.util.logging.Logger;

/**
 * Runs the healthcare alert and communication system.
 */
public class AlertSystem {
    private static final Logger LOGGER = Logger.getLogger(AlertSystem.class.getName());

    /**
     * Starts the system, managing alerts, reminders, chats, and video calls.
     */
    public static void main(String[] args) {
        final String hospitalEmergencyEmail = System.getenv("HOSPITAL_EMERGENCY_EMAIL");
        final String hospitalEmergencyNumber = System.getenv("HOSPITAL_EMERGENCY_NUMBER");

        try {
            // Set up notifications
            Notifiable emailNotification = new EmailNotification(
                System.getenv("EMAIL_USERNAME"),
                System.getenv("EMAIL_PASSWORD"),
                "smtp.gmail.com",
                "587"
            );
            Notifiable smsNotification = new SmsNotification();
            NotificationService notificationService = new NotificationService(emailNotification, smsNotification);

            // Emergency alerts
            EmergencyAlert emergencyAlert = new EmergencyAlert(
                notificationService, hospitalEmergencyEmail, hospitalEmergencyNumber
            );
            emergencyAlert.checkVitals(80, 130, 98, 37);
            LOGGER.info("Vital signs checked");

            // Panic button
            PanicButton panicButton = new PanicButton(
                notificationService, hospitalEmergencyEmail, hospitalEmergencyNumber
            );
            if (panicButton.press()) {
                LOGGER.info("Panic alert sent");
            } else {
                LOGGER.warning("Panic alert failed");
            }

            // Appointment reminder
            CustomDate dueDate = new CustomDate(2025, 4, 28, 10);
            Reminder appointment = new Reminder(
                hospitalEmergencyEmail,
                hospitalEmergencyNumber,
                "You have an appointment with Dr. John at 10:00 AM",
                dueDate,
                notificationService
            );
            if (appointment.sendReminder()) {
                LOGGER.info("Reminder sent");
            } else {
                LOGGER.warning("Reminder failed");
            }

            // Chat session
            ChatServer server = new ChatServer();
            Client doctor = new Client(server, "Doctor");
            Client patient = new Client(server, "Patient");
            server.startSession();
            doctor.sendMessage("How are you, Mr. Habib?");
            patient.sendMessage("Fine, I'm feeling well today.");
            doctor.sendMessage("That's great. Your health is improving!");
            server.endSession();
            doctor.displayChat(false); // Show all messages
            LOGGER.info("Chat session completed");

            // Video consultations
            VideoCall googleMeet = new VideoCall(
                "GoogleMeet", notificationService, hospitalEmergencyEmail, hospitalEmergencyNumber
            );
            String meetLink = googleMeet.startConsultation();
            LOGGER.info("Google Meet started: " + meetLink);
            googleMeet.endConsultation();

            VideoCall zoom = new VideoCall(
                "Zoom", notificationService, hospitalEmergencyEmail, hospitalEmergencyNumber
            );
            String zoomLink = zoom.startConsultation();
            LOGGER.info("Zoom started: " + zoomLink);
            zoom.endConsultation();

        } catch (RpmsException e) {
            e.log(LOGGER);
        }
    }
}