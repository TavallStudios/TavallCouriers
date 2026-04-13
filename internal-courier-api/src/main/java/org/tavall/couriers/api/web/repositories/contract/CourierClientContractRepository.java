package org.tavall.couriers.api.web.repositories.contract;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tavall.couriers.api.web.entities.contract.CourierClientContractEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourierClientContractRepository extends JpaRepository<CourierClientContractEntity, UUID> {
    List<CourierClientContractEntity> findAllByClientUserIdOrderByUpdatedAtDesc(UUID clientUserId);
    List<CourierClientContractEntity> findAllByStatusOrderByUpdatedAtDesc(String status);
    Optional<CourierClientContractEntity> findByIdAndClientUserId(UUID id, UUID clientUserId);
    Optional<CourierClientContractEntity> findFirstByDraftId(UUID draftId);
}
