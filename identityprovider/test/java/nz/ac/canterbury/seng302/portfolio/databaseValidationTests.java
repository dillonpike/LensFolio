package nz.ac.canterbury.seng302.portfolio;

    import org.junit.jupiter.api.Test;
    import org.springframework.stereotype.Controller;


    import static org.junit.jupiter.api.Assertions.assertEquals;
    import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the RegisterClientServer class.
 */
@Controller
public class databaseValidationTests {

  /**
   * Checks that the setUserData(final int userId, final String firstName,
   * final String middleName, final String lastName, final String email,
   * final String bio, final String nickname, final String personalPronouns)
   * Checks that an existing user is successfully updated
   */
  @Test
  public void givenValidUserInfo_whenUpdated_thenUpdateSuccessfully() {

  }

  /**
   * Checks that the setUserData(final int userId, final String firstName,
   * final String middleName, final String lastName, final String email,
   * final String bio, final String nickname, final String personalPronouns)
   * Checks that a non-existent user cannot be updated.
   */
  @Test
  public void givenInvalidUserInfo_whenUpdated_thenReturnErrorMessage() {

  }

  /**
   * Checking getUserData(int userId), checks that if a valid id is requested, the database returns the correct item
   */
  @Test
  public void givenDataInDatabase_whenValidIdIsRequested_thenOutputWithDatabaseValue() {

  }

  /**
   * Checking getUserData(int userId), checks that if an invalid id is requested, an error message is returned
   */
  @Test
  public void givenDataInDatabase_whenInvalidIdIsRequested_thenOutputWithErrorMessage() {

  }

  /**
   * Checking receiveConformation(final String username, final String password, final String firstName,
   * final String middleName, final String lastName, final String email)
   * Checks that if a new valid entry is inserted, the database inserts the correct item
   */
  @Test
  public void givenValidEntry_whenEntryInserted_thenOutputWithDatabaseValue() {

  }

  /**
   * Checking receiveConformation(final String username, final String password, final String firstName,
   * final String middleName, final String lastName, final String email)
   * Checks that if a new, invalid entry is inserted, an error message is returned and the entry is not inserted
   */
  @Test
  public void givenInvalidEntry_whenEntryInserted_thenOutputWithErrorMessage() {

  }

  /**
   * Checking receiveConformation(final String username, final String password, final String firstName,
   * final String middleName, final String lastName, final String email)
   * Checks that if a duplicated entry is inserted, an error message is returned and the entry is not inserted
   */
  @Test
  public void givenDuplicateEntry_whenEntryInserted_thenOutputWithErrorMessage() {

  }

}
