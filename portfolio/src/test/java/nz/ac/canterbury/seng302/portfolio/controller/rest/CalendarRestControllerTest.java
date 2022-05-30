package nz.ac.canterbury.seng302.portfolio.controller.rest;

import nz.ac.canterbury.seng302.portfolio.controller.CalendarController;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for CalendarRestController.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CalendarRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class CalendarRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SprintService sprintService;

    /**
     * Needed so tests can load application context.
     */
    @MockBean
    private UserAccountClientService userAccountClientService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(CalendarController.class).build();
    }

    /**
     * Tests that the rest controller returns a response with a no content success status code and a payload of true
     * if the sprint service successfully updates the sprint dates.
     * @throws Exception when an exception is thrown while performing the post request
     */
    @Test
    void updateSprintDatesValid() throws Exception {
        when(sprintService.updateSprintDates(anyInt(), anyString(), anyString())).thenReturn(true);
        mockMvc.perform(post("/update-sprint")
                .param("id", "1")
                .param("sprintStartDate", "2001-10-20")
                .param("sprintEndDate", "2001-10-21"))
                .andExpect(status().isNoContent())
                .andExpect(content().string("true"));
    }

    /**
     * Tests that the rest controller returns a response with a bad request status code and a payload of false
     * if the sprint service fails to update the sprint dates.
     * @throws Exception when an exception is thrown while performing the post request
     */
    @Test
    void updateSprintDatesInvalid() throws Exception {
        when(sprintService.updateSprintDates(anyInt(), anyString(), anyString())).thenReturn(false);
        mockMvc.perform(post("/update-sprint")
                        .param("id", "1")
                        .param("sprintStartDate", "2001-10-20")
                        .param("sprintEndDate", "2001-10-21"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("false"));
    }
}