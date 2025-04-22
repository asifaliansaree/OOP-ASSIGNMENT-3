package emergencyalertsystem;

import exceptionhandler.RpmsException;
import notifier.NotificationService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Monitors vital signs and sends alerts if they're off.
 */
public class EmergencyAlert {
    private static final Logger LOGGER = Logger.getLogger(EmergencyAlert.class.getName());
    private final NotificationService notifier;
    private final List<VitalSignThreshold> thresholds;
    private final String emergencyEmail;
    private final String emergencyNumber;

    /**
     * Sets up alert system with notifier and contacts.
     * @param notifier Notification service.
     * @param emergencyEmail Email for alerts.
     * @param emergencyNumber Phone for alerts.
     * @throws RpmsException If inputs are invalid.
     */
    public EmergencyAlert(NotificationService notifier, String emergencyEmail, String emergencyNumber)
            throws RpmsException {
        if (notifier == null) {
            throw new RpmsException("INVALID_INPUT", "Notifier can't be null");
        }
        if (emergencyEmail == null || emergencyEmail.trim().isEmpty() || !emergencyEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RpmsException("INVALID_INPUT", "Invalid emergency email");
        }
        if (emergencyNumber == null || emergencyNumber.trim().isEmpty() || !emergencyNumber.matches("\\+?[1-9]\\d{1,14}")) {
            throw new RpmsException("INVALID_INPUT", "Invalid emergency phone number");
        }
        this.notifier = notifier;
        this.thresholds = new ArrayList<>();
        this.emergencyEmail = emergencyEmail;
        this.emergencyNumber = emergencyNumber;
        initializeDefaultThreshold();
    }

    /*
     * Getter
     */
    public NotificationService getNotifier() {
        return notifier;
    }
    public String getEmergencyNumber() {
        return emergencyNumber;
    }
    public String getEmergencyEmail() {
        return emergencyEmail;
    }
    public List<VitalSignThreshold> getThreshold() {
        return Collections.unmodifiableList(thresholds);
    }
    public void initializeDefaultThreshold() throws RpmsException {
        try {
            thresholds.add(new VitalSignThreshold(60, 100, "HeartRate"));
            thresholds.add(new VitalSignThreshold(60, 140, "BloodPressure"));
            thresholds.add(new VitalSignThreshold(95, 100, "OxygenLevel"));
            thresholds.add(new VitalSignThreshold(35, 38.5, "Temperature"));
        } catch (RpmsException e) {
            e.log(LOGGER);
            throw e;
        }
    }

    /**
     * Adds a vital sign threshold.
     * @param threshold Threshold to add.
     * @throws RpmsException If invalid or duplicate.
     */
    public void addVitalThreshold(VitalSignThreshold threshold) throws RpmsException {
        if (threshold == null) {
            throw new RpmsException("INVALID_INPUT", "Threshold can't be null");
        }
        if (thresholds.stream().anyMatch(t -> t.getVitalName().equals(threshold.getVitalName()))) {
            throw new RpmsException("DUPLICATE_VITAL", "Vital sign already exists: " + threshold.getVitalName());
        }
        thresholds.add(threshold);
    }

    /**
     * Checks vitals and sends alerts if needed.
     * @param heartRate Heart rate value.
     * @param bloodPressure Blood pressure value.
     * @param oxygenLevel Oxygen level value.
     * @param temperature Temperature value.
     * @throws RpmsException If checking fails.
     */
    public void checkVitals(double heartRate, double bloodPressure, double oxygenLevel, double temperature)
            throws RpmsException {
        Map<String, Double> vitals = new HashMap<>();
        vitals.put("HeartRate", heartRate);
        vitals.put("BloodPressure", bloodPressure);
        vitals.put("OxygenLevel", oxygenLevel);
        vitals.put("Temperature", temperature);

        for (VitalSignThreshold threshold : thresholds) {
            Double value = vitals.get(threshold.getVitalName());
            if (value == null) {
                LOGGER.warning("Unknown vital: " + threshold.getVitalName());
                continue;
            }
            if (threshold.isCritical(value)) {
                triggerAlert(threshold, value);
            }
        }
    }

    /**
     * Sends alert for critical vital sign.
     * @param threshold Violated threshold.
     * @param currentValue Current vital value.
     * @throws RpmsException If alert fails.
     */
    public void triggerAlert(VitalSignThreshold threshold, double currentValue) throws RpmsException {
        // Create alert message
        String alertMessage = String.format(
            "ALERT: Critical %s detected! Value: %.2f (Safe Range: %.2f–%.2f)",
            threshold.getVitalName(), currentValue, threshold.getMin(), threshold.getMax()
        );
        notifier.sendEmailAlert(emergencyEmail, "Emergency Alert", alertMessage);
        notifier.sendSMSAlert(emergencyNumber, "Emergency Alert", alertMessage);
    }
}