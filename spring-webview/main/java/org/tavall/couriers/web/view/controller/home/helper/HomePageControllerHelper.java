package org.tavall.couriers.web.view.controller.home.helper;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.tavall.couriers.api.web.entities.contract.CourierContractTermDefinitionEntity;
import org.tavall.couriers.api.web.service.contract.CourierContractTemplateService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class HomePageControllerHelper {

    private final CourierContractTemplateService templateService;
    private final Environment environment;

    public HomePageControllerHelper(CourierContractTemplateService templateService, Environment environment) {
        this.templateService = templateService;
        this.environment = environment;
    }

    public void populateHomeModel(Model model) {
        List<CourierContractTermDefinitionEntity> activeTerms = templateService.getActiveTermDefinitions();
        model.addAttribute("title", "Tavall Couriers");
        model.addAttribute("activeTemplate", templateService.getActiveTemplate());
        model.addAttribute("activeTerms", activeTerms);
        model.addAttribute("termOptions", buildTermOptions(activeTerms));
        model.addAttribute("clientGoogleEnabled", isGoogleConfigured());
        model.addAttribute("clientDevLoginEnabled", environment.getProperty("tavall.client.dev-login.enabled", Boolean.class, false));
    }

    private Map<String, List<String>> buildTermOptions(List<CourierContractTermDefinitionEntity> activeTerms) {
        Map<String, List<String>> options = new LinkedHashMap<>();
        if (activeTerms == null) {
            return options;
        }
        for (CourierContractTermDefinitionEntity term : activeTerms) {
            if (term == null || term.getCode() == null) {
                continue;
            }
            String optionsText = term.getOptionsText();
            if (optionsText == null || optionsText.isBlank()) {
                options.put(term.getCode(), List.of());
                continue;
            }
            List<String> parsed = optionsText.lines()
                    .map(String::trim)
                    .filter(value -> !value.isBlank())
                    .toList();
            options.put(term.getCode(), parsed);
        }
        return options;
    }

    private boolean isGoogleConfigured() {
        String clientId = environment.getProperty("spring.security.oauth2.client.registration.google.client-id");
        String clientSecret = environment.getProperty("spring.security.oauth2.client.registration.google.client-secret");
        return clientId != null && !clientId.isBlank() && clientSecret != null && !clientSecret.isBlank();
    }
}
