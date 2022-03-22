package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.portfolio.authentication.CookieUtil;
import nz.ac.canterbury.seng302.shared.identityprovider.UserIDFromTokenRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserIDFromTokenResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserAccountService {

    @GrpcClient(value = "identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub userAccountStub;

    public UserIDFromTokenResponse getUserIDFromToken(String token) {
        UserIDFromTokenRequest response = UserIDFromTokenRequest.newBuilder().setToken(token).build();
        return userAccountStub.getUserIDFromToken(response);
    }

    public Integer getLoggedInUserID(HttpServletRequest request) {
        UserIDFromTokenResponse response = getUserIDFromToken(CookieUtil.getValue(request, "lens-session-token"));
        return response.getUserId();
    }
}
