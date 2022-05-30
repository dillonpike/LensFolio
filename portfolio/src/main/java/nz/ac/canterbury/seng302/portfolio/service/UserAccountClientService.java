package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.stereotype.Service;

@Service
public class UserAccountClientService {

    @GrpcClient(value = "identity-provider-grpc-server")
    UserAccountServiceGrpc.UserAccountServiceBlockingStub userAccountStub;

    /**
     * Returns the user id from the given AuthState.
     * @param principal AutState to extract the user id from
     * @return user id from the given AuthState
     */
    public Integer getUserIDFromAuthState(AuthState principal) {
        return Integer.valueOf(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));
    }

    /**
     * Returns the user role from the given AuthState.
     * @param principal AutState to extract the user role from
     * @return user role from the given AuthState
     */
    public String getRoleFromAuthState(AuthState principal) {
        return principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("role"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");
    }

    /***
     * Method to retrieve all users from database
     * @return all users
     */
    public PaginatedUsersResponse getAllUsers() {
        GetPaginatedUsersRequest response = GetPaginatedUsersRequest.newBuilder()
                .build();
        return userAccountStub.getPaginatedUsers(response);
    }

    public UserRoleChangeResponse addRoleToUser(int userId, UserRole role) {
        ModifyRoleOfUserRequest response = ModifyRoleOfUserRequest.newBuilder()
                .setUserId(userId)
                .setRole(role)
                .build();
        return userAccountStub.addRoleToUser(response);
    }

    /**
     * Call removeRoleFromUser function in the IDP to delete the role from a user
     * @param role a UserRole object indicating the user role that will be deleted
     * @param userId an Integer indicating the user id of a user that a role will be deleted from
     * @return UserRoleChangeResponse which contains information whether deleting a role from user was done successfully in the idp
     */
    public UserRoleChangeResponse deleteRoleFromUser(int userId, UserRole role) {
        ModifyRoleOfUserRequest request = ModifyRoleOfUserRequest.newBuilder()
                .setUserId(userId)
                .setRole(role)
                .build();
        return userAccountStub.removeRoleFromUser(request);
    }
}
