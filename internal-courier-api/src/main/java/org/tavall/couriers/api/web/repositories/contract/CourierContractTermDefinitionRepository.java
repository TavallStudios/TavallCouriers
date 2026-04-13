package org.tavall.couriers.api.web.repositories.contract;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tavall.couriers.api.web.entities.contract.CourierContractTermDefinitionEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourierContractTermDefinitionRepository extends JpaRepository<CourierContractTermDefinitionEntity, UUID> {
    List<CourierContractTermDefinitionEntity> findAllByActiveTrueOrderByDisplayOrderAsc();
    List<CourierContractTermDefinitionEntity> findAllByOrderByDisplayOrderAsc();
    Optional<CourierContractTermDefinitionEntity> findByCodeIgnoreCase(String code);
}
