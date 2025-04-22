package customdate;

import ExceptionHandler.RpmsErrorHandling;

/**
 * Represents a custom date and time with year, month, day, and hour.
 */
public class CustomDate {
    private int year;
    private int month;
    private int day;
    private int hour;

    /**
     * Constructs a CustomDate with the specified year, month, day, and hour.
     * @param year The year (1900–2100).
     * @param month The month (1–12).
     * @param day The day (1–31, depending on the month and year).
     * @param hour The hour (0–23).
     * @throws RpmsErrorHandling If any input is invalid.
     */
    public CustomDate(int year, int month, int day, int hour) throws RpmsErrorHandling {
        setYear(year);
        setMonth(month);
        setDay(day, month, year);
        setHour(hour);
    }
    public int getYear() {
        return year;
    }
    public int getMonth() {
        return month;
    }
    public int getDay() {
        return day;
    }
    public int getHour() {
        return hour;
    }

    /**
     * Sets the year.
     * @param year The year to set.
     * @throws RpmsErrorHandling If the year is not between 1900 and 2100.
     */
    public void setYear(int year) throws RpmsErrorHandling {
        if (year < 1900 || year > 2100) {
            throw new RpmsErrorHandling("Year must be between 1900 and 2100");
        }
        this.year = year;
    }

    /**
     * Sets the month.
     * @param month The month to set.
     * @throws RpmsErrorHandling If the month is not between 1 and 12.
     */
    public void setMonth(int month) throws RpmsErrorHandling {
        if (month < 1 || month > 12) {
            throw new RpmsErrorHandling("Month must be between 1 and 12");
        }
        this.month = month;
    }

    /**
     * Sets the day, validating based on the month and year.
     * @param day The day to set.
     * @param month The month for validation.
     * @param year The year for leap year validation.
     * @throws RpmsErrorHandling If the day is invalid for the given month and year.
     */
    public void setDay(int day, int month, int year) throws RpmsErrorHandling {
        if (day < 1) {
            throw new RpmsErrorHandling("Day must be at least 1");
        }
        int maxDays;
        switch (month) {
            case 4: case 6: case 9: case 11:
                maxDays = 30;
                break;
            case 2:
                maxDays = isLeapYear(year) ? 29 : 28;
                break;
            default:
                maxDays = 31;
        }
        if (day > maxDays) {
            throw new RpmsErrorHandling("Day must be valid for the given month and year (max: " + maxDays + ")");
        }
        this.day = day;
    }

    /**
     * Sets the hour.
     * @param hour The hour to set.
     * @throws RpmsErrorHandling If the hour is not between 0 and 23.
     */
    public void setHour(int hour) throws RpmsErrorHandling {
        if (hour < 0 || hour > 23) {
            throw new RpmsErrorHandling("Hour must be between 0 and 23");
        }
        this.hour = hour;
    }

    /**
     * Checks if the given year is a leap year.
     * @param year The year to check.
     * @return True if the year is a leap year, false otherwise.
     */
    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    /**
     * Returns a string representation of the date in the format "YYYY-MM-DD:HH.AM/PM".
     * @return The formatted date string.
     */
    @Override
    public String toString() {
        return String.format("%d-%02d-%02d:%02d.%s",
            year, month, day,
            (hour % 12 == 0 ? 12 : hour % 12),
            (hour < 12 ? "AM" : "PM"));
    }
}