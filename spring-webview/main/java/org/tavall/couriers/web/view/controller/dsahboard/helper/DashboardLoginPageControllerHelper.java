package org.tavall.couriers.web.view.controller.dsahboard.helper;

import org.springframework.core.env.Environment;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.tavall.couriers.api.web.endpoints.Routes;
import org.tavall.couriers.api.web.user.permission.Role;
import org.tavall.couriers.web.view.controller.dsahboard.model.DemoCredential;

import java.util.List;

@Component
public class DashboardLoginPageControllerHelper {

    private final Environment environment;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public DashboardLoginPageControllerHelper(Environment environment,
                                              ObjectProvider<ClientRegistrationRepository> clientRegistrationRepository) {
        this.environment = environment;
        this.clientRegistrationRepository = clientRegistrationRepository.getIfAvailable();
    }

    public String dashboardHome(Model model, Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:" + Routes.dashboardLoginHome();
        }

        if (hasRole(authentication, Role.CLIENT)) {
            return "redirect:" + Routes.clientDashboard();
        }
        if (hasRole(authentication, Role.SUPERUSER)) {
            return "redirect:" + Routes.adminDashboard();
        }
        if (hasRole(authentication, Role.MERCHANT)) {
            return "redirect:" + Routes.adminDashboard();
        }
        if (hasRole(authentication, Role.DRIVER)) {
            return "redirect:" + Routes.driverDashboard();
        }
        if (hasRole(authentication, Role.SUPPORT)) {
            return "redirect:" + Routes.adminDashboard();
        }

        return "redirect:" + Routes.home();
    }

    public String dashboardLoginRedirect(Model model, Authentication authentication) {
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return dashboardHome(model, authentication);
        }
        return dashboardLogin(model);
    }

    public String dashboardLogin(Model model) {
        model.addAttribute("title", "Dashboard Login");
        model.addAttribute("clientGoogleEnabled", clientRegistrationRepository != null);
        model.addAttribute("clientDevLoginEnabled", environment.getProperty("tavall.client.dev-login.enabled", Boolean.class, false));
        model.addAttribute("demoCredentials", List.of(
                new DemoCredential("Driver", "driver", "driver"),
                new DemoCredential("Admin", "merchant", "merchant"),
                new DemoCredential("Ops Root", "superuser", "superuser")
        ));
        return "dashboard/dashboard-login";
    }

    public String loginInternal() {
        return "dashboard/login";
    }

    private boolean hasRole(Authentication authentication, Role role) {
        return authentication.getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals(role.authority()));
    }
}
