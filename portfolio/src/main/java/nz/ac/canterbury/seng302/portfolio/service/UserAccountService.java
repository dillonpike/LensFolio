package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.portfolio.authentication.CookieUtil;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserAccountService {

    public Integer getUserIDFromAuthState(AuthState principal) {
        return Integer.valueOf(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));
    }

    public String getRoleFromAuthState(AuthState principal) {
        return principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("role"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");
    }
}
