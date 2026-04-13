package org.tavall.couriers.web.view.controller.home;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tavall.couriers.api.web.service.contract.CourierContractDraftService;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/client/contracts")
public class ClientContractFlowController {

    private final CourierContractDraftService draftService;

    public ClientContractFlowController(CourierContractDraftService draftService) {
        this.draftService = draftService;
    }

    @PostMapping("/draft")
    public ResponseEntity<Map<String, Object>> saveDraft(@RequestBody Map<String, String> payload) {
        String sessionKey = readRequired(payload, "sessionKey");
        Map<String, String> selections = new LinkedHashMap<>(payload);
        selections.remove("sessionKey");
        var draft = draftService.saveDraft(sessionKey, selections, null);
        return ResponseEntity.ok(Map.of(
                "draftId", draft.getId().toString(),
                "status", draft.getStatus()
        ));
    }

    @PostMapping("/draft/generate")
    public ResponseEntity<Map<String, Object>> generateDraft(@RequestBody Map<String, String> payload) {
        String sessionKey = readRequired(payload, "sessionKey");
        Map<String, String> selections = new LinkedHashMap<>(payload);
        selections.remove("sessionKey");
        var draft = draftService.generateDraft(sessionKey, selections);
        return ResponseEntity.ok(Map.of(
                "draftId", draft.getId().toString(),
                "status", draft.getStatus(),
                "generatedContractHtml", draft.getGeneratedContractHtml()
        ));
    }

    private String readRequired(Map<String, String> payload, String key) {
        String value = payload != null ? payload.get(key) : null;
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(key + " is required.");
        }
        return value.trim();
    }
}
