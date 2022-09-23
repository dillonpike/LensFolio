package nz.ac.canterbury.seng302.portfolio.controller.rest;

import nz.ac.canterbury.seng302.portfolio.model.HighFivers;
import nz.ac.canterbury.seng302.portfolio.service.EvidenceService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles REST requests for the calendar page.
 */
@RestController
public class HighFiveController {

    /**
     * Service that provides methods for sprints.
     */
    @Autowired
    private EvidenceService evidenceService;

    @GetMapping("/get-high-fivers")
    public List<HighFivers> addEvidence(
            @ModelAttribute("evidenceId") Integer evidenceId,
            Model model,
            HttpServletResponse httpServletResponse,
            @AuthenticationPrincipal AuthState principal
    ) {
        try{
            return evidenceService.getHighFivers(evidenceId);
        } catch (Exception e) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new ArrayList<>();
        }

    }
}
