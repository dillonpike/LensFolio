package nz.ac.canterbury.seng302.portfolio.utility;

import com.google.protobuf.Timestamp;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;

public class Utility {
    public Utility() {}
    public String getDateAddedString(Timestamp dateAdded) {
        if (dateAdded != null) {
            Date date = new Date(dateAdded.getSeconds() * 1000);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
            return dateFormat.format(date);
        } else {
            return null;
        }
    }

    public String getDateSinceAddedString(Timestamp dateAdded) {
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

    private int getMonthsSinceAdded(Timestamp dateAdded) {
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

    private int getYearsSinceAdded(Timestamp dateAdded) {
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
}
