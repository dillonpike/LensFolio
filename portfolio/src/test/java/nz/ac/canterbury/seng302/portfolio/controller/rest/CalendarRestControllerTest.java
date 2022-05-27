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
 * Junit testing to test the Calendar Controller
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CalendarRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class CalendarRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SprintService sprintService;

    @MockBean
    private UserAccountClientService userAccountClientService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(CalendarController.class).build();
    }

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