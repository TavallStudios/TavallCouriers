package org.tavall.couriers.api.web.repositories.contract;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tavall.couriers.api.web.entities.contract.CourierContractTemplateEntity;

import java.util.Optional;
import java.util.UUID;

public interface CourierContractTemplateRepository extends JpaRepository<CourierContractTemplateEntity, UUID> {
    Optional<CourierContractTemplateEntity> findFirstByActiveTrueOrderByUpdatedAtDesc();
}
