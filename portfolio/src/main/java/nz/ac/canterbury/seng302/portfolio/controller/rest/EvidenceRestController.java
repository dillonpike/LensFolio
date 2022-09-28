package nz.ac.canterbury.seng302.portfolio.controller.rest;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.service.ElementService;
import nz.ac.canterbury.seng302.portfolio.service.EvidenceService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * Handles REST requests for evidence.
 */
@RestController
public class EvidenceRestController {
    @Autowired
    private EvidenceService evidenceService;

    @Autowired
    private ElementService elementService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    /***
     * Request handler for deleting event, user will redirect to project detail page after
     * @param id Event Id
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return project detail page
     */
    @PostMapping("/delete-evidence/{id}")
    public void evidenceRemove(@PathVariable("id") Integer id,
                                 HttpServletResponse httpServletResponse, @AuthenticationPrincipal AuthState principal,
                                 Model model) {
        Integer userID = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, userID);
        try{
            Evidence evidence = evidenceService.getEvidence(id);
            if(evidence.getUserId() == userID){
                boolean wasRemoved = evidenceService.removeEvidence(id);
                if (wasRemoved) {
                    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                } else {
                    httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } else {
                httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        } catch (NullPointerException e) {
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }


    }
}
