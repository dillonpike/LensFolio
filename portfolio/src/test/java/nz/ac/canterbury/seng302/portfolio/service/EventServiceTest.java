package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;


    /**
     * Tests for if an event can be made or not, this is done through the EventService class.
     */
    @Test
    void addEvent_success() {
        Event event = new Event();
        event.setEventName("Testing");

        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event created = eventService.addEvent(event);
        assertThat(created.getEventName()).isSameAs(event.getEventName());
    }

    /**
     * Tests if given an event exist it can be retrieved from the database.
     */
    @Test
    void testGetAllEvent_givenEventExistInDb_returnNonEmptyList() {
        Event event = new Event();
        event.setEventName("Testing");
        List<Event> eventList = new ArrayList<>();
        eventList.add(event);

        //providing mock/knowledge
        when(eventRepository.findAll()).thenReturn(eventList);

        List<Event> fetchedEvents = eventService.getAllEvents();
        assertThat(fetchedEvents.size()).isGreaterThan(0);
        assertThat(fetchedEvents).isSameAs(eventList);
    }

    /**
     * Tests for if an event can be updated or not, this is done through the EventService class.
     */
    @Test
    void testUpdateEvent() {
        Date date = new Date();
        LocalTime time = LocalTime.now();
        Event event = new Event();
        event.setEventName("Testing");
        event.setEventStartDate(date);
        event.setEventEndDate(date);
        event.setEventStartTime(time);
        event.setEventEndTime(time);


        //providing mock/knowledge
        when(eventRepository.findById(any(Integer.class))).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event updated = eventService.updateEvent(event);
        assertThat(updated.getEventName()).isSameAs(event.getEventName());
        assertThat(updated.getEventStartDate()).isSameAs(event.getEventStartDate());
        assertThat(updated.getEventEndDate()).isSameAs(event.getEventEndDate());
        assertThat(updated.getEventStartTime()).isSameAs(event.getEventStartTime());
        assertThat(updated.getEventEndTime()).isSameAs(event.getEventEndTime());
    }

    /**
     * Tests for if an event can be removed or not, this is done through the EventService class.
     */
    @Test
    void testRemoveEvent() {
        Event event = new Event();
        event.setEventName("Testing");

        //providing mock/knowledge
        when(eventRepository.findById(any(Integer.class))).thenReturn(Optional.of(event)).thenThrow(NullPointerException.class);

        eventService.removeEvent(event.getId());

        try {
            Event e = eventService.getEventById(event.getId());
            fail("Did not remove the event from the database.");
        } catch (Exception ignore) {}
    }

}
