package nz.ac.canterbury.seng302.portfolio.utility;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import org.springframework.web.util.HtmlUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * This Dictionary is used for adding events together that have matching types and dates.
 * It also has a method to generate the combined values which are the individual JSON lists of the events.
 */
public class EventDic {
    // Creating a HashTable Dictionary
    // Because we want to separate the types of events we store the JSON objects as the values rather than just the amounts.

    HashMap<EventTypes, String> datesToEvents = new HashMap<>();

    /**
     * Method used to combine the JSON lists together for the events.
     *
     * @return  Combine JSON list.
     */
    public String makeJSON() {
        StringBuilder JSONList = new StringBuilder();
        Collection<String> values = datesToEvents.values();

        // Iterate the values
        for (String val : values) {
            JSONList.append(val);
        }

        return JSONList.toString();
    }

    /**
     * This method adds a deadline to the hashTable the hash is based on the day and type of event-JSON in this case a deadline event-JSON.
     *
     * @param deadline The Deadline object to convert to JSON.
     */
    public void add(Deadline deadline) {
        int amount = 1;

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm");

        // Uses EventTypes costume hashCode() to hash based on type and date.
        String eventData = datesToEvents.get(new EventTypes("Deadline", deadline.getDeadlineDateString()));
        String description = "- " + HtmlUtils.htmlEscape(deadline.getDeadlineName());
        StringBuilder eventObject = new StringBuilder();
        String date = df.format(Date.from(deadline.getDeadlineDate().toInstant().truncatedTo(ChronoUnit.DAYS)));  // To fix error with times at 11pm ranging 2 days rather than 1.
        if (eventData == null) {
            eventObject.append("{title: '").append(amount).append("', start: '").append(date).append("', type: 'Deadline").append("', description: '").append(description).append("'},");
        } else {
            try {
                amount += Integer.parseInt(eventData.split("'")[1]);
                description = eventData.split("'")[7] + "<br>- " + HtmlUtils.htmlEscape(deadline.getDeadlineName());
            } catch (Exception ignore) {
                // Current uses -1 to represent error. May want to change this to a thrown error.
                amount = -1;
            }
            eventObject.append("{title: '").append(amount).append("', start: '").append(date).append("', type: 'Deadline").append("', description: '").append(description).append("'},");
        }
        datesToEvents.put(new EventTypes("Deadline", deadline.getDeadlineDateString()), eventObject.toString());
    }


    /**
     * This method adds an event to the hashTable the hash is based on the day and type of event-JSON in this case an event event-JSON.
     * This method also makes a unique event-JSON for each day the event spans.
     *
     * @param event The Event object to convert to JSON.
     */
    public void add(Event event) { // By removing the while loop system you can have events over multiple days rather than signal days.
        Calendar current = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        current.setTime(event.getEventStartDate());
        end.setTime(event.getEventEndDate());
        // The time is set to the end of the day so that event that overlap on days but not time can still match.
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);

        while (current.compareTo(end) <= 0) {
            int amount = 1;
            Instant date = current.getTime().toInstant();
            // Uses EventTypes costume hashCode() to hash based on type and date.
            String eventData = datesToEvents.get(new EventTypes("Event", Project.dateToString(Date.from(date))));
            String description = "- " + HtmlUtils.htmlEscape(event.getEventName());
            StringBuilder eventObject = new StringBuilder();
            if (eventData == null) {
                eventObject.append("{title: '").append(amount).append("', start: '").append(date).append("', type: 'Event").append("', description: '").append(description).append("'},");
            } else {
                try {
                    amount += Integer.parseInt(eventData.split("'")[1]);
                    description = eventData.split("'")[7] + "<br>- " + HtmlUtils.htmlEscape(event.getEventName());
                } catch (Exception ignore) {
                    // Current uses -1 to represent error. May want to change this to a thrown error.
                    amount = -1;
                }
                eventObject.append("{title: '").append(amount).append("', start: '").append(date).append("', type: 'Event").append("', description: '").append(description).append("'},");
            }
            datesToEvents.put(new EventTypes("Event", Project.dateToString(Date.from(date))), eventObject.toString());
            current.add(Calendar.DATE, 1);
        }
    }

    /**
     * This method adds a milestone to the hashTable the hash is based on the day and type of event-JSON in this case a milestone event-JSON.
     *
     * @param milestone The Milestone object to convert to JSON.
     */
    public void add(Milestone milestone) {
        int amount = 1;
        // Uses EventTypes costume hashCode() to hash based on type and date.
        String eventData = datesToEvents.get(new EventTypes("Milestone", milestone.getMilestoneDateString()));
        String description = "- " + HtmlUtils.htmlEscape(milestone.getMilestoneName());
        StringBuilder eventObject = new StringBuilder();
        if (eventData == null) {
            eventObject.append("{title: '").append(amount).append("' , start: '").append(milestone.getMilestoneDate()).append("', type: 'Milestone").append("', description: '").append(description).append("'},");
        } else {
            try {
                amount += Integer.parseInt(eventData.split("'")[1]);
                description = eventData.split("'")[7] + "<br>- " + HtmlUtils.htmlEscape(milestone.getMilestoneName());
            } catch (Exception ignore) {
                // Current uses -1 to represent error. May want to change this to a thrown error.
                amount = -1;
            }
            eventObject.append("{title: '").append(amount).append("' , start: '").append(milestone.getMilestoneDate()).append("', type: 'Milestone").append("', description: '").append(description).append("'},");
        }
        datesToEvents.put(new EventTypes("Milestone", milestone.getMilestoneDateString()), eventObject.toString());
    }

}
