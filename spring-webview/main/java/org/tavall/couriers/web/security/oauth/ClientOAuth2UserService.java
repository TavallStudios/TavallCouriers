package org.tavall.couriers.web.security.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.tavall.couriers.api.web.service.user.UserAccountService;
import org.tavall.couriers.api.web.user.permission.Role;

import java.util.Map;
import java.util.Set;

@Component
public class ClientOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final UserAccountService userAccountService;

    public ClientOAuth2UserService(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = delegate.loadUser(userRequest);
        Map<String, Object> attributes = user.getAttributes();
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String subject = buildSubject(registrationId, attributes);
        String username = resolveUsername(attributes, user.getName());
        userAccountService.getOrCreateFromExternalIdentity(subject, username, Set.of(Role.CLIENT));
        String nameAttributeKey = resolveNameAttributeKey(userRequest, attributes);
        return new DefaultOAuth2User(Role.CLIENT.grantedAuthorities(), attributes, nameAttributeKey);
    }

    private String buildSubject(String registrationId, Map<String, Object> attributes) {
        Object sub = attributes.get("sub");
        String providerSubject = sub != null ? sub.toString() : String.valueOf(attributes.getOrDefault("email", "unknown"));
        return registrationId + ":" + providerSubject;
    }

    private String resolveUsername(Map<String, Object> attributes, String fallback) {
        Object email = attributes.get("email");
        if (email != null && !email.toString().isBlank()) {
            return email.toString().trim();
        }
        Object name = attributes.get("name");
        if (name != null && !name.toString().isBlank()) {
            return name.toString().trim();
        }
        return fallback != null ? fallback : "client-user";
    }

    private String resolveNameAttributeKey(OAuth2UserRequest userRequest, Map<String, Object> attributes) {
        String configured = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        if (configured != null && !configured.isBlank() && attributes.containsKey(configured)) {
            return configured;
        }
        if (attributes.containsKey("email")) {
            return "email";
        }
        if (attributes.containsKey("sub")) {
            return "sub";
        }
        return attributes.keySet().stream().findFirst().orElse("sub");
    }
}
