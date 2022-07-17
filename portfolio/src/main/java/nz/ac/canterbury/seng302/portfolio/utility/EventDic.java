package nz.ac.canterbury.seng302.portfolio.utility;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.model.Project;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * This Dictionary is used for adding events together that have matching types and dates.
 * It also has a method to generate the combined values which are the individual JSON lists of the events.
 */
public class EventDic {
    // Creating a HashTable Dictionary
    // Because we want to separate the types of events we store the JSON objects as the values rather than just the amounts.
    Hashtable<EventTypes, String> datesToEvents = new Hashtable<>();

    /**
     * Method used to combine the JSON lists together for the events.
     *
     * @return  Combine JSON list.
     */
    public String makeJSON() {
        StringBuilder JSONList = new StringBuilder();
        Enumeration<String> values = datesToEvents.elements();

        // Iterate the values
        while(values.hasMoreElements() ){
            JSONList.append(values.nextElement());
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
        // Uses EventTypes costume hashCode() to hash based on type and date.
        String eventData = datesToEvents.get(new EventTypes("Deadline", deadline.getDeadlineDateString()));

        StringBuilder eventObject = new StringBuilder();
        if (eventData == null) {
            eventObject.append("{title: '").append(amount).append("' , start: '").append(deadline.getDeadlineDate()).append("', type: 'Deadline").append("'},");
        } else {
            try {
                amount += Integer.parseInt(eventData.split("'")[1]);
            } catch (Exception ignore) {
                // Current uses -1 to represent error. May want to change this to a thrown error.
                amount = -1;
            }
            eventObject.append("{title: '").append(amount).append("' , start: '").append(deadline.getDeadlineDate()).append("', type: 'Deadline").append("'},");
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
            StringBuilder eventObject = new StringBuilder();
            if (eventData == null) {
                eventObject.append("{title: '").append(amount).append("', start: '").append(date).append("', type: 'Event").append("'},");
            } else {
                try {
                    amount += Integer.parseInt(eventData.split("'")[1]);
                } catch (Exception ignore) {
                    // Current uses -1 to represent error. May want to change this to a thrown error.
                    amount = -1;
                }
                eventObject.append("{title: '").append(amount).append("', start: '").append(date).append("', type: 'Event").append("'},");
            }
            datesToEvents.put(new EventTypes("Event", Project.dateToString(Date.from(date))), eventObject.toString());
            current.add(Calendar.DATE, 1);
        }
    }

    /**
     * Currently un-used.
     * @param milestone ignore
     */
    public void add(Milestone milestone) { //TODO Create a method for mileStone most likely can just use the code from Deadlines.
    }

}
