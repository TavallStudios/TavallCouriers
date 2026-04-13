package org.tavall.couriers.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.tavall.couriers.api.web.endpoints.dashboard.DefaultDashboardEndpoints;
import org.tavall.couriers.api.web.user.permission.Role;
import org.tavall.couriers.web.security.oauth.ClientOAuth2LoginSuccessHandler;
import org.tavall.couriers.web.security.oauth.ClientOAuth2UserService;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   ObjectProvider<ClientRegistrationRepository> clientRegistrationRepository,
                                                   ClientOAuth2UserService clientOAuth2UserService,
                                                   ClientOAuth2LoginSuccessHandler clientOAuth2LoginSuccessHandler) throws Exception {

        http
                // Allow public entry pages; protect dashboards and redirect unauth users home.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",                      // public home, splash style
                                "/dashboard",             // dashboard entry (guest redirects to login)
                                "/dashboard/home",        // alias entry
                                "/dashboard/login",       // GET login page
                                "/dashboard/client/dev-login",
                                "/dashboard/client/google/start",
                                "/oauth2/**",
                                "/api/client/contracts/**",
                                "/tracking",              // public tracking entry
                                "/tracking/**",           // public tracking detail
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()
                        .requestMatchers("/dashboard/client/**").hasRole("CLIENT")
                        .requestMatchers("/dashboard/admin/**").hasAnyRole("MERCHANT", "SUPERUSER", "SUPPORT")
                        .requestMatchers("/purchase").hasAnyRole("MERCHANT", "DRIVER", "SUPERUSER")
                        .anyRequest().authenticated()
                )

                // Your custom thymeleaf login page + processing endpoint
                .formLogin(form -> form
                        .loginPage("/dashboard/login")          // GET shows your template
                        .loginProcessingUrl("/dashboard/login") // POST submits here (your form already does this)
                        .defaultSuccessUrl("/dashboard", true)  // where to go after login
                        .failureUrl("/dashboard/login?error=true")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl(DefaultDashboardEndpoints.DASHBOARD_LOGOUT_PATH)
                        .logoutSuccessUrl("/dashboard/login?logout=true")
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            String path = request.getRequestURI();
                            if (path != null && path.startsWith("/dashboard")) {
                                response.sendRedirect("/dashboard/login");
                            } else {
                                response.sendRedirect("/");
                            }
                        })
                )

                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )

                // Make unauthenticated visitors exist but have NO roles
                .anonymous(anon -> anon
                        .principal("guest")
                )

                // keep defaults, but allow camera frame uploads
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/internal/api/v1/stream/frame")
                );

        if (clientRegistrationRepository.getIfAvailable() != null) {
            http.oauth2Login(oauth -> oauth
                    .loginPage("/dashboard/login")
                    .userInfoEndpoint(userInfo -> userInfo.userService(clientOAuth2UserService))
                    .successHandler(clientOAuth2LoginSuccessHandler)
                    .failureUrl("/dashboard/login?oauthError=true")
            );
        }

        return http.build();
    }
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder, Environment env) {
        UserDetails driver = buildUser("driver", Role.DRIVER, encoder, env,
                "security.driver.password",
                "demo.credentials.driver.password",
                "SPRING_SECURITY_USER_PASSWORD");

        UserDetails merchant = buildUser("merchant", Role.MERCHANT, encoder, env,
                "security.merchant.password",
                "demo.credentials.merchant.password");

        UserDetails superuser = buildUser("superuser", Role.SUPERUSER, encoder, env,
                "security.superuser.password",
                "demo.credentials.superuser.password",
                "SPRING_SECURITY_USER_PASSWORD");

        UserDetails user = buildUser("user", Role.USER, encoder, env,
                "security.user.password",
                "demo.credentials.user.password");

        return new InMemoryUserDetailsManager(driver, merchant, superuser, user);
    }

    private UserDetails buildUser(String username,
                                  Role role,
                                  PasswordEncoder encoder,
                                  Environment env,
                                  String... passwordKeys) {
        String password = resolveValue(env, username, passwordKeys);
        return User.withUsername(username)
                .password(encoder.encode(password))
                .authorities(role.grantedAuthorities())
                .build();
    }

    private String resolveValue(Environment env, String defaultValue, String... keys) {
        for (String key : keys) {
            String candidate = env.getProperty(key);
            if (candidate != null && !candidate.isBlank()) {
                return candidate;
            }
        }
        return defaultValue;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
