package nz.ac.canterbury.seng302.identityprovider.service;


import com.fasterxml.jackson.databind.util.ArrayIterator;
import nz.ac.canterbury.seng302.identityprovider.model.GroupModel;
import nz.ac.canterbury.seng302.identityprovider.model.Roles;
import nz.ac.canterbury.seng302.identityprovider.repository.GroupRepository;
import nz.ac.canterbury.seng302.identityprovider.repository.RolesRepository;
import nz.ac.canterbury.seng302.identityprovider.model.UserModel;
import nz.ac.canterbury.seng302.identityprovider.repository.UserModelRepository;
import nz.ac.canterbury.seng302.identityprovider.server.GroupModelServerService;
import nz.ac.canterbury.seng302.identityprovider.server.UserAccountServerService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.naming.directory.InvalidAttributesException;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class UserModelServiceTests {

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

    @Mock
    private GroupRepository groupRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserAccountServerService.class);

    private final GroupModel testGroup = new GroupModel("Test", "Test Group", 1);

    private final Roles teacherRole = new Roles(1, "TEACHER");

    private UserModel testUser = new UserModel();

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        userModelService = new UserModelService(userModelRepository, rolesRepository, groupRepository);

        testUser.setUserId(1);
    }

    /**
     * Given a new user with all attributes set up,
     * then save to mocked repository, test all attributes
     */
    @Test
    void testWhenAddNewUser_ThenReturnAllAttribute() {
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
    void testAddNewUser_givenTestEmail_returnSameEmailAndUserId() {
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
    void testWhenAddUser_ThenReturnDefaultStudentRole() {
        UserModel user = new UserModel();
        Roles studentRole = new Roles(0, "STUDENT");
        when(rolesRepository.findByRoleName("STUDENT")).thenReturn(studentRole);
        // userModelRepository is mocked so that when it is called to save a user, it returns the user that was given
        when(userModelRepository.save(any(UserModel.class))).then(returnsFirstArg());
        UserModel newUser = userModelService.addUser(user);

        assertEquals(Set.of(studentRole), newUser.getRoles());
    }

    @Test
    void testSaveEditedUser_givenUserExist_returnSuccess() {
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
    void testGetUserByUsername_givenUserExist_returnSameUserAttributes() {
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
        when(userModelRepository.findByUsername(any(String.class))).thenReturn(List.of(user));
        UserModel testUser = userModelService.getUserByUsername("username");

        assertThat(user.getUserId()).isSameAs(testUser.getUserId());
        assertThat(user.getUsername()).isSameAs(testUser.getUsername());
        assertThat(user.getPassword()).isSameAs(testUser.getPassword());
        assertThat(user.getFirstName()).isSameAs(testUser.getFirstName());
        assertThat(user.getMiddleName()).isSameAs(testUser.getMiddleName());
        assertThat(user.getLastName()).isSameAs(testUser.getLastName());
        assertThat(user.getNickname()).isSameAs(testUser.getNickname());
        assertThat(user.getEmail()).isSameAs(testUser.getEmail());
        assertThat(user.getBio()).isSameAs(testUser.getBio());
        assertThat(user.getPersonalPronouns()).isSameAs(testUser.getPersonalPronouns());
    }

    @Test
    void testGetUserInformationByList_givenListOfUserIds_returnListOfUserResponse() {
        UserModel userModel1 = new UserModel("test1", "password", "test", "test", "test", "test", "test", "test", "test");
        UserModel userModel2 = new UserModel("test2", "password", "test2", "test2", "test2", "test", "test", "test", "test");
        UserModel userModel3 = new UserModel("test3", "password", "test3", "test3", "test2", "test", "test", "test", "test");
        userModel1.setUserId(1);
        userModel2.setUserId(2);
        userModel3.setUserId(3);

        UserResponse userResponse1 = UserResponse.newBuilder().setUsername("test1").setId(1).build();
        UserResponse userResponse2 = UserResponse.newBuilder().setUsername("test2").setId(2).build();
        UserResponse userResponse3 = UserResponse.newBuilder().setUsername("test3").setId(3).build();
        List<UserResponse> userResponseExpectedList = new ArrayList<>();
        userResponseExpectedList.add(userResponse1);
        userResponseExpectedList.add(userResponse2);
        userResponseExpectedList.add(userResponse3);


        Set<Integer> userIds = new HashSet<>();
        userIds.add(1);
        userIds.add(2);
        userIds.add(3);

        when(userModelRepository.findByUserId(userModel1.getUserId())).thenReturn(userModel1);
        when(userModelRepository.findByUserId(userModel2.getUserId())).thenReturn(userModel2);
        when(userModelRepository.findByUserId(userModel3.getUserId())).thenReturn(userModel3);


        List<UserResponse> userResponseList = userModelService.getUserInformationByList(userIds);
        assertThat(userResponseList.size()).isSameAs(userResponseExpectedList.size());

    }

    @Test
    void testCheckUserHasTeachersRole() {

        testUser.setRoles(new HashSet<>());

        when(rolesRepository.findByRoleName("TEACHER")).thenReturn(teacherRole);

        userModelService.checkUserHasTeacherRole(testUser);

        assertTrue(testUser.getRoles().contains(teacherRole));
    }



}
