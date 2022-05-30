package nz.ac.canterbury.seng302.portfolio.service;


import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.repository.EventRepository;
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
    private EventRepository repository;

    /**
     * Get list of all events
     * @return List of events
     */
    public List<Event> getAllEvents() {
        return (List<Event>) repository.findAll();
    }

    /**
     * Get event by Id
     * @param id id of event
     * @return event with the id that is the input
     * @throws Exception If event can't be found
     */
    public Event getEventById(Integer id) throws Exception {

        Optional<Event> event = repository.findById(id);
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
        Optional<Event> sOptional = repository.findById((Integer) event.getId());

        if (sOptional.isPresent()) {
            Event eventUpdate = sOptional.get();
            eventUpdate.setEventStartDate(event.getEventStartDate());
            eventUpdate.setEventEndDate(event.getEventEndDate());
            eventUpdate.setEventName(event.getEventName());
            eventUpdate.setEventStartTime(event.getEventStartTime());
            eventUpdate.setEventEndTime(event.getEventEndTime());

            eventUpdate = repository.save(eventUpdate);
            return eventUpdate;
        } else {
            event = repository.save(event);
            return event;
        }
    }


    /**
     * Add a new event to the database. It gives the new event an ID based on eventIdCount.
     * @param event New event to add
     * @return Event that was added to the database
     */
    public Event addEvent(Event event) {
        event = repository.save(event);
        return event;
    }

    /**
     * Remove an event from the database.
     * @param id ID of the event being removed
     */
    public void removeEvent(Integer id) {
        Optional<Event> sOptional = repository.findById(id);

        if(sOptional.isPresent()) {
            Event eventUpdate = sOptional.get();
            repository.deleteById(eventUpdate.getId());
        }
    }

    /**
     * Get list of all events
     * @return List of events
     */
    public List<Event> getAllEventsOrdered() {
        return repository.findAllByOrderByEventStartDate();
    }
}
