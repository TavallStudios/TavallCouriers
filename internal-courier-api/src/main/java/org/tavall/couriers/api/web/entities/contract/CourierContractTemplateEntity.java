package org.tavall.couriers.api.web.entities.contract;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "courier_contract_templates", schema = "courier_schemas")
public class CourierContractTemplateEntity {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "template_name", nullable = false, length = 120)
    private String templateName;

    @Column(name = "template_title", nullable = false, length = 160)
    private String templateTitle;

    @Column(name = "intro_text", nullable = false, columnDefinition = "text")
    private String introText;

    @Column(name = "operations_text", nullable = false, columnDefinition = "text")
    private String operationsText;

    @Column(name = "pricing_text", nullable = false, columnDefinition = "text")
    private String pricingText;

    @Column(name = "review_notice", nullable = false, columnDefinition = "text")
    private String reviewNotice;

    @Column(name = "binding_party_name", length = 160)
    private String bindingPartyName;

    @Column(name = "binding_party_address", columnDefinition = "text")
    private String bindingPartyAddress;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected CourierContractTemplateEntity() {
    }

    public CourierContractTemplateEntity(UUID id,
                                         String templateName,
                                         String templateTitle,
                                         String introText,
                                         String operationsText,
                                         String pricingText,
                                         String reviewNotice,
                                         String bindingPartyName,
                                         String bindingPartyAddress,
                                         boolean active,
                                         Instant createdAt,
                                         Instant updatedAt) {
        this.id = id;
        this.templateName = templateName;
        this.templateTitle = templateTitle;
        this.introText = introText;
        this.operationsText = operationsText;
        this.pricingText = pricingText;
        this.reviewNotice = reviewNotice;
        this.bindingPartyName = bindingPartyName;
        this.bindingPartyAddress = bindingPartyAddress;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateTitle() {
        return templateTitle;
    }

    public void setTemplateTitle(String templateTitle) {
        this.templateTitle = templateTitle;
    }

    public String getIntroText() {
        return introText;
    }

    public void setIntroText(String introText) {
        this.introText = introText;
    }

    public String getOperationsText() {
        return operationsText;
    }

    public void setOperationsText(String operationsText) {
        this.operationsText = operationsText;
    }

    public String getPricingText() {
        return pricingText;
    }

    public void setPricingText(String pricingText) {
        this.pricingText = pricingText;
    }

    public String getReviewNotice() {
        return reviewNotice;
    }

    public void setReviewNotice(String reviewNotice) {
        this.reviewNotice = reviewNotice;
    }

    public String getBindingPartyName() {
        return bindingPartyName;
    }

    public void setBindingPartyName(String bindingPartyName) {
        this.bindingPartyName = bindingPartyName;
    }

    public String getBindingPartyAddress() {
        return bindingPartyAddress;
    }

    public void setBindingPartyAddress(String bindingPartyAddress) {
        this.bindingPartyAddress = bindingPartyAddress;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
