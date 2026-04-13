package org.tavall.couriers.web.view.controller.dsahboard.admin.helper;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.tavall.couriers.api.web.endpoints.Routes;
import org.tavall.couriers.api.web.service.contract.CourierClientContractService;
import org.tavall.couriers.api.web.service.contract.CourierContractTemplateService;
import org.tavall.couriers.api.web.service.contract.CourierPickupRequestService;
import org.tavall.couriers.api.web.service.route.DeliveryRouteService;
import org.tavall.couriers.api.web.service.shipping.ShippingLabelMetaDataService;
import org.tavall.couriers.api.web.service.user.UserAccountService;
import org.tavall.couriers.api.web.user.permission.Role;

import java.util.Set;
import java.util.UUID;

@Component
public class AdminDashboardControllerHelper {

    private final CourierContractTemplateService templateService;
    private final CourierClientContractService clientContractService;
    private final CourierPickupRequestService pickupRequestService;
    private final UserAccountService userAccountService;
    private final ShippingLabelMetaDataService shippingLabelMetaDataService;
    private final DeliveryRouteService deliveryRouteService;

    public AdminDashboardControllerHelper(CourierContractTemplateService templateService,
                                          CourierClientContractService clientContractService,
                                          CourierPickupRequestService pickupRequestService,
                                          UserAccountService userAccountService,
                                          ShippingLabelMetaDataService shippingLabelMetaDataService,
                                          DeliveryRouteService deliveryRouteService) {
        this.templateService = templateService;
        this.clientContractService = clientContractService;
        this.pickupRequestService = pickupRequestService;
        this.userAccountService = userAccountService;
        this.shippingLabelMetaDataService = shippingLabelMetaDataService;
        this.deliveryRouteService = deliveryRouteService;
    }

    public String dashboard(Model model, Authentication authentication, String status, String error) {
        model.addAttribute("title", "Admin Operations");
        model.addAttribute("statusMessage", status);
        model.addAttribute("errorMessage", error);
        model.addAttribute("activeTemplate", templateService.getActiveTemplate());
        model.addAttribute("termDefinitions", templateService.getAllTermDefinitions());
        model.addAttribute("pendingContracts", clientContractService.getContractsPendingReview());
        model.addAttribute("pickupRequests", pickupRequestService.getAllPickupRequests());
        model.addAttribute("adminUsers", userAccountService.getAllUsers());
        model.addAttribute("totalShipments", shippingLabelMetaDataService.getAllShipmentLabels().size());
        model.addAttribute("routeCount", deliveryRouteService.getAllRoutes().size());
        model.addAttribute("actorName", authentication != null ? authentication.getName() : "admin");
        return "dashboard/admin/admin-dashboard";
    }

    public String updateTemplate(String templateTitle,
                                 String introText,
                                 String operationsText,
                                 String pricingText,
                                 String reviewNotice,
                                 String bindingPartyName,
                                 String bindingPartyAddress,
                                 RedirectAttributes redirectAttributes) {
        try {
            templateService.updateActiveTemplate(templateTitle, introText, operationsText, pricingText, reviewNotice, bindingPartyName, bindingPartyAddress);
            redirectAttributes.addAttribute("status", "Contract template updated.");
        } catch (RuntimeException ex) {
            redirectAttributes.addAttribute("error", ex.getMessage());
        }
        return "redirect:" + Routes.adminDashboard();
    }

    public String saveTerm(String code,
                           String label,
                           String inputType,
                           String category,
                           String helpText,
                           String optionsText,
                           String defaultValue,
                           int displayOrder,
                           boolean required,
                           RedirectAttributes redirectAttributes) {
        try {
            templateService.saveTermDefinition(code, label, inputType, category, helpText, optionsText, defaultValue, required, displayOrder, true);
            redirectAttributes.addAttribute("status", "Contract term saved.");
        } catch (RuntimeException ex) {
            redirectAttributes.addAttribute("error", ex.getMessage());
        }
        return "redirect:" + Routes.adminDashboard();
    }

    public String createClient(String clientUsername, RedirectAttributes redirectAttributes) {
        try {
            userAccountService.createUser(clientUsername, Set.of(Role.CLIENT));
            redirectAttributes.addAttribute("status", "Client account created.");
        } catch (RuntimeException ex) {
            redirectAttributes.addAttribute("error", ex.getMessage());
        }
        return "redirect:" + Routes.adminDashboard();
    }

    public String activateContract(UUID contractId,
                                   Authentication authentication,
                                   String reviewNotes,
                                   RedirectAttributes redirectAttributes) {
        try {
            clientContractService.activateContract(contractId, authentication != null ? authentication.getName() : "admin", reviewNotes);
            redirectAttributes.addAttribute("status", "Contract activated.");
        } catch (RuntimeException ex) {
            redirectAttributes.addAttribute("error", ex.getMessage());
        }
        return "redirect:" + Routes.adminDashboard();
    }

    public String rejectContract(UUID contractId,
                                 Authentication authentication,
                                 String reviewNotes,
                                 RedirectAttributes redirectAttributes) {
        try {
            clientContractService.rejectContract(contractId, authentication != null ? authentication.getName() : "admin", reviewNotes);
            redirectAttributes.addAttribute("status", "Contract rejected.");
        } catch (RuntimeException ex) {
            redirectAttributes.addAttribute("error", ex.getMessage());
        }
        return "redirect:" + Routes.adminDashboard();
    }
}
