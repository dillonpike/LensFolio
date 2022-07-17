package nz.ac.canterbury.seng302.portfolio.utility;

import nz.ac.canterbury.seng302.portfolio.controller.SprintLifetimeController;
import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Milestone;

import java.util.Date;
import java.util.Hashtable;

public class EventDic {
    // Creating a HashTable Dictionary
    // Because we want to separate the types of events we store the JSON objects as the values rather than just the amounts.
    Hashtable<String, String> datesToEvents = new Hashtable<>();

    public String makeJSON() {
        StringBuilder JSONList = new StringBuilder();
        do {
            try {
                JSONList.append(datesToEvents.elements().nextElement());
            } catch (Exception ignore) {
                break;
            }
        } while (true);

        return JSONList.toString();
    }

    public void add(Deadline deadline) {
        int amount = 1;
        String eventData = datesToEvents.get(deadline.getDeadlineDateString());
        StringBuilder eventObject = new StringBuilder();
        if (eventData == null) {
            eventObject.append("{title: '").append(amount).append("' , start: '").append(deadline.getDeadlineDate()).append("', type: 'Deadline").append("'},");
        } else {
            try {
                amount += Integer.parseInt(eventData.split(" ")[1]);
            } catch (Exception ignore) {
                // Current uses -1 to represent error. May want to change this to a thrown error.
                amount = -1;
            }
            eventObject.append("{title: '").append(amount).append("' , start: '").append(deadline.getDeadlineDate()).append("', type: 'Deadline").append("'},");
        }
        datesToEvents.put(deadline.getDeadlineDateString(), eventObject.toString());
    }

    public void add(Event event) {
        int amount = 1;
        String eventData = datesToEvents.get(event.getDateRange());
        StringBuilder eventObject = new StringBuilder();
        if (eventData == null) {
            Date endDate = SprintLifetimeController.getUpdatedDate(event.getEventEndDate(), 1, 0);
            eventObject.append("{title: '").append(amount).append("', start: '").append(event.getEventStartDate()).append("', end: '").append(endDate.toInstant()).append("', type: 'Event").append("'},");
        } else {
            try {
                amount += Integer.parseInt(eventData.split(" ")[1]);
            } catch (Exception ignore) {
                // Current uses -1 to represent error. May want to change this to a thrown error.
                amount = -1;
            }
            Date endDate = SprintLifetimeController.getUpdatedDate(event.getEventEndDate(), 1, 0);
            eventObject.append("{title: '").append(amount).append("', start: '").append(event.getEventStartDate()).append("', end: '").append(endDate.toInstant()).append("', type: 'Event").append("'},");
        }
        datesToEvents.put(event.getDateRange(), eventObject.toString());
    }

    public void add(Milestone milestone) {
    }

}
