package nz.ac.canterbury.seng302.portfolio.utility;

import com.google.protobuf.Timestamp;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;

public class Utility {
    public Utility() {}

    /**
     * Formats the Timestamp given into a date, formatted: "dd MMMM yyyy"
     * @param dateAdded Timestamp of when user was added to the system
     * @return String formatted date
     */
    public static String getDateAddedString(Timestamp dateAdded) {
        if (dateAdded != null) {
            Date date = new Date(dateAdded.getSeconds() * 1000);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
            return dateFormat.format(date);
        } else {
            return null;
        }
    }

    /**
     * Takes the date a user was added to the system and returns a string that is formatted to show how many years
     * and months it has been since they joined.
     * @param dateAdded Date of the user being added to the system
     * @return String formatted of how long the user has been in the system, formatted: "({X} Years, {Y} months)"
     */
    public static String getDateSinceAddedString(Timestamp dateAdded) {
        StringBuilder timeSinceAddedSB = new StringBuilder();
        timeSinceAddedSB.append("(");

        int yearsSince = getYearsSinceAdded(dateAdded);
        if (yearsSince > 0) {
            timeSinceAddedSB.append(yearsSince);
            timeSinceAddedSB.append(" Year");
            if (yearsSince == 1) {
                timeSinceAddedSB.append(", ");
            } else {
                timeSinceAddedSB.append("s, ");
            }
        }
        int monthsSince = getMonthsSinceAdded(dateAdded);
        timeSinceAddedSB.append(monthsSince % 12);
        timeSinceAddedSB.append(" Month");
        if (monthsSince % 12 == 1) {
            timeSinceAddedSB.append(")");
        } else {
            timeSinceAddedSB.append("s)");
        }

        return timeSinceAddedSB.toString();
    }

    /**
     * Gets months since the timestamp given rounded down.
     * @param dateAdded Timestamp of when a user has been added
     * @return Months since timestamp
     */
    private static int getMonthsSinceAdded(Timestamp dateAdded) {
        if (dateAdded != null) {
            Period difference = Period.between(
                    LocalDate.ofEpochDay((dateAdded.getSeconds() / 86400)),
                    LocalDate.now()
            );
            return difference.getMonths();
        } else {
            return 0;
        }
    }

    /**
     * Gets years since the timestamp given rounded down.
     * @param dateAdded Timestamp of when a user has been added
     * @return Years since timestamp
     */
    private static int getYearsSinceAdded(Timestamp dateAdded) {
        if (dateAdded != null) {
            Period difference = Period.between(
                    LocalDate.ofEpochDay((dateAdded.getSeconds() / 86400)),
                    LocalDate.now()
            );
            return difference.getYears();
        } else {
            return 0;
        }
    }

    private static Date stringToDateTime(String dateTimeString) {
        Date date = null;
        try {
            date = new SimpleDateFormat("dd/MMM/yyyy h:mm a").parse(dateTimeString);
        } catch (Exception e) {
            System.err.println("Error parsing date: " + e.getMessage());
        }
        return date;
    }
}
