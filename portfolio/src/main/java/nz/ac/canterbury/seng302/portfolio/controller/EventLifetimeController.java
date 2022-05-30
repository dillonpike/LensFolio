package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.service.DateValidationService;
import nz.ac.canterbury.seng302.portfolio.service.EventService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


/**
 * Controller for the add event page
 */
@Controller
public class EventLifetimeController {

    @Autowired
    private EventService eventService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private DateValidationService dateValidationService;

    /**
     * Navigates to the add event page and saves new event
     * @param model For adding the event and error handling
     */
    @GetMapping("/add-event")
    public String eventAddForm(Model model) {

        Event blankEvent = new Event();

        model.addAttribute("event", blankEvent);
        model.addAttribute("eventDateError", "");

        return "addEvent";

    }

    /**
     * Tries to save the new event to the database
     * @param event New event
     */
    @PostMapping("/add-event")
    public String projectSave(
            @ModelAttribute("event") Event event,
            Model model
    ) {
        event.setStartDateString(event.getStartDateString());
        event.setEndDateString(event.getEndDateString());
        event.setStartTimeString(event.getStartTimeString());
        event.setEndTimeString(event.getEndTimeString());
        eventService.addEvent(event);
        return "redirect:/details";
    }

    @PostMapping(value="/add-event/error")
    public String updateEventRangeErrors(@RequestParam(value="eventStartDate") String eventStartDate,
                                          @RequestParam(value="eventEndDate") String eventEndDate,
                                          @RequestParam(value="eventStartTime") String eventStartTime,
                                         @RequestParam(value="eventEndTime") String eventEndTime,
                                          Model model) {
        model.addAttribute("eventDateError",
                dateValidationService.validateDateRangeNotEmpty(eventStartDate, eventEndDate) + " " +
                        dateValidationService.validateStartDateNotAfterEndDate(eventStartDate, eventEndDate) + " " +
                        dateValidationService.validateDatesInProjectDateRange(eventStartDate, eventEndDate) + " " +
                        dateValidationService.validateStartTimeNotAfterEndTime(eventStartTime, eventEndTime, eventStartDate, eventEndDate) + " " +
                        dateValidationService.validateTimeRangeNotEmpty(eventStartTime, eventEndTime)

        );
        return "addEvent :: #eventDateError";
    }


    /***
     * Request handler for deleting event, user will redirect to project detail page after
     * @param id Event Id
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return project detail page
     */
    @GetMapping("/delete-event/{id}")
    public String sprintRemove(@PathVariable("id") Integer id, Model model) {
        eventService.removeEvent(id);

        /* Return the name of the Thymeleaf template */
        return "redirect:/details";
    }

}
