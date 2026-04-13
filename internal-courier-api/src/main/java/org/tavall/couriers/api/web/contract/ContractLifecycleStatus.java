package org.tavall.couriers.api.web.contract;

public enum ContractLifecycleStatus {
    DRAFT,
    GENERATED,
    PENDING_ACCOUNT_LINK,
    PENDING_CLIENT_APPROVAL,
    PENDING_INTERNAL_REVIEW,
    ACTIVE,
    REJECTED,
    ARCHIVED
}
