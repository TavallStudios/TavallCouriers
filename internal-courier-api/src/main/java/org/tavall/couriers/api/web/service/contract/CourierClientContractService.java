package org.tavall.couriers.api.web.service.contract;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.tavall.couriers.api.web.contract.ContractLifecycleStatus;
import org.tavall.couriers.api.web.entities.contract.CourierClientContractEntity;
import org.tavall.couriers.api.web.entities.contract.CourierContractDraftEntity;
import org.tavall.couriers.api.web.entities.contract.CourierContractStatusHistoryEntity;
import org.tavall.couriers.api.web.entities.contract.CourierContractTemplateEntity;
import org.tavall.couriers.api.web.entities.contract.CourierContractTermDefinitionEntity;
import org.tavall.couriers.api.web.repositories.contract.CourierClientContractRepository;
import org.tavall.couriers.api.web.repositories.contract.CourierContractStatusHistoryRepository;
import org.tavall.couriers.api.web.user.UserAccountEntity;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CourierClientContractService {

    private static final TypeReference<Map<String, String>> STRING_MAP = new TypeReference<>() {
    };

    private final CourierClientContractRepository contractRepository;
    private final CourierContractStatusHistoryRepository statusHistoryRepository;
    private final CourierContractTemplateService templateService;
    private final CourierContractDraftService draftService;
    private final ObjectMapper objectMapper;

    public CourierClientContractService(CourierClientContractRepository contractRepository,
                                        CourierContractStatusHistoryRepository statusHistoryRepository,
                                        CourierContractTemplateService templateService,
                                        CourierContractDraftService draftService,
                                        ObjectMapper objectMapper) {
        this.contractRepository = contractRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.templateService = templateService;
        this.draftService = draftService;
        this.objectMapper = objectMapper;
    }

    public CourierClientContractEntity claimGeneratedDraft(CourierContractDraftEntity draft, UserAccountEntity clientUser) {
        if (draft == null || clientUser == null || clientUser.getUserUUID() == null) {
            return null;
        }
        CourierClientContractEntity existing = draft.getId() != null
                ? contractRepository.findFirstByDraftId(draft.getId()).orElse(null)
                : null;
        if (existing != null) {
            if (!clientUser.getUserUUID().equals(existing.getClientUserId())) {
                existing.setClientUserId(clientUser.getUserUUID());
                existing.setUpdatedAt(Instant.now());
                contractRepository.save(existing);
            }
            return existing;
        }

        Map<String, String> selections = draftService.readSelections(draft);
        CourierContractTemplateEntity template = templateService.getActiveTemplate();
        List<CourierContractTermDefinitionEntity> terms = templateService.getActiveTermDefinitions();
        Instant now = Instant.now();
        CourierClientContractEntity contract = new CourierClientContractEntity(
                UUID.randomUUID(),
                draft.getId(),
                clientUser.getUserUUID(),
                template != null ? template.getId() : null,
                template != null ? template.getTemplateTitle() : "Medical Courier Services Agreement",
                firstNonBlank(draft.getContactName(), clientUser.getUsername()),
                draft.getCompanyName(),
                draft.getContactEmail(),
                writeSelections(selections),
                firstNonBlank(draft.getGeneratedContractHtml(), draftService.renderDraftHtml(template, terms, selections)),
                ContractLifecycleStatus.PENDING_CLIENT_APPROVAL.name(),
                selections.get("parcelCount"),
                parseInteger(selections.get("serviceRadiusMiles")),
                selections.get("serviceZone"),
                selections.get("pickupZone"),
                selections.get("priceDetails"),
                null,
                null,
                null,
                null,
                null,
                null,
                now,
                now
        );
        CourierClientContractEntity saved = contractRepository.save(contract);
        recordStatus(saved.getId(), ContractLifecycleStatus.PENDING_CLIENT_APPROVAL, "Draft linked to client account.", clientUser.getUsername());
        return saved;
    }

    public List<CourierClientContractEntity> getContractsForClient(UUID clientUserId) {
        if (clientUserId == null) {
            return List.of();
        }
        return contractRepository.findAllByClientUserIdOrderByUpdatedAtDesc(clientUserId);
    }

    public CourierClientContractEntity findContractForClient(UUID clientUserId, UUID contractId) {
        if (clientUserId == null || contractId == null) {
            return null;
        }
        return contractRepository.findByIdAndClientUserId(contractId, clientUserId).orElse(null);
    }

    public CourierClientContractEntity approveContract(UUID clientUserId,
                                                       UUID contractId,
                                                       String signerName,
                                                       String ipAddress) {
        CourierClientContractEntity contract = findContractForClient(clientUserId, contractId);
        if (contract == null) {
            throw new IllegalArgumentException("Contract not found.");
        }
        if (!ContractLifecycleStatus.PENDING_CLIENT_APPROVAL.name().equals(contract.getStatus())) {
            throw new IllegalStateException("Contract is not awaiting client approval.");
        }
        String normalizedSigner = blankToNull(signerName);
        if (normalizedSigner == null) {
            throw new IllegalArgumentException("Signer name is required.");
        }
        Instant now = Instant.now();
        contract.setSignedByName(normalizedSigner);
        contract.setApprovalIpAddress(blankToNull(ipAddress));
        contract.setSignedAt(now);
        contract.setApprovedAt(now);
        contract.setStatus(ContractLifecycleStatus.PENDING_INTERNAL_REVIEW.name());
        contract.setUpdatedAt(now);
        CourierClientContractEntity saved = contractRepository.save(contract);
        recordStatus(saved.getId(), ContractLifecycleStatus.PENDING_INTERNAL_REVIEW,
                "Client approved contract terms.", normalizedSigner);
        return saved;
    }

    public CourierClientContractEntity activateContract(UUID contractId, String actorName, String reviewNotes) {
        return updateAdminStatus(contractId, ContractLifecycleStatus.ACTIVE, actorName, reviewNotes);
    }

    public CourierClientContractEntity rejectContract(UUID contractId, String actorName, String reviewNotes) {
        return updateAdminStatus(contractId, ContractLifecycleStatus.REJECTED, actorName, reviewNotes);
    }

    public List<CourierClientContractEntity> getContractsPendingReview() {
        return contractRepository.findAllByStatusOrderByUpdatedAtDesc(ContractLifecycleStatus.PENDING_INTERNAL_REVIEW.name());
    }

    public CourierClientContractEntity findContract(UUID contractId) {
        if (contractId == null) {
            return null;
        }
        return contractRepository.findById(contractId).orElse(null);
    }

    public List<CourierContractStatusHistoryEntity> getStatusHistory(UUID contractId) {
        if (contractId == null) {
            return List.of();
        }
        return statusHistoryRepository.findAllByContractIdOrderByCreatedAtAsc(contractId);
    }

    public Map<String, String> readSelections(CourierClientContractEntity contract) {
        return contract == null ? Map.of() : readSelections(contract.getSelectedTermsJson());
    }

    public boolean allowsRecurringPickups(CourierClientContractEntity contract) {
        String recurringValue = readSelections(contract).get("recurringPickups");
        return recurringValue == null || recurringValue.equalsIgnoreCase("Allowed");
    }

    private CourierClientContractEntity updateAdminStatus(UUID contractId,
                                                          ContractLifecycleStatus status,
                                                          String actorName,
                                                          String reviewNotes) {
        CourierClientContractEntity contract = findContract(contractId);
        if (contract == null) {
            throw new IllegalArgumentException("Contract not found.");
        }
        Instant now = Instant.now();
        contract.setStatus(status.name());
        contract.setInternalReviewedAt(now);
        contract.setInternalReviewNotes(blankToNull(reviewNotes));
        contract.setUpdatedAt(now);
        CourierClientContractEntity saved = contractRepository.save(contract);
        recordStatus(saved.getId(), status, reviewNotes, actorName);
        return saved;
    }

    private void recordStatus(UUID contractId, ContractLifecycleStatus status, String note, String actorName) {
        if (contractId == null || status == null) {
            return;
        }
        statusHistoryRepository.save(new CourierContractStatusHistoryEntity(
                UUID.randomUUID(),
                contractId,
                status.name(),
                blankToNull(note),
                blankToNull(actorName),
                Instant.now()
        ));
    }

    private Map<String, String> readSelections(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            Map<String, String> parsed = objectMapper.readValue(json, STRING_MAP);
            Map<String, String> normalized = new LinkedHashMap<>();
            parsed.forEach((key, value) -> {
                if (key != null && value != null && !value.isBlank()) {
                    normalized.put(key, value);
                }
            });
            return normalized;
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to read client contract selections.", ex);
        }
    }

    private String writeSelections(Map<String, String> selections) {
        try {
            return objectMapper.writeValueAsString(selections == null ? Map.of() : selections);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to write client contract selections.", ex);
        }
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private String firstNonBlank(String primary, String fallback) {
        String normalized = blankToNull(primary);
        return normalized != null ? normalized : blankToNull(fallback);
    }
}
