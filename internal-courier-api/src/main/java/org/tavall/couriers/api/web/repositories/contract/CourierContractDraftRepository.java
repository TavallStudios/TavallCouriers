package org.tavall.couriers.api.web.repositories.contract;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tavall.couriers.api.web.entities.contract.CourierContractDraftEntity;

import java.util.Optional;
import java.util.UUID;

public interface CourierContractDraftRepository extends JpaRepository<CourierContractDraftEntity, UUID> {
    Optional<CourierContractDraftEntity> findTopBySessionKeyOrderByUpdatedAtDesc(String sessionKey);
    Optional<CourierContractDraftEntity> findTopByLinkedClientIdOrderByUpdatedAtDesc(UUID linkedClientId);
}
