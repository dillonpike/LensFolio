package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.service.DateValidationService;
import nz.ac.canterbury.seng302.portfolio.service.EventService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class EditEventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private DateValidationService dateValidationService;

    /**
     * Gets data for editing a given event.
     * @param id Id of event
     * @param model Used to display the event data to the UI
     * @throws Exception If getting the event from the given id fails
     */
    @GetMapping("/edit-event/{id}")
    public String eventEditForm(@PathVariable("id") Integer id, Model model ) throws Exception {
        Event event = eventService.getEventById(id);
        /* Add Event details to the model */
        model.addAttribute("eventId", id);
        model.addAttribute("event", event);
        model.addAttribute("eventDateError", "");

        /* Return the name of the Thymeleaf template */
        return "editEvent";
    }

    /**
     * Tries to save new data to event with given eventId to the database.
     * @param id Id of event edited
     * @param event Event data to be updated
     * @throws Exception if sprint cannot be found from the given ID or if it cannot be saved.
     */
    @PostMapping("/edit-event/{id}")
    public String eventEditSave(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal AuthState principal,
            @ModelAttribute("event") Event event,
            Model model
    ) throws Exception {
        Event newEvent = eventService.getEventById(id);
        newEvent.setEventName(event.getEventName());
        newEvent.setStartDateString(event.getStartDateString());
        newEvent.setEndDateString(event.getEndDateString());

        eventService.updateEvent(newEvent);

        return "redirect:/details";
    }

    @RequestMapping(value="/edit-event/error", method= RequestMethod.POST)
    public String updateEventRangeErrors(@RequestParam(value="eventStartDate") String eventStartDate,
                                         @RequestParam(value="eventEndDate") String eventEndDate,
                                         @RequestParam(value="eventStartTime") String eventStartTime,
                                         @RequestParam(value="eventEndTime") String eventEndTime,
                                         Model model) {
        model.addAttribute("eventDateError",
                dateValidationService.validateDateRangeNotEmpty(eventStartDate, eventEndDate) + " " +
                        dateValidationService.validateStartDateNotAfterEndDate(eventStartDate, eventEndDate) + " " +
                        dateValidationService.validateDatesInProjectDateRange(eventStartDate, eventEndDate) + " " +
                        dateValidationService.validateStartTimeNotAfterEndTime(eventStartTime, eventEndTime, eventStartDate, eventEndDate));
        return "editEvent :: #eventDateError";
    }
}
