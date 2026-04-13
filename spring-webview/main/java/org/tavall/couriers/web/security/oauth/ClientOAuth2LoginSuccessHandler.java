package org.tavall.couriers.web.security.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.tavall.couriers.api.web.endpoints.Routes;
import org.tavall.couriers.api.web.service.contract.CourierClientContractService;
import org.tavall.couriers.api.web.service.contract.CourierContractDraftService;
import org.tavall.couriers.api.web.service.user.UserAccountService;
import org.tavall.couriers.api.web.user.UserAccountEntity;

import java.io.IOException;

@Component
public class ClientOAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    public static final String DRAFT_SESSION_KEY = "clientDraftSessionKey";

    private final UserAccountService userAccountService;
    private final CourierContractDraftService draftService;
    private final CourierClientContractService clientContractService;

    public ClientOAuth2LoginSuccessHandler(UserAccountService userAccountService,
                                           CourierContractDraftService draftService,
                                           CourierClientContractService clientContractService) {
        this.userAccountService = userAccountService;
        this.draftService = draftService;
        this.clientContractService = clientContractService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (authentication != null && authentication.getName() != null) {
            UserAccountEntity user = userAccountService.findByUsername(authentication.getName());
            String sessionKey = request.getSession().getAttribute(DRAFT_SESSION_KEY) instanceof String key ? key : null;
            if (user != null && sessionKey != null && !sessionKey.isBlank()) {
                var draft = draftService.getLatestDraft(sessionKey);
                if (draft != null) {
                    draftService.markLinkedToClient(draft, user.getUserUUID());
                    clientContractService.claimGeneratedDraft(draft, user);
                }
            }
            request.getSession().removeAttribute(DRAFT_SESSION_KEY);
        }
        response.sendRedirect(Routes.dashboard());
    }
}
