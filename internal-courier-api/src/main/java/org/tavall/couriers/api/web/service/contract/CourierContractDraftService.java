package org.tavall.couriers.api.web.service.contract;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.tavall.couriers.api.web.contract.ContractLifecycleStatus;
import org.tavall.couriers.api.web.entities.contract.CourierContractDraftEntity;
import org.tavall.couriers.api.web.entities.contract.CourierContractTemplateEntity;
import org.tavall.couriers.api.web.entities.contract.CourierContractTermDefinitionEntity;
import org.tavall.couriers.api.web.repositories.contract.CourierContractDraftRepository;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CourierContractDraftService {

    private static final TypeReference<Map<String, String>> STRING_MAP = new TypeReference<>() {
    };

    private final CourierContractDraftRepository draftRepository;
    private final CourierContractTemplateService templateService;
    private final ObjectMapper objectMapper;

    public CourierContractDraftService(CourierContractDraftRepository draftRepository,
                                       CourierContractTemplateService templateService,
                                       ObjectMapper objectMapper) {
        this.draftRepository = draftRepository;
        this.templateService = templateService;
        this.objectMapper = objectMapper;
    }

    public CourierContractDraftEntity saveDraft(String sessionKey,
                                                Map<String, String> selections,
                                                String generatedContractHtml) {
        CourierContractDraftEntity draft = findOrCreateDraft(sessionKey);
        Map<String, String> normalizedSelections = normalizeSelections(selections);
        Instant now = Instant.now();

        draft.setContactName(normalizedSelections.get("contactName"));
        draft.setContactEmail(normalizedSelections.get("contactEmail"));
        draft.setCompanyName(normalizedSelections.get("companyName"));
        draft.setPhoneNumber(normalizedSelections.get("phoneNumber"));
        draft.setSelectedTermsJson(writeSelections(normalizedSelections));
        draft.setGeneratedContractHtml(blankToNull(generatedContractHtml));
        draft.setStatus(generatedContractHtml == null
                ? ContractLifecycleStatus.DRAFT.name()
                : ContractLifecycleStatus.GENERATED.name());
        draft.setUpdatedAt(now);
        if (draft.getCreatedAt() == null) {
            draft.setCreatedAt(now);
        }
        return draftRepository.save(draft);
    }

    public CourierContractDraftEntity generateDraft(String sessionKey, Map<String, String> selections) {
        Map<String, String> normalizedSelections = normalizeSelections(selections);
        CourierContractTemplateEntity template = templateService.getActiveTemplate();
        List<CourierContractTermDefinitionEntity> terms = templateService.getActiveTermDefinitions();
        String generatedHtml = renderDraftHtml(template, terms, normalizedSelections);
        return saveDraft(sessionKey, normalizedSelections, generatedHtml);
    }

    public CourierContractDraftEntity getLatestDraft(String sessionKey) {
        if (sessionKey == null || sessionKey.isBlank()) {
            return null;
        }
        return draftRepository.findTopBySessionKeyOrderByUpdatedAtDesc(sessionKey).orElse(null);
    }

    public CourierContractDraftEntity markLinkedToClient(CourierContractDraftEntity draft, UUID clientUserId) {
        if (draft == null || clientUserId == null) {
            return draft;
        }
        draft.setLinkedClientId(clientUserId);
        draft.setStatus(ContractLifecycleStatus.PENDING_CLIENT_APPROVAL.name());
        draft.setUpdatedAt(Instant.now());
        return draftRepository.save(draft);
    }

    public Map<String, String> readSelections(CourierContractDraftEntity draft) {
        if (draft == null || draft.getSelectedTermsJson() == null || draft.getSelectedTermsJson().isBlank()) {
            return Map.of();
        }
        try {
            Map<String, String> parsed = objectMapper.readValue(draft.getSelectedTermsJson(), STRING_MAP);
            return normalizeSelections(parsed);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to read contract draft selections.", ex);
        }
    }

    public String renderDraftHtml(CourierContractTemplateEntity template,
                                  List<CourierContractTermDefinitionEntity> terms,
                                  Map<String, String> selections) {
        StringBuilder html = new StringBuilder();
        html.append("<article class=\"contract-preview\">");
        html.append("<header class=\"contract-preview__header\">");
        html.append("<p class=\"contract-preview__eyebrow\">Generated Service Contract</p>");
        html.append("<h2>").append(escape(blankFallback(template != null ? template.getTemplateTitle() : null,
                "Medical Courier Services Agreement"))).append("</h2>");
        html.append("<p>").append(escape(blankFallback(template != null ? template.getIntroText() : null,
                "This generated agreement captures the service selections submitted through the Tavall Couriers intake flow."))).append("</p>");
        html.append("</header>");

        html.append("<section class=\"contract-preview__section\">");
        html.append("<h3>Client Profile</h3>");
        html.append("<ul>");
        appendListItem(html, "Company", selections.get("companyName"));
        appendListItem(html, "Primary Contact", selections.get("contactName"));
        appendListItem(html, "Email", selections.get("contactEmail"));
        appendListItem(html, "Phone", selections.get("phoneNumber"));
        html.append("</ul>");
        html.append("</section>");

        html.append("<section class=\"contract-preview__section\">");
        html.append("<h3>Operational Terms</h3>");
        html.append("<ul>");
        for (CourierContractTermDefinitionEntity term : terms) {
            if (term == null || term.getCode() == null) {
                continue;
            }
            String code = term.getCode();
            if (code.startsWith("contact") || "companyName".equals(code) || "phoneNumber".equals(code)) {
                continue;
            }
            appendListItem(html, term.getLabel(), selections.get(code));
        }
        html.append("</ul>");
        html.append("</section>");

        html.append("<section class=\"contract-preview__section\">");
        html.append("<h3>Operations and Pricing</h3>");
        html.append("<p>").append(escape(blankFallback(template != null ? template.getOperationsText() : null,
                "Operational execution is based on the selected service profile and remains subject to dispatch review."))).append("</p>");
        html.append("<p>").append(escape(blankFallback(template != null ? template.getPricingText() : null,
                "Pricing becomes effective only after internal activation."))).append("</p>");
        html.append("</section>");

        html.append("<section class=\"contract-preview__section\">");
        html.append("<h3>Binding Parties</h3>");
        html.append("<p><strong>Client:</strong> ________________________________</p>");
        html.append("<p><strong>Tavall Couriers:</strong> ");
        html.append(escape(blankFallback(template != null ? template.getBindingPartyName() : null, "________________"))).append("</p>");
        html.append("<p><strong>Tavall Address:</strong> ");
        html.append(escape(blankFallback(template != null ? template.getBindingPartyAddress() : null, "________________"))).append("</p>");
        html.append("</section>");

        html.append("<section class=\"contract-preview__section contract-preview__notice\">");
        html.append("<h3>Review Notice</h3>");
        html.append("<p>").append(escape(blankFallback(template != null ? template.getReviewNotice() : null,
                "Client approval records the requested service profile, but a human reviewer will confirm operational fit before service activation."))).append("</p>");
        html.append("</section>");
        html.append("</article>");
        return html.toString();
    }

    private CourierContractDraftEntity findOrCreateDraft(String sessionKey) {
        if (sessionKey == null || sessionKey.isBlank()) {
            throw new IllegalArgumentException("Session key is required.");
        }
        return draftRepository.findTopBySessionKeyOrderByUpdatedAtDesc(sessionKey)
                .orElseGet(() -> new CourierContractDraftEntity(
                        UUID.randomUUID(),
                        sessionKey.trim(),
                        null,
                        null,
                        null,
                        null,
                        "{}",
                        null,
                        ContractLifecycleStatus.DRAFT.name(),
                        null,
                        Instant.now(),
                        Instant.now()
                ));
    }

    private Map<String, String> normalizeSelections(Map<String, String> selections) {
        Map<String, String> normalized = new LinkedHashMap<>();
        if (selections == null) {
            return normalized;
        }
        selections.forEach((key, value) -> {
            if (key == null) {
                return;
            }
            String normalizedKey = key.trim();
            if (normalizedKey.isBlank()) {
                return;
            }
            String normalizedValue = blankToNull(value);
            if (normalizedValue != null) {
                normalized.put(normalizedKey, normalizedValue);
            }
        });
        return normalized;
    }

    private String writeSelections(Map<String, String> selections) {
        try {
            return objectMapper.writeValueAsString(selections == null ? Map.of() : selections);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to write contract draft selections.", ex);
        }
    }

    private void appendListItem(StringBuilder html, String label, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        html.append("<li><strong>")
                .append(escape(label))
                .append(":</strong> ")
                .append(escape(value))
                .append("</li>");
    }

    private String blankFallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
