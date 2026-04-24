package org.tavall.couriers.web.view.controller.dsahboard.client;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.tavall.couriers.api.web.endpoints.Routes;
import org.tavall.couriers.web.view.controller.dsahboard.client.helper.ClientDashboardControllerHelper;

import java.util.UUID;

@Controller
@PreAuthorize("hasRole('CLIENT')")
public class ClientDashboardController {

    private final ClientDashboardControllerHelper helper;

    public ClientDashboardController(ClientDashboardControllerHelper helper) {
        this.helper = helper;
    }

    @GetMapping(Routes.DASHBOARD_CLIENT)
    public String dashboard(Model model,
                            Authentication authentication,
                            @RequestParam(value = "status", required = false) String status,
                            @RequestParam(value = "error", required = false) String error) {
        return helper.dashboard(model, authentication, status, error);
    }

    @PostMapping("/dashboard/client/contracts/{contractId}/approve")
    public String approveContract(@PathVariable("contractId") UUID contractId,
                                  @RequestParam("signerName") String signerName,
                                  Authentication authentication,
                                  HttpServletRequest request,
                                  RedirectAttributes redirectAttributes) {
        return helper.approveContract(contractId, signerName, authentication, request, redirectAttributes);
    }

    @PostMapping("/dashboard/client/pickups")
    public String createPickup(@RequestParam("contractId") UUID contractId,
                               @RequestParam("requestType") String requestType,
                               @RequestParam("scheduledFor") String scheduledFor,
                               @RequestParam("pickupAddress") String pickupAddress,
                               @RequestParam(value = "pickupZone", required = false) String pickupZone,
                               @RequestParam(value = "notes", required = false) String notes,
                               @RequestParam(value = "recurringRule", required = false) String recurringRule,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        return helper.createPickup(contractId, requestType, scheduledFor, pickupAddress, pickupZone, notes, recurringRule, authentication, redirectAttributes);
    }
}
