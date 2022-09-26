package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserAccountClientServiceTest {

    /**
     * The class that we want to test in this case UserAccountClientService Class
     */
    @Autowired
    private static UserAccountClientService userAccountClientService = new UserAccountClientService();

    /**
     * The mocked stub so that we can mock the grpc responses
     */
    @Autowired
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub userAccountServiceBlockingStub = mock(UserAccountServiceGrpc.UserAccountServiceBlockingStub.class);

    /**
     * Setup to replace the autowired instances of these with the mocks
     */
    @BeforeEach
    void setup() {
        userAccountClientService.userAccountStub = userAccountServiceBlockingStub;
    }

    /**
     * Test to check delete role from user method in User Account Client Service
     * Expect that removeRoleFromUSer() method to be called
     */
    @Test
    void callDeleteRoleFromUserMethod_expectRoleFromUserMethodTobeCalled() {
        UserRoleChangeResponse response = UserRoleChangeResponse.newBuilder().setIsSuccess(true).build();
        ModifyRoleOfUserRequest req = ModifyRoleOfUserRequest.newBuilder().setUserId(1).setRole(UserRole.STUDENT).build();
        when(userAccountServiceBlockingStub.removeRoleFromUser(req)).thenReturn(response);
        UserRoleChangeResponse actual = userAccountClientService.deleteRoleFromUser(1,UserRole.STUDENT);
        assertTrue(actual.getIsSuccess());
        Mockito.verify(userAccountServiceBlockingStub).removeRoleFromUser(req);
    }

    /***
     * Test to check the addRoleToUser method in User account client service
     * Expect addRoleToUser method is called
     */
    @Test
    void callAddRoleFromUserMethod_expectRoleFromUserMethodTobeCalled() {
        UserRoleChangeResponse response = UserRoleChangeResponse.newBuilder().setIsSuccess(true).build();
        ModifyRoleOfUserRequest req = ModifyRoleOfUserRequest.newBuilder().setUserId(1).setRole(UserRole.STUDENT).build();
        when(userAccountServiceBlockingStub.addRoleToUser(req)).thenReturn(response);
        UserRoleChangeResponse actual = userAccountClientService.addRoleToUser(1,UserRole.STUDENT);
        assertTrue(actual.getIsSuccess());
        Mockito.verify(userAccountServiceBlockingStub).addRoleToUser(req);
    }

    /**
     * Tests that the getStudentUsers method returns only users with the student role.
     */
    @Test
    void callGetStudentUsers_expectStudentUsersToBeReturned() {
        PaginatedUsersResponse response = PaginatedUsersResponse.newBuilder()
                .addUsers(UserResponse.newBuilder().setId(0).addRoles(UserRole.STUDENT))
                .addUsers(UserResponse.newBuilder().setId(1).addRoles(UserRole.TEACHER))
                .addUsers(UserResponse.newBuilder().setId(2).addRoles(UserRole.COURSE_ADMINISTRATOR))
                .addUsers(UserResponse.newBuilder().setId(3).addRoles(UserRole.COURSE_ADMINISTRATOR).addRoles(UserRole.STUDENT))
                .build();
        when(userAccountServiceBlockingStub.getPaginatedUsers(any(GetPaginatedUsersRequest.class))).thenReturn(response);
        List<Integer> actualStudentIds = userAccountClientService.getStudentUsers().stream().map(UserResponse::getId).toList();
        List<Integer> expectedStudentIds = List.of(0, 3);
        assertEquals(expectedStudentIds, actualStudentIds);
    }
}