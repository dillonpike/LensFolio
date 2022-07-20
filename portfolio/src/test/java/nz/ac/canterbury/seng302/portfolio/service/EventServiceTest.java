package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.repository.EventRepository;
import nz.ac.canterbury.seng302.portfolio.repository.SprintRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class EventServiceTest {

    @Mock
    private SprintRepository sprintRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private DateValidationService dateValidationService;

    @InjectMocks
    private EventService eventService;

    @InjectMocks
    private SprintService sprintService;

    @Test
    void givenOneEventExistAndDateInRange_returnBlueColor() {
        Sprint sprint = new Sprint();
        sprint.setName("Testing");
        sprint.setStartDate(sprintService.calendarDateStringToDate("2001-12-20", false));
        sprint.setEndDate(sprintService.calendarDateStringToDate("2001-12-22", true));
        sprint.setColour("#5897fc");
        List<Sprint> sprintList = new ArrayList<>();
        sprintList.add(sprint);

        Event event = new Event();
        event.setEventStartDate(sprintService.calendarDateStringToDate("2001-12-21", false));
        event.setEventEndDate(sprintService.calendarDateStringToDate("2001-12-21", true));
        List<Event> eventList = new ArrayList<>();
        eventList.add(event);

        when(eventService.getAllEventsOrderedWithColour(sprintList)).thenReturn(eventList);

        List<Event> outputEventList = eventService.getAllEventsOrderedWithColour(sprintList);
        assertThat(outputEventList.size()).isSameAs(eventList.size());
        assertThat(outputEventList.get(0).getStartDateColour()).isSameAs(sprint.getColour());
        assertThat(outputEventList.get(0).getEndDateColour()).isSameAs(sprint.getColour());
    }

    @Test
    void givenOneEventExistAndDateOutOfRange_returnNull() {
        Sprint sprint = new Sprint();
        sprint.setName("Testing");
        sprint.setStartDate(sprintService.calendarDateStringToDate("2001-12-20", false));
        sprint.setEndDate(sprintService.calendarDateStringToDate("2001-12-22", true));
        sprint.setColour("#5897fc");
        List<Sprint> sprintList = new ArrayList<>();
        sprintList.add(sprint);

        Event event = new Event();
        event.setEventStartDate(sprintService.calendarDateStringToDate("2001-12-23", false));
        event.setEventEndDate(sprintService.calendarDateStringToDate("2001-12-23", true));
        List<Event> eventList = new ArrayList<>();
        eventList.add(event);

        when(eventService.getAllEventsOrderedWithColour(sprintList)).thenReturn(eventList);

        List<Event> outputEventList = eventService.getAllEventsOrderedWithColour(sprintList);
        assertThat(outputEventList.size()).isSameAs(eventList.size());
        assertThat(outputEventList.get(0).getStartDateColour()).isNull();
        assertThat(outputEventList.get(0).getEndDateColour()).isNull();

    }

    @Test
    void givenMultipleEventsWithAllDateInSprintRange_returnSprintColor() {
        Sprint sprint = new Sprint();
        sprint.setName("Testing");
        sprint.setStartDate(sprintService.calendarDateStringToDate("2001-12-20", false));
        sprint.setEndDate(sprintService.calendarDateStringToDate("2001-12-22", true));
        sprint.setColour("#5897fc");
        List<Sprint> sprintList = new ArrayList<>();
        sprintList.add(sprint);

        Event event1 = new Event();
        Event event2 = new Event();
        Event event3 = new Event();

        event1.setEventStartDate(sprintService.calendarDateStringToDate("2001-12-21", false));
        event1.setEventEndDate(sprintService.calendarDateStringToDate("2001-12-21", true));

        event2.setEventStartDate(sprintService.calendarDateStringToDate("2001-12-21", false));
        event2.setEventEndDate(sprintService.calendarDateStringToDate("2001-12-21", true));

        event3.setEventStartDate(sprintService.calendarDateStringToDate("2001-12-21", false));
        event3.setEventEndDate(sprintService.calendarDateStringToDate("2001-12-21", true));

        List<Event> eventList = new ArrayList<>();
        eventList.add(event1);
        eventList.add(event2);
        eventList.add(event3);

        when(eventService.getAllEventsOrderedWithColour(sprintList)).thenReturn(eventList);
        List<Event> outputEventList = eventService.getAllEventsOrderedWithColour(sprintList);

        assertThat(outputEventList.size()).isSameAs(eventList.size());

        assertThat(outputEventList.get(0).getStartDateColour()).isSameAs(sprint.getColour());
        assertThat(outputEventList.get(1).getStartDateColour()).isSameAs(sprint.getColour());
        assertThat(outputEventList.get(2).getStartDateColour()).isSameAs(sprint.getColour());

        assertThat(outputEventList.get(0).getEndDateColour()).isSameAs(sprint.getColour());
        assertThat(outputEventList.get(1).getEndDateColour()).isSameAs(sprint.getColour());
        assertThat(outputEventList.get(2).getEndDateColour()).isSameAs(sprint.getColour());

    }

    @Test
    void givenMultipleEventsWithPartialEventsInSprintRange_returnBlueColorIfInRange_andNullIfOutOfRange() {
        Sprint sprint = new Sprint();
        sprint.setName("Testing");
        sprint.setStartDate(sprintService.calendarDateStringToDate("2001-12-20", false));
        sprint.setEndDate(sprintService.calendarDateStringToDate("2001-12-22", true));
        sprint.setColour("#5897fc");
        List<Sprint> sprintList = new ArrayList<>();
        sprintList.add(sprint);

        Event event1 = new Event();
        Event event2 = new Event();
        Event event3 = new Event();

        event1.setEventStartDate(sprintService.calendarDateStringToDate("2001-12-21", false));
        event1.setEventEndDate(sprintService.calendarDateStringToDate("2001-12-21", true));

        event2.setEventStartDate(sprintService.calendarDateStringToDate("2001-12-21", false));
        event2.setEventEndDate(sprintService.calendarDateStringToDate("2001-12-21", true));

        event3.setEventStartDate(sprintService.calendarDateStringToDate("2001-12-23", false));
        event3.setEventEndDate(sprintService.calendarDateStringToDate("2001-12-23", true));

        List<Event> eventList = new ArrayList<>();
        eventList.add(event1);
        eventList.add(event2);
        eventList.add(event3);

        when(eventService.getAllEventsOrderedWithColour(sprintList)).thenReturn(eventList);
        List<Event> outputEventList = eventService.getAllEventsOrderedWithColour(sprintList);

        assertThat(outputEventList.size()).isSameAs(eventList.size());

        assertThat(outputEventList.get(0).getStartDateColour()).isSameAs(sprint.getColour());
        assertThat(outputEventList.get(1).getStartDateColour()).isSameAs(sprint.getColour());
        assertThat(outputEventList.get(2).getStartDateColour()).isNull();

        assertThat(outputEventList.get(0).getEndDateColour()).isSameAs(sprint.getColour());
        assertThat(outputEventList.get(1).getEndDateColour()).isSameAs(sprint.getColour());
        assertThat(outputEventList.get(2).getEndDateColour()).isNull();
    }

    @Test
    void givenSprint_returnEventsThatOverlap() {
        Sprint sprint = new Sprint();
        sprint.setName("Testing");
        sprint.setStartDate(sprintService.calendarDateStringToDate("2001-12-20", false));
        sprint.setEndDate(sprintService.calendarDateStringToDate("2001-12-22", true));
        sprint.setColour("#5897fc");

        Event event1 = new Event();
        Event event2 = new Event();
        Event event3 = new Event();
        Event event4 = new Event();

        event1.setEventName("Start Date Overlaps");
        event1.setEventStartDate(sprintService.calendarDateStringToDate("2001-12-21", false));
        event1.setEventEndDate(sprintService.calendarDateStringToDate("2001-12-23", true));

        event2.setEventName("End Date Overlaps");
        event2.setEventStartDate(sprintService.calendarDateStringToDate("2001-12-19", false));
        event2.setEventEndDate(sprintService.calendarDateStringToDate("2001-12-21", true));

        event3.setEventName("No Dates Overlap");
        event3.setEventStartDate(sprintService.calendarDateStringToDate("2001-12-23", false));
        event3.setEventEndDate(sprintService.calendarDateStringToDate("2001-12-23", true));

        event4.setEventName("Dates encapsulate sprint");
        event4.setEventStartDate(sprintService.calendarDateStringToDate("2001-12-18", false));
        event4.setEventEndDate(sprintService.calendarDateStringToDate("2001-12-24", true));

        List<Event> eventList = new ArrayList<>();
        eventList.add(event1);
        eventList.add(event2);
        eventList.add(event3);
        eventList.add(event4);

        when(eventService.validateEventStartDateInSprintDate(event1, sprint)).thenReturn(true);
        when(eventService.validateEventEndDateInSprintDate(event2, sprint)).thenReturn(true);

        when(eventService.getAllEventsOrdered()).thenReturn(eventList);

        List<Event> returnedEvents = eventService.getAllEventsOverlappingWithSprint(sprint);

        assertEquals(event1.getEventName(), returnedEvents.get(0).getEventName());
        assertEquals(event2.getEventName(), returnedEvents.get(1).getEventName());
        assertEquals(event4.getEventName(), returnedEvents.get(2).getEventName());

    }
}
