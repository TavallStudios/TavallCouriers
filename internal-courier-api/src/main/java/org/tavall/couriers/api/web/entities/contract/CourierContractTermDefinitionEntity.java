package org.tavall.couriers.api.web.entities.contract;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "courier_contract_term_definitions", schema = "courier_schemas")
public class CourierContractTermDefinitionEntity {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "code", nullable = false, length = 80, unique = true)
    private String code;

    @Column(name = "label", nullable = false, length = 140)
    private String label;

    @Column(name = "input_type", nullable = false, length = 40)
    private String inputType;

    @Column(name = "category", nullable = false, length = 60)
    private String category;

    @Column(name = "help_text", columnDefinition = "text")
    private String helpText;

    @Column(name = "options_text", columnDefinition = "text")
    private String optionsText;

    @Column(name = "default_value", columnDefinition = "text")
    private String defaultValue;

    @Column(name = "required", nullable = false)
    private boolean required;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected CourierContractTermDefinitionEntity() {
    }

    public CourierContractTermDefinitionEntity(UUID id,
                                               String code,
                                               String label,
                                               String inputType,
                                               String category,
                                               String helpText,
                                               String optionsText,
                                               String defaultValue,
                                               boolean required,
                                               boolean active,
                                               int displayOrder,
                                               Instant createdAt,
                                               Instant updatedAt) {
        this.id = id;
        this.code = code;
        this.label = label;
        this.inputType = inputType;
        this.category = category;
        this.helpText = helpText;
        this.optionsText = optionsText;
        this.defaultValue = defaultValue;
        this.required = required;
        this.active = active;
        this.displayOrder = displayOrder;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    public String getOptionsText() {
        return optionsText;
    }

    public void setOptionsText(String optionsText) {
        this.optionsText = optionsText;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
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
