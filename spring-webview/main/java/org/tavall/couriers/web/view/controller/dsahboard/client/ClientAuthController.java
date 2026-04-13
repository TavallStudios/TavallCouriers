package org.tavall.couriers.web.view.controller.dsahboard.client;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.tavall.couriers.api.web.endpoints.Routes;
import org.tavall.couriers.api.web.service.contract.CourierClientContractService;
import org.tavall.couriers.api.web.service.contract.CourierContractDraftService;
import org.tavall.couriers.api.web.service.user.UserAccountService;
import org.tavall.couriers.api.web.user.UserAccountEntity;
import org.tavall.couriers.api.web.user.permission.Role;
import org.tavall.couriers.web.security.oauth.ClientOAuth2LoginSuccessHandler;

import java.util.Set;

@Controller
public class ClientAuthController {

    private final Environment environment;
    private final UserAccountService userAccountService;
    private final CourierContractDraftService draftService;
    private final CourierClientContractService clientContractService;

    public ClientAuthController(Environment environment,
                                UserAccountService userAccountService,
                                CourierContractDraftService draftService,
                                CourierClientContractService clientContractService) {
        this.environment = environment;
        this.userAccountService = userAccountService;
        this.draftService = draftService;
        this.clientContractService = clientContractService;
    }

    @GetMapping("/dashboard/client/google/start")
    public String startGoogle(@RequestParam(value = "sessionKey", required = false) String sessionKey,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (!isGoogleConfigured()) {
            redirectAttributes.addAttribute("error", "Google sign-in is not configured on this environment.");
            return "redirect:" + Routes.dashboardLoginHome();
        }
        if (sessionKey != null && !sessionKey.isBlank()) {
            session.setAttribute(ClientOAuth2LoginSuccessHandler.DRAFT_SESSION_KEY, sessionKey.trim());
        }
        return "redirect:/oauth2/authorization/google";
    }

    @PostMapping(Routes.DASHBOARD_CLIENT_DEV_LOGIN)
    public String devLogin(@RequestParam(value = "sessionKey", required = false) String sessionKey,
                           @RequestParam(value = "contactEmail", required = false) String contactEmail,
                           @RequestParam(value = "contactName", required = false) String contactName,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        if (!isLocalBypassAllowed(request)) {
            redirectAttributes.addAttribute("error", "Client dev login is disabled.");
            return "redirect:" + Routes.dashboardLoginHome();
        }
        String username = firstNonBlank(contactEmail, contactName, "client-demo");
        UserAccountEntity user = userAccountService.getOrCreateFromExternalIdentity(
                "local-client:" + username.toLowerCase(),
                username,
                Set.of(Role.CLIENT)
        );
        authenticateClientSession(request.getSession(true), user);
        claimDraft(sessionKey, user);
        return "redirect:" + Routes.clientDashboard();
    }

    private void authenticateClientSession(HttpSession session, UserAccountEntity user) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                "N/A",
                Role.CLIENT.grantedAuthorities()
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    }

    private void claimDraft(String sessionKey, UserAccountEntity user) {
        if (sessionKey == null || sessionKey.isBlank() || user == null) {
            return;
        }
        var draft = draftService.getLatestDraft(sessionKey.trim());
        if (draft == null) {
            return;
        }
        draftService.markLinkedToClient(draft, user.getUserUUID());
        clientContractService.claimGeneratedDraft(draft, user);
    }

    private boolean isGoogleConfigured() {
        String clientId = environment.getProperty("spring.security.oauth2.client.registration.google.client-id");
        String clientSecret = environment.getProperty("spring.security.oauth2.client.registration.google.client-secret");
        return clientId != null && !clientId.isBlank() && clientSecret != null && !clientSecret.isBlank();
    }

    private boolean isLocalBypassAllowed(HttpServletRequest request) {
        boolean enabled = environment.getProperty("tavall.client.dev-login.enabled", Boolean.class, false);
        String remoteAddress = request.getRemoteAddr();
        boolean localRequest = "127.0.0.1".equals(remoteAddress) || "::1".equals(remoteAddress) || "0:0:0:0:0:0:0:1".equals(remoteAddress);
        return enabled && localRequest;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "client-demo";
    }
}
