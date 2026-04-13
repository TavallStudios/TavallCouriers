package org.tavall.couriers.api.web.entities.contract;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "courier_contract_status_history", schema = "courier_schemas")
public class CourierContractStatusHistoryEntity {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "contract_id", nullable = false, columnDefinition = "uuid")
    private UUID contractId;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @Column(name = "note", columnDefinition = "text")
    private String note;

    @Column(name = "actor_name", length = 160)
    private String actorName;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected CourierContractStatusHistoryEntity() {
    }

    public CourierContractStatusHistoryEntity(UUID id,
                                              UUID contractId,
                                              String status,
                                              String note,
                                              String actorName,
                                              Instant createdAt) {
        this.id = id;
        this.contractId = contractId;
        this.status = status;
        this.note = note;
        this.actorName = actorName;
        this.createdAt = createdAt;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
