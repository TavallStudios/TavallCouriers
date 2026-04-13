package org.tavall.couriers.api.web.service.contract;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.tavall.couriers.api.web.entities.contract.CourierContractTemplateEntity;
import org.tavall.couriers.api.web.entities.contract.CourierContractTermDefinitionEntity;
import org.tavall.couriers.api.web.repositories.contract.CourierContractTemplateRepository;
import org.tavall.couriers.api.web.repositories.contract.CourierContractTermDefinitionRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class CourierContractTemplateService {

    private final CourierContractTemplateRepository templateRepository;
    private final CourierContractTermDefinitionRepository termDefinitionRepository;

    public CourierContractTemplateService(CourierContractTemplateRepository templateRepository,
                                          CourierContractTermDefinitionRepository termDefinitionRepository) {
        this.templateRepository = templateRepository;
        this.termDefinitionRepository = termDefinitionRepository;
    }

    @PostConstruct
    public void seedDefaults() {
        if (templateRepository.count() == 0) {
            Instant now = Instant.now();
            templateRepository.save(new CourierContractTemplateEntity(
                    UUID.randomUUID(),
                    "medical-courier-standard",
                    "Medical Courier Services Agreement",
                    "This agreement outlines the courier relationship between Tavall Couriers and the client organization requesting specimen, medication, and time-sensitive parcel transport.",
                    "Tavall Couriers manages dispatch coordination, logistics review, and service coverage based on the client-selected operating profile.",
                    "Pricing and route commitments are generated from the submitted intake terms and become effective only after internal activation.",
                    "Client approval confirms the submitted terms. A human reviewer will still verify service alignment and contact the client before live scheduling begins.",
                    "",
                    "",
                    true,
                    now,
                    now
            ));
        }
        if (termDefinitionRepository.count() == 0) {
            Instant now = Instant.now();
            seedTerm("companyName", "Company Name", "text", "Profile", "Organization requesting service.", "", "", true, 10, now);
            seedTerm("contactName", "Primary Contact", "text", "Profile", "Operations or billing contact for onboarding.", "", "", true, 20, now);
            seedTerm("contactEmail", "Contact Email", "email", "Profile", "Used to claim the generated contract after sign-up.", "", "", true, 30, now);
            seedTerm("phoneNumber", "Phone Number", "text", "Profile", "Optional callback number for onboarding.", "", "", false, 40, now);
            seedTerm("parcelCount", "Parcel Volume", "select", "Operations", "Expected parcel count or daily specimen load.", "1-25\n26-75\n76-150\n150+", "1-25", true, 50, now);
            seedTerm("serviceRadiusMiles", "Service Radius (Miles)", "number", "Operations", "Maximum service radius from pickup origin.", "", "25", true, 60, now);
            seedTerm("serviceZone", "Service Zone", "text", "Operations", "Primary operational zone or delivery region.", "", "", true, 70, now);
            seedTerm("pickupZone", "Pickup Zone", "text", "Operations", "Pickup campus, region, or origin zone.", "", "", true, 80, now);
            seedTerm("serviceFrequency", "Service Frequency", "select", "Operations", "Expected pickup cadence.", "On demand\nDaily\nWeekdays only\nScheduled routes", "On demand", true, 90, now);
            seedTerm("deliveryWindow", "Delivery Window", "select", "Operations", "Operational timeline requirement.", "Standard same day\nRush within 4 hours\nSTAT within 2 hours\nNext day", "Standard same day", true, 100, now);
            seedTerm("temperatureControl", "Temperature Control", "select", "Compliance", "Handling requirements for medical material.", "Ambient only\nRefrigerated\nFrozen\nMixed handling", "Ambient only", true, 110, now);
            seedTerm("liabilityTerms", "Liability Terms", "select", "Compliance", "Preferred liability posture.", "Standard coverage\nEnhanced coverage required\nClient-provided chain-of-custody", "Standard coverage", true, 120, now);
            seedTerm("priceModel", "Price Model", "select", "Commercial", "Primary pricing structure.", "Per delivery\nPer mile\nDedicated route\nHybrid", "Per delivery", true, 130, now);
            seedTerm("priceDetails", "Price Details", "text", "Commercial", "Quoted pricing notes, minimums, or route estimates.", "", "", true, 140, now);
            seedTerm("recurringPickups", "Recurring Pickups", "select", "Commercial", "Whether scheduled recurring pickup support is requested.", "Allowed\nNot needed", "Allowed", true, 150, now);
        }
    }

    public CourierContractTemplateEntity getActiveTemplate() {
        return templateRepository.findFirstByActiveTrueOrderByUpdatedAtDesc()
                .orElseGet(() -> templateRepository.findAll().stream().findFirst().orElse(null));
    }

    public List<CourierContractTermDefinitionEntity> getActiveTermDefinitions() {
        return termDefinitionRepository.findAllByActiveTrueOrderByDisplayOrderAsc();
    }

    public List<CourierContractTermDefinitionEntity> getAllTermDefinitions() {
        return termDefinitionRepository.findAllByOrderByDisplayOrderAsc();
    }

    public CourierContractTemplateEntity updateActiveTemplate(String templateTitle,
                                                              String introText,
                                                              String operationsText,
                                                              String pricingText,
                                                              String reviewNotice,
                                                              String bindingPartyName,
                                                              String bindingPartyAddress) {
        CourierContractTemplateEntity template = getActiveTemplate();
        Instant now = Instant.now();
        if (template == null) {
            template = new CourierContractTemplateEntity(
                    UUID.randomUUID(),
                    "medical-courier-standard",
                    "Medical Courier Services Agreement",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    true,
                    now,
                    now
            );
        }
        template.setTemplateTitle(blankFallback(templateTitle, template.getTemplateTitle()));
        template.setIntroText(blankFallback(introText, template.getIntroText()));
        template.setOperationsText(blankFallback(operationsText, template.getOperationsText()));
        template.setPricingText(blankFallback(pricingText, template.getPricingText()));
        template.setReviewNotice(blankFallback(reviewNotice, template.getReviewNotice()));
        template.setBindingPartyName(normalize(bindingPartyName));
        template.setBindingPartyAddress(normalize(bindingPartyAddress));
        template.setActive(true);
        template.setUpdatedAt(now);
        if (template.getCreatedAt() == null) {
            template.setCreatedAt(now);
        }
        return templateRepository.save(template);
    }

    public CourierContractTermDefinitionEntity saveTermDefinition(String code,
                                                                  String label,
                                                                  String inputType,
                                                                  String category,
                                                                  String helpText,
                                                                  String optionsText,
                                                                  String defaultValue,
                                                                  boolean required,
                                                                  int displayOrder,
                                                                  boolean active) {
        String normalizedCode = normalize(code);
        if (normalizedCode == null) {
            throw new IllegalArgumentException("Term code is required.");
        }
        Instant now = Instant.now();
        CourierContractTermDefinitionEntity entity = termDefinitionRepository.findByCodeIgnoreCase(normalizedCode)
                .orElseGet(() -> new CourierContractTermDefinitionEntity(
                        UUID.randomUUID(),
                        normalizedCode,
                        "",
                        "text",
                        "Operations",
                        "",
                        "",
                        "",
                        false,
                        true,
                        displayOrder > 0 ? displayOrder : 999,
                        now,
                        now
                ));
        entity.setCode(normalizedCode);
        entity.setLabel(blankFallback(label, entity.getLabel()));
        entity.setInputType(blankFallback(inputType, entity.getInputType()));
        entity.setCategory(blankFallback(category, entity.getCategory()));
        entity.setHelpText(normalize(helpText));
        entity.setOptionsText(normalize(optionsText));
        entity.setDefaultValue(normalize(defaultValue));
        entity.setRequired(required);
        entity.setActive(active);
        entity.setDisplayOrder(displayOrder > 0 ? displayOrder : entity.getDisplayOrder());
        entity.setUpdatedAt(now);
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(now);
        }
        return termDefinitionRepository.save(entity);
    }

    private void seedTerm(String code,
                          String label,
                          String inputType,
                          String category,
                          String helpText,
                          String optionsText,
                          String defaultValue,
                          boolean required,
                          int displayOrder,
                          Instant now) {
        termDefinitionRepository.save(new CourierContractTermDefinitionEntity(
                UUID.randomUUID(),
                code,
                label,
                inputType,
                category,
                helpText,
                optionsText,
                defaultValue,
                required,
                true,
                displayOrder,
                now,
                now
        ));
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private String blankFallback(String incoming, String fallback) {
        String normalized = normalize(incoming);
        return normalized != null ? normalized : fallback;
    }
}
