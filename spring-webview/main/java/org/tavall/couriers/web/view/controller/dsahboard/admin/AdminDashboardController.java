package org.tavall.couriers.web.view.controller.dsahboard.admin;

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
import org.tavall.couriers.web.view.controller.dsahboard.admin.helper.AdminDashboardControllerHelper;

import java.util.UUID;

@Controller
@PreAuthorize("hasAnyRole('MERCHANT','SUPERUSER','SUPPORT')")
public class AdminDashboardController {

    private final AdminDashboardControllerHelper helper;

    public AdminDashboardController(AdminDashboardControllerHelper helper) {
        this.helper = helper;
    }

    @GetMapping(Routes.DASHBOARD_ADMIN)
    public String dashboard(Model model,
                            Authentication authentication,
                            @RequestParam(value = "status", required = false) String status,
                            @RequestParam(value = "error", required = false) String error) {
        return helper.dashboard(model, authentication, status, error);
    }

    @PostMapping("/dashboard/admin/template")
    public String updateTemplate(@RequestParam("templateTitle") String templateTitle,
                                 @RequestParam("introText") String introText,
                                 @RequestParam("operationsText") String operationsText,
                                 @RequestParam("pricingText") String pricingText,
                                 @RequestParam("reviewNotice") String reviewNotice,
                                 @RequestParam(value = "bindingPartyName", required = false) String bindingPartyName,
                                 @RequestParam(value = "bindingPartyAddress", required = false) String bindingPartyAddress,
                                 RedirectAttributes redirectAttributes) {
        return helper.updateTemplate(templateTitle, introText, operationsText, pricingText, reviewNotice, bindingPartyName, bindingPartyAddress, redirectAttributes);
    }

    @PostMapping("/dashboard/admin/terms")
    public String saveTerm(@RequestParam("code") String code,
                           @RequestParam("label") String label,
                           @RequestParam("inputType") String inputType,
                           @RequestParam("category") String category,
                           @RequestParam(value = "helpText", required = false) String helpText,
                           @RequestParam(value = "optionsText", required = false) String optionsText,
                           @RequestParam(value = "defaultValue", required = false) String defaultValue,
                           @RequestParam(value = "displayOrder", required = false, defaultValue = "999") int displayOrder,
                           @RequestParam(value = "required", required = false, defaultValue = "false") boolean required,
                           RedirectAttributes redirectAttributes) {
        return helper.saveTerm(code, label, inputType, category, helpText, optionsText, defaultValue, displayOrder, required, redirectAttributes);
    }

    @PostMapping("/dashboard/admin/clients/create")
    public String createClient(@RequestParam("clientUsername") String clientUsername,
                               RedirectAttributes redirectAttributes) {
        return helper.createClient(clientUsername, redirectAttributes);
    }

    @PostMapping("/dashboard/admin/contracts/{contractId}/activate")
    public String activateContract(@PathVariable("contractId") UUID contractId,
                                   Authentication authentication,
                                   @RequestParam(value = "reviewNotes", required = false) String reviewNotes,
                                   RedirectAttributes redirectAttributes) {
        return helper.activateContract(contractId, authentication, reviewNotes, redirectAttributes);
    }

    @PostMapping("/dashboard/admin/contracts/{contractId}/reject")
    public String rejectContract(@PathVariable("contractId") UUID contractId,
                                 Authentication authentication,
                                 @RequestParam(value = "reviewNotes", required = false) String reviewNotes,
                                 RedirectAttributes redirectAttributes) {
        return helper.rejectContract(contractId, authentication, reviewNotes, redirectAttributes);
    }
}
