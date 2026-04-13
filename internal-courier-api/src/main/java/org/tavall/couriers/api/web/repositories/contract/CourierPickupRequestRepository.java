package org.tavall.couriers.api.web.repositories.contract;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tavall.couriers.api.web.entities.contract.CourierPickupRequestEntity;

import java.util.List;
import java.util.UUID;

public interface CourierPickupRequestRepository extends JpaRepository<CourierPickupRequestEntity, UUID> {
    List<CourierPickupRequestEntity> findAllByClientUserIdOrderByCreatedAtDesc(UUID clientUserId);
    List<CourierPickupRequestEntity> findAllByStatusOrderByCreatedAtDesc(String status);
    List<CourierPickupRequestEntity> findAllByOrderByCreatedAtDesc();
}
