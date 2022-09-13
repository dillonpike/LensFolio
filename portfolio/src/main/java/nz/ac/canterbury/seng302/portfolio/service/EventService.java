package nz.ac.canterbury.seng302.portfolio.service;


import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.repository.EventRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.ws.rs.NotAcceptableException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


/***
 * Contains methods for saving, deleting, updating and retrieving event objects to the database.
 */
@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    private static final String EVENT_NAME_ERROR_MESSAGE = "eventAlertMessage";

    private static final String EVENT_DATE_ERROR_MESSAGE = "eventDateTimeAlertMessage";

    /**
     * Get list of all events
     * @return List of events
     */
    public List<Event> getAllEvents() {
        return (List<Event>) eventRepository.findAll();
    }

    /**
     * Get event by Id
     * @param id id of event
     * @return event with the id that is the input
     * @throws ObjectNotFoundException If event can't be found
     */
    public Event getEventById(Integer id) throws ObjectNotFoundException {

        Optional<Event> event = eventRepository.findById(id);
        if (event.isPresent()) {
            return event.get();
        } else {
            throw new ObjectNotFoundException(id, "Unknown Event");
        }
    }

    /**
     * Updates a event
     * @param event Event to update it to
     * @return Newly edited event
     */
    public Event updateEvent(Event event) {
        Optional<Event> sOptional = eventRepository.findById((Integer) event.getId());

        if (sOptional.isPresent()) {
            Event eventUpdate = sOptional.get();
            eventUpdate.setEventStartDate(event.getEventStartDate());
            eventUpdate.setEventEndDate(event.getEventEndDate());
            eventUpdate.setEventName(event.getEventName().trim());

            eventUpdate = eventRepository.save(eventUpdate);
            return eventUpdate;
        } else {
            event = eventRepository.save(event);
            return event;
        }
    }


    /**
     * Add a new event to the database, trim any whitespace from the event name. It gives the new event an ID based on eventIdCount.
     * @param event New event to add
     * @return Event that was added to the database
     */
    public Event addEvent(Event event) {
        String eventName = event.getEventName().trim();
        event.setEventName(eventName);
        event = eventRepository.save(event);
        return event;
    }

    /**
     * Remove an event from the database.
     * @param id ID of the event being removed
     */
    public void removeEvent(Integer id) {
        Optional<Event> sOptional = eventRepository.findById(id);

        if (sOptional.isPresent()) {
            Event eventUpdate = sOptional.get();
            eventRepository.deleteById(eventUpdate.getId());
        }
    }

    /**
     * Returns a list of all events ordered by start date
     * @return  events ordered by start date
     */
    public List<Event> getAllEventsOrderedStartDate() {
        return eventRepository.findAllByOrderByEventStartDate();
    }

    /**
     * Returns a list of all events ordered by end date
     * @return  events ordered by start date
     */
    public List<Event> getAllEventsOrderedEndDate() {
        return eventRepository.findAllByOrderByEventEndDate();
    }

    /***
     * For any events existing, get the sprints colour for its start date if it is within the sprint time slot,
     * and the same is done with the events end date
     *
     * @param sprints sprints in chronological order
     * @return events in chronological order
     */
    public List<Event> getAllEventsOrderedWithColour(List<Sprint> sprints) {
        List<Event> eventList = getAllEventsOrderedStartDate();
        for (Event currentEvent : eventList) {
            // Reset Event's color
            currentEvent.setStartDateColour(null);
            currentEvent.setEndDateColour(null);

            for (Sprint sprint : sprints) {
                if (validateEventStartDateInSprintDate(currentEvent, sprint)) {
                    currentEvent.setStartDateColour(sprint.getColour());
                }
                if (validateEventEndDateInSprintDate(currentEvent, sprint)) {
                    currentEvent.setEndDateColour(sprint.getColour());
                }
            }
            eventRepository.save(currentEvent);
        }
        return getAllEventsOrderedStartDate();
    }

    /**
     * Gets a list of events that overlap with the given sprint in some way. This is to know what events should be
     * displayed with this sprint. It does this by checking if either of the dates are within the sprints dates.
     * @param sprint Sprint to check events against.
     * @return List of events that overlap with the given sprint.
     */
    public List<Event> getAllEventsOverlappingWithSprint(Sprint sprint) {
        ArrayList<Event> eventsList = (ArrayList<Event>) getAllEventsOrderedStartDate();
        ArrayList<Event> eventsOverlapped = new ArrayList<>();

        for (Event currentEvent : eventsList) {
            if (validateEventStartDateInSprintDate(currentEvent, sprint) ||
                    validateEventEndDateInSprintDate(currentEvent, sprint) ||
                    // For events that start before and go after the sprint (would not be present with above checks).
                    (currentEvent.getEventStartDate().before(sprint.getStartDate()) && currentEvent.getEventEndDate().after(sprint.getEndDate()))
            ) {
                eventsOverlapped.add(currentEvent);
            }
        }
        return eventsOverlapped;
    }

    /**
     * Validate if particular event's start date is in sprint date range
     * @param event The updated event
     * @param sprint The sprint to compare with
     * @return True if event start date is in sprint date range
     */
    public boolean validateEventStartDateInSprintDate(Event event, Sprint sprint) {
        return event.getEventStartDate().compareTo(sprint.getStartDate()) >= 0 && event.getEventStartDate().compareTo(sprint.getEndDate()) <= 0;
    }


    /**
     * Validate if particular event's end date is in sprint date
     * @param event The updated event
     * @param sprint The sprint to compare with
     * @return True if event end date is in sprint date range
     */
    public boolean validateEventEndDateInSprintDate(Event event, Sprint sprint) {
        return event.getEventEndDate().compareTo(sprint.getStartDate()) >= 0 && event.getEventEndDate().compareTo(sprint.getEndDate()) <= 0;
    }

    /**
     * Validate an events fields to ensure they are valid.
     * @param event Event to validate
     * @param model Model to add errors to
     * @throws NotAcceptableException If the event is not valid
     */
    public void validateEvent(Event event, Model model) throws NotAcceptableException {

        event.setEventName(event.getEventName().trim());
        model.addAttribute("event", event);
        boolean hasError = false;
        if (event.getEventName() == null || event.getEventName().trim().isEmpty()) {
            model.addAttribute(EVENT_NAME_ERROR_MESSAGE, "Event name cannot be empty");
            hasError = true;
        } else if (event.getEventName().length() < 2) {
            model.addAttribute(EVENT_NAME_ERROR_MESSAGE, "Name must be at least 2 characters");
            hasError = true;
        } else if (event.getEventName().length() > 30) {
            model.addAttribute(EVENT_NAME_ERROR_MESSAGE, "Name cannot be greater than 30 characters");
            hasError = true;
        }
        if (event.getEventStartDate() == null || event.getEventStartDate().before(new Date(0))) {
            model.addAttribute(EVENT_DATE_ERROR_MESSAGE, "Correctly formatted dates is required");
            hasError = true;
        }
        if (event.getEventEndDate() == null || event.getEventEndDate().before(new Date(0))) {
            model.addAttribute(EVENT_DATE_ERROR_MESSAGE, "Correctly formatted dates is required");
            hasError = true;
        }
        if (hasError) {
            throw new NotAcceptableException("Event fields have errors");
        }
    }
}
