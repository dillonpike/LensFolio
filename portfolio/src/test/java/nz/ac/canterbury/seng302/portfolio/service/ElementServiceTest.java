package nz.ac.canterbury.seng302.portfolio.service;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.UserSorting;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit testing for the ElementService class.
 */
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class ElementServiceTest {

    @Mock
    private UserAccountClientService userAccountClientService;

    @Mock
    private UserSortingService userSortingService;

    @InjectMocks
    private ElementService elementService;

    /**
     * Mocked user response which contains the data of the user1
     */
    private final UserResponse mockUser1 = UserResponse.newBuilder()
            .setBio("default bio")
            .setCreated(Timestamp.newBuilder().setSeconds(55))
            .setEmail("hello@test.com")
            .setFirstName("firsttestname")
            .setLastName("lasttestname")
            .setMiddleName("middlettestname")
            .setNickname("niktestname")
            .setPersonalPronouns("He/him")
            .addRoles(UserRole.STUDENT)
            .addRoles(UserRole.COURSE_ADMINISTRATOR)
            .build();
    /**
     * Mocked user response which contains the data of the user2
     */
    private final UserResponse mockUser2 = UserResponse.newBuilder()
            .setBio("default bio")
            .setCreated(Timestamp.newBuilder().setSeconds(55))
            .setEmail("hello@test.com")
            .setFirstName("firsttestname")
            .setLastName("lasttestname")
            .setMiddleName("middlettestname")
            .setNickname("niktestname")
            .setPersonalPronouns("He/him")
            .addRoles(UserRole.STUDENT)
            .build();

    private final PaginatedUsersResponse mockedUserList = PaginatedUsersResponse.newBuilder()
            .addUsers(mockUser1).addUsers(mockUser2).build();

    /**
     * Tests that when the given user has a saved user sorting, then the addUsersToModel method adds all users to the
     * model as well as the saved user sorting.
     */
    @Test
    void testAddUsersToModelWithUserSorting() {
        int id = 1;
        UserSorting expectedUserSorting = new UserSorting(id, 2, "desc");
        when(userAccountClientService.getAllUsers()).thenReturn(mockedUserList);
        when(userSortingService.getUserSortingById(id)).thenReturn(expectedUserSorting);
        Model model = mock(Model.class);
        elementService.addUsersToModel(model, 1);
        verify(model, times(1)).addAttribute("users", mockedUserList.getUsersList());
        verify(model, times(1)).addAttribute("userSorting", expectedUserSorting);
    }

    /**
     * Tests that when the given user does not have a saved user sorting, then the addUsersToModel method adds all
     * users to the model as well as a default user sorting.
     */
    @Test
    void testAddUsersToModelWithoutUserSorting() {
        int id = 1;
        int expectedColumnIndex = 0;
        String expectedSortOrder = "asc";
        when(userAccountClientService.getAllUsers()).thenReturn(mockedUserList);
        when(userSortingService.getUserSortingById(id)).thenThrow(ObjectNotFoundException.class);
        Model model = mock(Model.class);
        elementService.addUsersToModel(model, id);
        verify(model, times(1)).addAttribute("users", mockedUserList.getUsersList());

        ArgumentCaptor<UserSorting> captor = ArgumentCaptor.forClass(UserSorting.class);
        verify(model, times(1)).addAttribute(eq("userSorting"), captor.capture());
        assertEquals(id, captor.getValue().getUserId());
        assertEquals(expectedColumnIndex, captor.getValue().getColumnIndex());
        assertEquals(expectedSortOrder, captor.getValue().getSortOrder());
    }
}