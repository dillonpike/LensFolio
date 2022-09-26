package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.LeaderboardEntry;
import nz.ac.canterbury.seng302.portfolio.service.ElementService;
import nz.ac.canterbury.seng302.portfolio.service.LeaderboardService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utility.ToastUtility;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the leaderboard
 */
@Controller
public class LeaderboardController {

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private LeaderboardService leaderboardService;

    @Autowired
    private ElementService elementService;

    /**
     * Method to receive GET method for group page to display leaderboard entries to leaderboard page
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @param principal The user's authentication state
     * @return leader board page
     */
    @GetMapping("/leaderboard")
    public String showLeaderboardPage(
            Model model,
            @AuthenticationPrincipal AuthState principal
    ) {
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);
        List<LeaderboardEntry> leaderboardEntries = leaderboardService.getLeaderboardEntries(userAccountClientService.getStudentUsers());
        model.addAttribute("leaderboardEntries", leaderboardEntries);
        ToastUtility.addToastsToModel(model, new ArrayList<>(), 3);
        return "leaderboard";
    }

    @GetMapping("/leaderboard-table")
    public String leaderboardTable(
            Model model
    ) {
        List<LeaderboardEntry> leaderboardEntries = leaderboardService.getLeaderboardEntries(userAccountClientService.getStudentUsers());
        model.addAttribute("leaderboardEntries", leaderboardEntries);
        return "leaderboard::leaderboardTable";
    }
}
