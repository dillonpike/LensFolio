package nz.ac.canterbury.seng302.identityprovider;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import nz.ac.canterbury.seng302.identityprovider.service.RegisterServerService;
import nz.ac.canterbury.seng302.identityprovider.service.UserModelService;
import nz.ac.canterbury.seng302.identityprovider.model.UserModel;

@SpringBootTest
class IdentityproviderDatabaseTests {

    /**
     * editUserAccount(UserModel user)
     * Checks that an existing user is successfully updated
     */
    @Test
    public void givenValidUserInfo_whenUpdated_thenUpdateSuccessfully() {

    }

    /**
     * editUserAccount(UserModel user) - user doesn't exist
     * Checks that a non-existent user cannot be updated.
     */
    @Test
    public void givenInvalidUserInfo_whenUpdated_thenReturnErrorMessage() {

    }

    /**
     * Checking getUserById(int userId), checks that if a valid id is requested, the database returns the correct item
     */
    @Test
    public void givenDataInDatabase_whenValidIdIsRequested_thenOutputWithDatabaseValue() {

    }

    /**
     * Checking getUserByUsername(string username), checks that if an invalid id is requested, an error message is returned
     */
    @Test
    public void givenDataInDatabase_whenInvalidIdIsRequested_thenOutputWithErrorMessage() {

    }

    /**
     * Checking addUser(UserModel user)
     * Checks that if a new valid entry is inserted, the database inserts the correct item
     */
    @Test
    public void givenValidEntry_whenEntryInserted_thenOutputWithDatabaseValue() {

    }

    /**
     * Checking addUser(UserModel user)
     * Checks that if a new, invalid entry is inserted, an error message is returned and the entry is not inserted
     */
    @Test
    public void givenInvalidEntry_whenEntryInserted_thenOutputWithErrorMessage() {

    }

    /**
     * Checking addUser(UserModel user)
     * Checks that if a duplicated entry is inserted, an error message is returned and the entry is not inserted
     */
    @Test
    public void givenDuplicateEntry_whenEntryInserted_thenOutputWithErrorMessage() {

    }

}
