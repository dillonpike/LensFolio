package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.LeaderboardEntry;
import nz.ac.canterbury.seng302.portfolio.repository.EvidenceRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Unit test class for LeaderboardService
 */
class LeaderboardServiceTest {

    @InjectMocks
    private LeaderboardService leaderboardService;

    @Mock
    private EvidenceRepository evidenceRepository;

    @Mock
    private EvidenceService evidenceService;

    @Mock
    private ElementService elementService;

    /**
     * Setup to replace the autowired instances of these with the mocks
     */
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this); // This is required for Mockito annotations to work
    }


    /**
     * Test to check getLeaderboard method works in LeaderboardService Class
     * Also test if the method returns the correct size of LeaderboardEntry
     */
    @Test
    void testGetLeaderboardEntriesWithTheSameSize() {
        PaginatedUsersResponse response = PaginatedUsersResponse.newBuilder()
                .addUsers(UserResponse.newBuilder().setId(0).addRoles(UserRole.STUDENT))
                .addUsers(UserResponse.newBuilder().setId(1).addRoles(UserRole.STUDENT))
                .addUsers(UserResponse.newBuilder().setId(2).addRoles(UserRole.STUDENT))
                .build();

        List<UserResponse> usersList = response.getUsersList();

        for (UserResponse user : usersList) {
            List<Evidence> evidences = List.of(new Evidence());
            when(evidenceService.getEvidences(user.getId())).thenReturn(evidences);
        }
        List<LeaderboardEntry> leaderboardEntries = leaderboardService.getLeaderboardEntries(usersList);
        assertEquals(3, leaderboardEntries.size());
    }

    /**
     * Test to check setRanks method works in LeaderboardService Class
     * Also test if the method returns the correct rank for each user
     */
    @Test
    void testSetRanksWithAllStudentsHaveNoEvidence() {
        PaginatedUsersResponse response = PaginatedUsersResponse.newBuilder()
                .addUsers(UserResponse.newBuilder().setId(0).addRoles(UserRole.STUDENT))
                .addUsers(UserResponse.newBuilder().setId(1).addRoles(UserRole.STUDENT))
                .addUsers(UserResponse.newBuilder().setId(2).addRoles(UserRole.STUDENT))
                .build();

        List<UserResponse> usersList = response.getUsersList();

        for (UserResponse user : usersList) {
            List<Evidence> evidences = List.of(new Evidence());
            when(evidenceService.getEvidences(user.getId())).thenReturn(evidences);
        }
        List<LeaderboardEntry> leaderboardEntries = leaderboardService.getLeaderboardEntries(usersList);
        leaderboardService.setRanks(leaderboardEntries);
        assertEquals(1, leaderboardEntries.get(0).getRank());
        assertEquals(1, leaderboardEntries.get(1).getRank());
        assertEquals(1, leaderboardEntries.get(2).getRank());
    }

    /**
     * Test to check setRanks method works in LeaderboardService Class
     * Also test if the method returns the correct rank for each user
     */
    @Test
    void testSetRanksWithAllStudentsHaveDifferentPiecesOfEvidence() {
        PaginatedUsersResponse response = PaginatedUsersResponse.newBuilder()
                .addUsers(UserResponse.newBuilder().setId(0).addRoles(UserRole.STUDENT).setFirstName("John").setLastName("Smith"))
                .addUsers(UserResponse.newBuilder().setId(1).addRoles(UserRole.STUDENT).setFirstName("Jane").setLastName("Doe"))
                .addUsers(UserResponse.newBuilder().setId(2).addRoles(UserRole.STUDENT).setFirstName("Bob").setLastName("Smith"))
                .build();

        List<UserResponse> usersList = response.getUsersList();

        List<Evidence> evidences1 = new ArrayList<>();
        when(evidenceService.getEvidences(0)).thenReturn(evidences1);


        List<Evidence> evidences2 = new ArrayList<>();
        evidences2.add(new Evidence());
        evidences2.add(new Evidence());
        when(evidenceService.getEvidences(1)).thenReturn(evidences2);

        List<Evidence> evidences3 = new ArrayList<>();
        evidences3.add(new Evidence());
        evidences3.add(new Evidence());
        evidences3.add(new Evidence());
        when(evidenceService.getEvidences(2)).thenReturn(evidences3);

        List<LeaderboardEntry> leaderboardEntries = leaderboardService.getLeaderboardEntries(usersList);

        assertEquals(1, leaderboardEntries.get(0).getRank());
        assertEquals("Bob", leaderboardEntries.get(0).getFirstName());

        assertEquals(2, leaderboardEntries.get(1).getRank());
        assertEquals("Jane", leaderboardEntries.get(1).getFirstName());

        assertEquals(3, leaderboardEntries.get(2).getRank());
        assertEquals("John", leaderboardEntries.get(2).getFirstName());
    }

}

