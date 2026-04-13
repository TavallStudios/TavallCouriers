package org.tavall.couriers.web.security;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.tavall.couriers.api.console.Log;
import org.tavall.couriers.api.web.service.user.UserAccountService;
import org.tavall.couriers.api.web.user.permission.Role;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

@Component
public class UserSessionListener {

    private final UserAccountService userAccountService;

    public UserSessionListener(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        if (event == null) {
            return;
        }
        Authentication authentication = event.getAuthentication();
        if (authentication == null
                || authentication instanceof AnonymousAuthenticationToken
                || !authentication.isAuthenticated()) {
            return;
        }
        String username = authentication.getName();
        if (username == null || username.isBlank()) {
            return;
        }
        String subject = resolveSubject(authentication, username);
        Set<Role> roles = resolveRoles(authentication);
        userAccountService.getOrCreateFromExternalIdentity(subject, username, roles);
        Log.info("User login cached: " + username);
    }

    @EventListener
    public void onLogoutSuccess(LogoutSuccessEvent event) {
        if (event == null) {
            return;
        }
        Authentication authentication = event.getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return;
        }
        String username = authentication.getName();
        userAccountService.flushUser(username);
        Log.info("User logout flushed: " + username);
    }

    private String resolveSubject(Authentication authentication, String username) {
        if (authentication instanceof OAuth2AuthenticationToken oauthToken
                && oauthToken.getPrincipal() instanceof OAuth2User oauth2User) {
            Object sub = oauth2User.getAttributes().get("sub");
            if (sub != null) {
                return oauthToken.getAuthorizedClientRegistrationId() + ":" + sub;
            }
            return oauthToken.getAuthorizedClientRegistrationId() + ":" + username.toLowerCase(Locale.ROOT);
        }
        if (resolveRoles(authentication).contains(Role.CLIENT)) {
            return "local-client:" + username.toLowerCase(Locale.ROOT);
        }
        return "local:" + username.toLowerCase(Locale.ROOT);
    }

    private Set<Role> resolveRoles(Authentication authentication) {
        Set<Role> roles = EnumSet.noneOf(Role.class);
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority == null || authority.getAuthority() == null) {
                continue;
            }
            String raw = authority.getAuthority();
            if (!raw.startsWith(Role.PREFIX)) {
                continue;
            }
            try {
                roles.add(Role.valueOf(raw.substring(Role.PREFIX.length())));
            } catch (IllegalArgumentException ignored) {
                // Ignore unsupported authorities.
            }
        }
        return roles;
    }
}
