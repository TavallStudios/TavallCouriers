package org.tavall.couriers.api.web.entities.contract;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "courier_pickup_requests", schema = "courier_schemas")
public class CourierPickupRequestEntity {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "contract_id", nullable = false, columnDefinition = "uuid")
    private UUID contractId;

    @Column(name = "client_user_id", nullable = false, columnDefinition = "uuid")
    private UUID clientUserId;

    @Column(name = "request_type", nullable = false, length = 40)
    private String requestType;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @Column(name = "pickup_address", nullable = false, columnDefinition = "text")
    private String pickupAddress;

    @Column(name = "pickup_zone", length = 120)
    private String pickupZone;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;

    @Column(name = "scheduled_for")
    private Instant scheduledFor;

    @Column(name = "recurring_rule", columnDefinition = "text")
    private String recurringRule;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected CourierPickupRequestEntity() {
    }

    public CourierPickupRequestEntity(UUID id,
                                      UUID contractId,
                                      UUID clientUserId,
                                      String requestType,
                                      String status,
                                      String pickupAddress,
                                      String pickupZone,
                                      String notes,
                                      Instant scheduledFor,
                                      String recurringRule,
                                      Instant createdAt,
                                      Instant updatedAt) {
        this.id = id;
        this.contractId = contractId;
        this.clientUserId = clientUserId;
        this.requestType = requestType;
        this.status = status;
        this.pickupAddress = pickupAddress;
        this.pickupZone = pickupZone;
        this.notes = notes;
        this.scheduledFor = scheduledFor;
        this.recurringRule = recurringRule;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getContractId() {
        return contractId;
    }

    public void setContractId(UUID contractId) {
        this.contractId = contractId;
    }

    public UUID getClientUserId() {
        return clientUserId;
    }

    public void setClientUserId(UUID clientUserId) {
        this.clientUserId = clientUserId;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getPickupZone() {
        return pickupZone;
    }

    public void setPickupZone(String pickupZone) {
        this.pickupZone = pickupZone;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getScheduledFor() {
        return scheduledFor;
    }

    public void setScheduledFor(Instant scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public String getRecurringRule() {
        return recurringRule;
    }

    public void setRecurringRule(String recurringRule) {
        this.recurringRule = recurringRule;
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
