package nz.ac.canterbury.seng302.portfolio.service;


import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.repository.EventRepository;
import nz.ac.canterbury.seng302.portfolio.repository.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/***
 * Contains methods for saving, deleting, updating and retrieving event objects to the database.
 */
@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;


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
     * @throws Exception If event can't be found
     */
    public Event getEventById(Integer id) throws Exception {

        Optional<Event> event = eventRepository.findById(id);
        if(event.isPresent()) {
            return event.get();
        } else {
            throw new Exception("Event not found");
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
            eventUpdate.setEventName(event.getEventName());
            eventUpdate.setEventStartTime(event.getEventStartTime());
            eventUpdate.setEventEndTime(event.getEventEndTime());

            eventUpdate = eventRepository.save(eventUpdate);
            return eventUpdate;
        } else {
            event = eventRepository.save(event);
            return event;
        }
    }


    /**
     * Add a new event to the database. It gives the new event an ID based on eventIdCount.
     * @param event New event to add
     * @return Event that was added to the database
     */
    public Event addEvent(Event event) {
        event = eventRepository.save(event);
        return event;
    }

    /**
     * Remove an event from the database.
     * @param id ID of the event being removed
     */
    public void removeEvent(Integer id) {
        Optional<Event> sOptional = eventRepository.findById(id);

        if(sOptional.isPresent()) {
            Event eventUpdate = sOptional.get();
            eventRepository.deleteById(eventUpdate.getId());
        }
    }

    /**
     * Get list of all events in order
     * @return List of events
     */
    public List<Event> getAllEventsOrdered() {
        return eventRepository.findAllByOrderByEventStartDate();
    }


    /***
     * For any events existing, get the sprints colour for its start date if it is within the sprint time slot,
     * and the same is done with the events end date
     *
     * @param sprints sprints in chronological order
     * @return events in chronological order
     */
    public List<Event> getAllEventsOrderedWithColour(List<Sprint> sprints) {
        List<Event> eventList = getAllEventsOrdered();
        for (int i = 0; i < eventList.size(); i++) {
//            Get the event from current index
            Event currentEvent = eventList.get(i);

            // Reset Event's color
            currentEvent.setStartDateColour(null);
            currentEvent.setEndDateColour(null);

            for (Sprint sprint : sprints) {
                if (currentEvent.getEventStartDate().after(sprint.getStartDate()) && currentEvent.getEventStartDate().before(sprint.getEndDate())) {
                    currentEvent.setStartDateColour(sprint.getColour());
                }
                if (currentEvent.getEventEndDate().after(sprint.getStartDate()) && currentEvent.getEventEndDate().before(sprint.getEndDate())) {
                    currentEvent.setEndDateColour(sprint.getColour());
                }
            }
            eventRepository.save(currentEvent);
        }
        return getAllEventsOrdered();
    }
}
