package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.ModifyRoleOfUserRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRoleChangeResponse;
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
    void callDeleteRoleFromUSerMethod_expectRoleFromUserMethodTobeCalled() {
        UserRoleChangeResponse response = UserRoleChangeResponse.newBuilder().setIsSuccess(true).build();
        ModifyRoleOfUserRequest req = ModifyRoleOfUserRequest.newBuilder().setUserId(1).setRole(UserRole.STUDENT).build();
        when(userAccountServiceBlockingStub.removeRoleFromUser(req)).thenReturn(response);
        UserRoleChangeResponse actual = userAccountClientService.deleteRoleFromUser(1,UserRole.STUDENT);
        Assertions.assertEquals(true, actual.getIsSuccess());
        Mockito.verify(userAccountServiceBlockingStub).removeRoleFromUser(req);
    }
}