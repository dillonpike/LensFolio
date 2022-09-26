package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.LeaderboardEntry;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service for the leaderboard
 */
@Service
public class LeaderboardService {

    @Autowired
    private EvidenceService evidenceService;

    /**
     * Gets the leaderboard entries for the given users, convert UserResponse to LeaderboardEntry
     * @param userList Current existing users
     * @return List of LeaderboardEntry
     */
    public List<LeaderboardEntry> getLeaderboardEntries(List<UserResponse> userList) {
        List<LeaderboardEntry> leaderboardEntries = new ArrayList<>();
        for (UserResponse user : userList) {
            int numEvidence =  evidenceService.getEvidences(user.getId()).size();
            leaderboardEntries.add(new LeaderboardEntry(user.getUsername(), user.getFirstName(), user.getLastName(), numEvidence, 1, user.getId()));
        }
        setRanks(leaderboardEntries);
        return leaderboardEntries;
    }

    /**
     * Method to set the ranks of the leaderboard entries
     * if two user have the same number of evidence, they will have the same rank
     * @param leaderboardEntries List of LeaderboardEntry
     */
    void setRanks(List<LeaderboardEntry> leaderboardEntries) {
        Collections.sort(leaderboardEntries);
        for (int i = 1; i < leaderboardEntries.size(); i++) {
            if (leaderboardEntries.get(i).getScore() == leaderboardEntries.get(i - 1).getScore()) {
                leaderboardEntries.get(i).setRank(leaderboardEntries.get(i - 1).getRank());
            } else {
                leaderboardEntries.get(i).setRank(i + 1);
            }
        }
    }
}
