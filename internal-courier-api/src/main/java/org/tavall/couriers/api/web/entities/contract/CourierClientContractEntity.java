package org.tavall.couriers.api.web.entities.contract;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "courier_client_contracts", schema = "courier_schemas")
public class CourierClientContractEntity {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "draft_id", columnDefinition = "uuid")
    private UUID draftId;

    @Column(name = "client_user_id", nullable = false, columnDefinition = "uuid")
    private UUID clientUserId;

    @Column(name = "template_id", columnDefinition = "uuid")
    private UUID templateId;

    @Column(name = "contract_title", nullable = false, length = 160)
    private String contractTitle;

    @Column(name = "client_display_name", length = 160)
    private String clientDisplayName;

    @Column(name = "client_company_name", length = 160)
    private String clientCompanyName;

    @Column(name = "client_contact_email", length = 160)
    private String clientContactEmail;

    @Column(name = "selected_terms_json", nullable = false, columnDefinition = "text")
    private String selectedTermsJson;

    @Column(name = "generated_contract_html", nullable = false, columnDefinition = "text")
    private String generatedContractHtml;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @Column(name = "parcel_volume_summary", length = 120)
    private String parcelVolumeSummary;

    @Column(name = "service_radius_miles")
    private Integer serviceRadiusMiles;

    @Column(name = "service_zone", length = 120)
    private String serviceZone;

    @Column(name = "pickup_zone", length = 120)
    private String pickupZone;

    @Column(name = "pricing_summary", columnDefinition = "text")
    private String pricingSummary;

    @Column(name = "signed_by_name", length = 160)
    private String signedByName;

    @Column(name = "approval_ip_address", length = 80)
    private String approvalIpAddress;

    @Column(name = "signed_at")
    private Instant signedAt;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "internal_reviewed_at")
    private Instant internalReviewedAt;

    @Column(name = "internal_review_notes", columnDefinition = "text")
    private String internalReviewNotes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected CourierClientContractEntity() {
    }

    public CourierClientContractEntity(UUID id,
                                       UUID draftId,
                                       UUID clientUserId,
                                       UUID templateId,
                                       String contractTitle,
                                       String clientDisplayName,
                                       String clientCompanyName,
                                       String clientContactEmail,
                                       String selectedTermsJson,
                                       String generatedContractHtml,
                                       String status,
                                       String parcelVolumeSummary,
                                       Integer serviceRadiusMiles,
                                       String serviceZone,
                                       String pickupZone,
                                       String pricingSummary,
                                       String signedByName,
                                       String approvalIpAddress,
                                       Instant signedAt,
                                       Instant approvedAt,
                                       Instant internalReviewedAt,
                                       String internalReviewNotes,
                                       Instant createdAt,
                                       Instant updatedAt) {
        this.id = id;
        this.draftId = draftId;
        this.clientUserId = clientUserId;
        this.templateId = templateId;
        this.contractTitle = contractTitle;
        this.clientDisplayName = clientDisplayName;
        this.clientCompanyName = clientCompanyName;
        this.clientContactEmail = clientContactEmail;
        this.selectedTermsJson = selectedTermsJson;
        this.generatedContractHtml = generatedContractHtml;
        this.status = status;
        this.parcelVolumeSummary = parcelVolumeSummary;
        this.serviceRadiusMiles = serviceRadiusMiles;
        this.serviceZone = serviceZone;
        this.pickupZone = pickupZone;
        this.pricingSummary = pricingSummary;
        this.signedByName = signedByName;
        this.approvalIpAddress = approvalIpAddress;
        this.signedAt = signedAt;
        this.approvedAt = approvedAt;
        this.internalReviewedAt = internalReviewedAt;
        this.internalReviewNotes = internalReviewNotes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getDraftId() {
        return draftId;
    }

    public void setDraftId(UUID draftId) {
        this.draftId = draftId;
    }

    public UUID getClientUserId() {
        return clientUserId;
    }

    public void setClientUserId(UUID clientUserId) {
        this.clientUserId = clientUserId;
    }

    public UUID getTemplateId() {
        return templateId;
    }

    public void setTemplateId(UUID templateId) {
        this.templateId = templateId;
    }

    public String getContractTitle() {
        return contractTitle;
    }

    public void setContractTitle(String contractTitle) {
        this.contractTitle = contractTitle;
    }

    public String getClientDisplayName() {
        return clientDisplayName;
    }

    public void setClientDisplayName(String clientDisplayName) {
        this.clientDisplayName = clientDisplayName;
    }

    public String getClientCompanyName() {
        return clientCompanyName;
    }

    public void setClientCompanyName(String clientCompanyName) {
        this.clientCompanyName = clientCompanyName;
    }

    public String getClientContactEmail() {
        return clientContactEmail;
    }

    public void setClientContactEmail(String clientContactEmail) {
        this.clientContactEmail = clientContactEmail;
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

    public String getParcelVolumeSummary() {
        return parcelVolumeSummary;
    }

    public void setParcelVolumeSummary(String parcelVolumeSummary) {
        this.parcelVolumeSummary = parcelVolumeSummary;
    }

    public Integer getServiceRadiusMiles() {
        return serviceRadiusMiles;
    }

    public void setServiceRadiusMiles(Integer serviceRadiusMiles) {
        this.serviceRadiusMiles = serviceRadiusMiles;
    }

    public String getServiceZone() {
        return serviceZone;
    }

    public void setServiceZone(String serviceZone) {
        this.serviceZone = serviceZone;
    }

    public String getPickupZone() {
        return pickupZone;
    }

    public void setPickupZone(String pickupZone) {
        this.pickupZone = pickupZone;
    }

    public String getPricingSummary() {
        return pricingSummary;
    }

    public void setPricingSummary(String pricingSummary) {
        this.pricingSummary = pricingSummary;
    }

    public String getSignedByName() {
        return signedByName;
    }

    public void setSignedByName(String signedByName) {
        this.signedByName = signedByName;
    }

    public String getApprovalIpAddress() {
        return approvalIpAddress;
    }

    public void setApprovalIpAddress(String approvalIpAddress) {
        this.approvalIpAddress = approvalIpAddress;
    }

    public Instant getSignedAt() {
        return signedAt;
    }

    public void setSignedAt(Instant signedAt) {
        this.signedAt = signedAt;
    }

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
    }

    public Instant getInternalReviewedAt() {
        return internalReviewedAt;
    }

    public void setInternalReviewedAt(Instant internalReviewedAt) {
        this.internalReviewedAt = internalReviewedAt;
    }

    public String getInternalReviewNotes() {
        return internalReviewNotes;
    }

    public void setInternalReviewNotes(String internalReviewNotes) {
        this.internalReviewNotes = internalReviewNotes;
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
