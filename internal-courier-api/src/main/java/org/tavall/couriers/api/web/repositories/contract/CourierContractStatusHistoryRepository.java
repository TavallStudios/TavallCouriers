package org.tavall.couriers.api.web.repositories.contract;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tavall.couriers.api.web.entities.contract.CourierContractStatusHistoryEntity;

import java.util.List;
import java.util.UUID;

public interface CourierContractStatusHistoryRepository extends JpaRepository<CourierContractStatusHistoryEntity, UUID> {
    List<CourierContractStatusHistoryEntity> findAllByContractIdOrderByCreatedAtAsc(UUID contractId);
}
