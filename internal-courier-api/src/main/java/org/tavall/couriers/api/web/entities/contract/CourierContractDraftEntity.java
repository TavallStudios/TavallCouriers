package org.tavall.couriers.api.web.entities.contract;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "courier_contract_drafts", schema = "courier_schemas")
public class CourierContractDraftEntity {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "session_key", nullable = false, length = 120)
    private String sessionKey;

    @Column(name = "contact_name", length = 160)
    private String contactName;

    @Column(name = "contact_email", length = 160)
    private String contactEmail;

    @Column(name = "company_name", length = 160)
    private String companyName;

    @Column(name = "phone_number", length = 60)
    private String phoneNumber;

    @Column(name = "selected_terms_json", nullable = false, columnDefinition = "text")
    private String selectedTermsJson;

    @Column(name = "generated_contract_html", columnDefinition = "text")
    private String generatedContractHtml;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @Column(name = "linked_client_id", columnDefinition = "uuid")
    private UUID linkedClientId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected CourierContractDraftEntity() {
    }

    public CourierContractDraftEntity(UUID id,
                                      String sessionKey,
                                      String contactName,
                                      String contactEmail,
                                      String companyName,
                                      String phoneNumber,
                                      String selectedTermsJson,
                                      String generatedContractHtml,
                                      String status,
                                      UUID linkedClientId,
                                      Instant createdAt,
                                      Instant updatedAt) {
        this.id = id;
        this.sessionKey = sessionKey;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.companyName = companyName;
        this.phoneNumber = phoneNumber;
        this.selectedTermsJson = selectedTermsJson;
        this.generatedContractHtml = generatedContractHtml;
        this.status = status;
        this.linkedClientId = linkedClientId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSelectedTermsJson() {
        return selectedTermsJson;
    }

    public void setSelectedTermsJson(String selectedTermsJson) {
        this.selectedTermsJson = selectedTermsJson;
    }

    public String getGeneratedContractHtml() {
        return generatedContractHtml;
    }

    public void setGeneratedContractHtml(String generatedContractHtml) {
        this.generatedContractHtml = generatedContractHtml;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getLinkedClientId() {
        return linkedClientId;
    }

    public void setLinkedClientId(UUID linkedClientId) {
        this.linkedClientId = linkedClientId;
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
