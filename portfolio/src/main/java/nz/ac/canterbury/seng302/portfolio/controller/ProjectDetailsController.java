package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.GreeterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;


import static nz.ac.canterbury.seng302.portfolio.DatabaseConfig.*;

@Controller
public class ProjectDetailsController {

    @Autowired
    private GreeterClientService greeterClientService;


    @GetMapping("/projectDetails")
    public String greeting(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(name="name", required=false, defaultValue="Blue") String favouriteColour,
            Model model
    ) throws Exception {
        // Talk to the GreeterService on the IdP to get a message, we'll tell them our favourite colour too
        String idpMessage = greeterClientService.receiveGreeting(favouriteColour);
        model.addAttribute("idpMessage", idpMessage);

        // Below code is just begging to be added as a method somewhere...
        String role = principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("role"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");

        Integer id = Integer.valueOf(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));

        // Generate our own message, based on the information we have available to us
        String portfolioMessage = String.format(
                "The portfolio service (which is serving you this message) knows you are logged in as '%s' (role='%s'), with ID=%d",
                principal.getName(),
                role,
                id
            );
        model.addAttribute("portfolioMessage", portfolioMessage);

        // Also pass on just the favourite colour value on its own to use
        model.addAttribute("currentFavouriteColour", favouriteColour);

        // List of current sprints in the database.
        model.addAttribute("sprintList", getSprints("SELECT * FROM sprint;"));
        // A new sprint that the user can submit to have added to the database.
        model.addAttribute("sprint", new Sprint());
        model.addAttribute("userRole", role);
        model.addAttribute("projectList", getProjects("SELECT * FROM project;"));
        model.addAttribute("project", new Project());

        return "projectDetails";
    }

    /**
     * Maps to saveSprint and saves the submitted sprint to the database and updates the list being displayed on the page.
     * @param sprint    The Sprint being submitted.
     * @param model     The Model holding the data for the HTML page.
     * @return Returns to the projectDetails page with updated model.
     * @throws Exception    Error most likely caused by invalid dates.
     */
    @PostMapping("/saveSprint")
    public String makeSprint(@ModelAttribute("sprint") Sprint sprint, Model model) throws Exception {
        // Currently, sets the project model to be a default.
        // TODO: Update code to check what project name has been given for the page.
        sprint.setProjectName("Default");
        int maxNum = getMaxSprint();  // Gets the current largest sprint in the database.
        sprint.setLabelNum(++maxNum);
        // Checks to see in sprint was added to the database if it was returns a new updated list from the database to the HTML page.
        if (insertSprint(sprint)) {
            model.addAttribute("sprintList", getSprints("SELECT * FROM sprint;"));
            model.addAttribute("sprint", new Sprint());
            model.addAttribute("editSprint", new Sprint());
            return "redirect:/projectDetails";
        } else {
            throw (new Exception());
        }

    }

    /**
     * Maps to saveProject and saves the submitted sprint to the database and updates the list being displayed on the page.
     * @param project    The Sprint being submitted.
     * @param model     The Model holding the data for the HTML page.
     * @return Returns to the projectDetails page with updated model.
     * @throws Exception    Error most likely caused by invalid dates.
     */
    @PostMapping("/saveProject")
    public String makeProject(@ModelAttribute("project") Project project, Model model) throws Exception {
        // Currently, sets the project model to be a default.
        if (insertProject(project)) {
            model.addAttribute("projectList", getProjects("SELECT * FROM project;"));
            model.addAttribute("project", new Project());
            return "redirect:/projectDetails";
        } else {
            throw (new Exception());
        }
    }

    /**
     *Display edit sprint page
     * @param sprint selected sprint that the user want to be edited
     */
    @PostMapping("/editSprint")
    public String editSprint(@ModelAttribute("sprint") Sprint sprint, Model model){
        model.addAttribute("sprint", sprint);
        return "editSprint";

    }

    /**
     *Display edit sprint page
     * @param sprint selected sprint that the user want to be edited
     */
    @PostMapping("/submitEditedSprint")
    public String submitEditedSprint(@ModelAttribute("sprint") Sprint sprint, Model model) throws  Exception{
        if (updateSprint(sprint)) {
            model.addAttribute("sprintList", getSprints("SELECT * FROM sprint;"));
            model.addAttribute("sprint", new Sprint());
            model.addAttribute("editSprint", new Sprint());
            return "redirect:/projectDetails";
        } else {
            throw (new Exception());
        }

    }
}
