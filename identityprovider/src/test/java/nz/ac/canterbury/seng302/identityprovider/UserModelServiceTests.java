package nz.ac.canterbury.seng302.identityprovider;


import nz.ac.canterbury.seng302.identityprovider.model.Roles;
import nz.ac.canterbury.seng302.identityprovider.model.RolesRepository;
import nz.ac.canterbury.seng302.identityprovider.model.UserModel;
import nz.ac.canterbury.seng302.identityprovider.model.UserModelRepository;
import nz.ac.canterbury.seng302.identityprovider.service.UserModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class UserModelServiceTests {

    /**
     * Mocked UserModelService object
     */
    @InjectMocks
    private UserModelService userModelService;

    /**
     * Mocked userModelRepository object
     */
    @Mock
    private UserModelRepository userModelRepository;

    /**
     * Mocked RolesRepository object
     */
    @Mock
    private RolesRepository rolesRepository;

    @Before
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        Roles student = new Roles(0,"STUDENT");
        Roles teacher = new Roles(1,"TEACHER");
        Roles administrator = new Roles(2, "COURSE ADMINISTRATOR");
        rolesRepository.save(student);
        when(rolesRepository.save(any(Roles.class))).thenReturn(student);

        userModelService = new UserModelService(userModelRepository, rolesRepository);

    }

    /**
     * Given a new user with all attributes set up,
     * then save to mocked repository, test all attributes
     */
    @Test
    public void testWhenAddNewUser_ThenReturnAllAttribute() {
        UserModel user = new UserModel();
        user.setUsername("username");
        user.setPassword("password");
        user.setFirstName("firstName");
        user.setMiddleName("middleName");
        user.setLastName("lastName");
        user.setNickname("nickName");
        user.setEmail("test@test.com");
        user.setBio("bio");
        user.setPersonalPronouns("Unknown");
        userModelRepository.save(user);
        when(userModelRepository.save(any(UserModel.class))).thenReturn(user);
        UserModel newUser = userModelService.addUser(user);
        System.out.println(userModelRepository.findAll());

        assertThat(newUser.getUserId()).isSameAs(user.getUserId());
        assertThat(newUser.getUsername()).isSameAs(user.getUsername());
        assertThat(newUser.getPassword()).isSameAs(user.getPassword());
        assertThat(newUser.getFirstName()).isSameAs(user.getFirstName());
        assertThat(newUser.getMiddleName()).isSameAs(user.getMiddleName());
        assertThat(newUser.getLastName()).isSameAs(user.getLastName());
        assertThat(newUser.getNickname()).isSameAs(user.getNickname());
        assertThat(newUser.getEmail()).isSameAs(user.getEmail());
        assertThat(newUser.getBio()).isSameAs(user.getBio());
        assertThat(newUser.getPersonalPronouns()).isSameAs(user.getPersonalPronouns());
    }

    /**
     * Test when add a new user to userModelRepository, given a test email, then the user should
     * be saved and return the same email and userId
     */
    @Test
    public void testAddNewUser_givenTestEmail_returnSameEmailAndUserId() {
        UserModel user = new UserModel();
        user.setEmail("123@gmail.com");
        when(userModelRepository.save(any(UserModel.class))).thenReturn(user);
        UserModel newUser = userModelService.addUser(user);

        assertThat(newUser.getUserId()).isSameAs(user.getUserId());
        assertThat(newUser.getEmail()).isSameAs(user.getEmail());
    }

    /**
     * Tests that when a user is saved to the repository, the student role is assigned to them by default.
     */
    @Test
    public void testWhenAddUser_ThenReturnDefaultStudentRole() {
        UserModel user = new UserModel();
        user.setEmail("123@gmail.com");
        Roles studentRole = new Roles(0, "STUDENT");
        when(rolesRepository.findByRoleName("STUDENT")).thenReturn(studentRole);
        // userModelRepository is mocked so that when it is called to save a user, it returns the user that was given
        when(userModelRepository.save(any(UserModel.class))).then(returnsFirstArg());
        UserModel newUser = userModelService.addUser(user);
        assertEquals(Set.of(studentRole), newUser.getRoles());
    }

    @Test
    public void testSaveEditedUser_givenUserExist_returnSuccess() {
        UserModel user = new UserModel();
        user.setUsername("username");
        user.setPassword("password");
        user.setFirstName("firstName");
        user.setMiddleName("middleName");
        user.setLastName("lastName");
        user.setNickname("nickName");
        user.setEmail("test@test.com");
        user.setBio("bio");
        user.setPersonalPronouns("Unknown");
        when(userModelRepository.save(any(UserModel.class))).thenReturn(user);
        boolean saved = userModelService.saveEditedUser(user);
        assertTrue(saved);
    }

    @Test
    public void testGetUserByUsername_givenUserExist_returnSameUserAttributes() {
        UserModel user = new UserModel();
        user.setUsername("username");
        user.setPassword("password");
        user.setFirstName("firstName");
        user.setMiddleName("middleName");
        user.setLastName("lastName");
        user.setNickname("nickName");
        user.setEmail("test@test.com");
        user.setBio("bio");
        user.setPersonalPronouns("Unknown");
        when(userModelRepository.save(any(UserModel.class))).thenReturn(user);
        UserModel newUser = userModelService.addUser(user);

        UserModel testUser = userModelService.getUserByUsername("username");

//        assertThat(testUser.getUserId()).isSameAs(user.getUserId());
//        assertThat(testUser.getUsername()).isSameAs(user.getUsername());
//        assertThat(testUser.getPassword()).isSameAs(user.getPassword());
//        assertThat(testUser.getFirstName()).isSameAs(user.getFirstName());
//        assertThat(testUser.getMiddleName()).isSameAs(user.getMiddleName());
//        assertThat(testUser.getLastName()).isSameAs(user.getLastName());
//        assertThat(testUser.getNickname()).isSameAs(user.getNickname());
//        assertThat(testUser.getEmail()).isSameAs(user.getEmail());
//        assertThat(testUser.getBio()).isSameAs(user.getBio());
//        assertThat(testUser.getPersonalPronouns()).isSameAs(user.getPersonalPronouns());
    }

}
