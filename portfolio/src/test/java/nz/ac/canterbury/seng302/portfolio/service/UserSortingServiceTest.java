package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.UserSorting;
import nz.ac.canterbury.seng302.portfolio.model.UserSortingRepository;
import org.apache.catalina.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

/**
 * Unit tests for UserSortingService class.
 */
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class UserSortingServiceTest {

    @Mock
    private UserSortingRepository repository;

    /**
     * UserSortingService object.
     */
    @InjectMocks
    private UserSortingService userSortingService = new UserSortingService();

    /**
     * UserSorting object used in tests.
     */
    private final UserSorting expectedUserSorting = new UserSorting(1);

    /**
     * Message in exception that should be thrown when a UserSorting object can't be found with the given id.
     */
    private final String expectedFindExceptionMessage = "UserSorting not found";

    /**
     * Given that there are no UserSorting objects stored in the database, then the updateUserSorting method should
     * save and return the given sprint.
     */
    @Test
    public void givenNoStoredUserSortings_whenUpdateUserSorting_thenReturnGivenUserSorting() {
        given(repository.findById(any(Integer.class))).willReturn(Optional.empty());
        when(repository.save(any(UserSorting.class))).thenReturn(expectedUserSorting);
        UserSorting userSorting = userSortingService.updateUserSorting(expectedUserSorting);
        assertEquals(expectedUserSorting, userSorting);
    }

    /**
     * Given that there are UserSorting objects stored in the database, then the updateUserSorting method should
     * save and return the given sprint.
     */
    @Test
    public void givenStoredUserSortings_whenUpdateUserSorting_thenReturnGivenUserSorting() {
        given(repository.findById(any(Integer.class))).willReturn(Optional.of(expectedUserSorting));
        when(repository.save(any(UserSorting.class))).thenReturn(expectedUserSorting);
        UserSorting userSorting = userSortingService.updateUserSorting(expectedUserSorting);
        assertEquals(expectedUserSorting, userSorting);
    }


    /**
     * Given that there are no UserSorting objects stored in the database, then the getUserSortingById method should
     * throw an Exception.
     */
    @Test
    public void givenNoStoredUserSortings_whenGetUserSortingById_thenThrowException() {
        given(repository.findById(any(Integer.class))).willReturn(Optional.empty());
        try {
            userSortingService.getUserSortingById(1);
            fail();
        } catch (Exception exception) {
            assertEquals(expectedFindExceptionMessage, exception.getMessage());
        }
    }

    /**
     * Given that there are UserSorting objects stored in the database, then the getUserSortingById method should
     * return the expected UserSorting object.
     */
    @Test
    public void givenStoredUserSortings_whenGetUserSortingById_thenReturnExpectedUserSorting() {
        given(repository.findById(any(Integer.class))).willReturn(Optional.of(expectedUserSorting));
        try {
            UserSorting userSorting = userSortingService.getUserSortingById(1);
            assertEquals(expectedUserSorting, userSorting);
        } catch (Exception e) {
            fail();
        }
    }
}
