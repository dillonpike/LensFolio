package nz.ac.canterbury.seng302.portfolio;

import nz.ac.canterbury.seng302.portfolio.model.Project;

import java.util.Calendar;
import java.util.Date;

/**
 * Includes helper methods related to testing dates.
 */
public class DateTestHelper {

    /**
     * Adds date to a calendar and adds the given amount of time from the given calendar field.
     * @param date string of the date to be added to
     * @param field the calendar field
     * @param amount amount of date or time to be added to the field
     * @return updated date as a string
     */
    public static Date addToDate(Date date, int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, amount);
        return calendar.getTime();
    }

    /**
     * Adds dateString to a calendar and adds the given amount of time from the given calendar field.
     * Returns the updated date as a string.
     * @param dateString string of the date to be added to
     * @param field the calendar field
     * @param amount amount of date or time to be added to the field
     * @return updated date as a string
     */
    public static String addToDateString(String dateString, int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Project.stringToDate(dateString));
        calendar.add(field, amount);
        return Project.dateToString(calendar.getTime());
    }
}
