package org.tavall.couriers.web.view.controller.dsahboard.client.helper;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.tavall.couriers.api.web.contract.PickupRequestType;
import org.tavall.couriers.api.web.endpoints.Routes;
import org.tavall.couriers.api.web.service.contract.CourierClientContractService;
import org.tavall.couriers.api.web.service.contract.CourierContractTemplateService;
import org.tavall.couriers.api.web.service.contract.CourierPickupRequestService;
import org.tavall.couriers.api.web.service.user.UserAccountService;
import org.tavall.couriers.api.web.user.UserAccountEntity;
import org.tavall.couriers.api.web.user.permission.Role;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.UUID;

@Component
public class ClientDashboardControllerHelper {

    private final UserAccountService userAccountService;
    private final CourierClientContractService clientContractService;
    private final CourierPickupRequestService pickupRequestService;
    private final CourierContractTemplateService templateService;

    public ClientDashboardControllerHelper(UserAccountService userAccountService,
                                           CourierClientContractService clientContractService,
                                           CourierPickupRequestService pickupRequestService,
                                           CourierContractTemplateService templateService) {
        this.userAccountService = userAccountService;
        this.clientContractService = clientContractService;
        this.pickupRequestService = pickupRequestService;
        this.templateService = templateService;
    }

    public String dashboard(Model model, Authentication authentication, String status, String error) {
        UserAccountEntity user = resolveClient(authentication);
        var contracts = clientContractService.getContractsForClient(user.getUserUUID());
        model.addAttribute("title", "Client Dashboard");
        model.addAttribute("clientContracts", contracts);
        model.addAttribute("activeContracts", contracts.stream().filter(contract -> "ACTIVE".equals(contract.getStatus())).toList());
        model.addAttribute("pickupRequests", pickupRequestService.getPickupsForClient(user.getUserUUID()));
        model.addAttribute("activeTemplate", templateService.getActiveTemplate());
        model.addAttribute("statusMessage", status);
        model.addAttribute("errorMessage", error);
        return "dashboard/client/client-dashboard";
    }

    public String approveContract(UUID contractId,
                                  String signerName,
                                  Authentication authentication,
                                  HttpServletRequest request,
                                  RedirectAttributes redirectAttributes) {
        try {
            UserAccountEntity user = resolveClient(authentication);
            clientContractService.approveContract(user.getUserUUID(), contractId, signerName, request.getRemoteAddr());
            redirectAttributes.addAttribute("status", "Contract approved. Tavall will review it and contact you with activation steps.");
        } catch (RuntimeException ex) {
            redirectAttributes.addAttribute("error", ex.getMessage());
        }
        return "redirect:" + Routes.clientDashboard();
    }

    public String createPickup(UUID contractId,
                               String requestType,
                               String scheduledFor,
                               String pickupAddress,
                               String pickupZone,
                               String notes,
                               String recurringRule,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            UserAccountEntity user = resolveClient(authentication);
            pickupRequestService.createPickupRequest(
                    user.getUserUUID(),
                    contractId,
                    PickupRequestType.valueOf(requestType.trim().toUpperCase()),
                    pickupAddress,
                    pickupZone,
                    LocalDateTime.parse(scheduledFor).atZone(ZoneId.systemDefault()).toInstant(),
                    notes,
                    recurringRule
            );
            redirectAttributes.addAttribute("status", "Pickup request submitted.");
        } catch (RuntimeException ex) {
            redirectAttributes.addAttribute("error", ex.getMessage());
        }
        return "redirect:" + Routes.clientDashboard();
    }

    private UserAccountEntity resolveClient(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new IllegalStateException("Active client session not found.");
        }
        UserAccountEntity user = userAccountService.findByUsername(authentication.getName());
        if (user != null) {
            return user;
        }
        return userAccountService.getOrCreateFromExternalIdentity(
                "local-client:" + authentication.getName().toLowerCase(),
                authentication.getName(),
                Set.of(Role.CLIENT)
        );
    }
}
