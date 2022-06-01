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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class EventServiceTest {

    @Mock
    private SprintRepository sprintRepository;

    @Mock
    private EventRepository eventRepository;

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
        List<Sprint> sprintList = new ArrayList<>();
        sprintList.add(sprint);

        Event event = new Event();
        event.setEventStartDate(sprintService.calendarDateStringToDate("2001-12-21", false));
        event.setEventEndDate(sprintService.calendarDateStringToDate("2001-12-21", true));
        event.setEndDateColour("#5897fc");
        List<Event> eventList = new ArrayList<>();
        eventList.add(event);

        when(eventService.getAllEventsOrderedWithColour(sprintList)).thenReturn(eventList);

        List<Event> outputEventList = eventService.getAllEventsOrderedWithColour(sprintList);
        assertThat(outputEventList.size()).isSameAs(eventList.size());
        assertThat(outputEventList.get(0).getEndDateColour()).isSameAs(event.getEndDateColour());
    }
}
