package org.tavall.couriers.api.web.service.contract;

import org.springframework.stereotype.Service;
import org.tavall.couriers.api.web.contract.ContractLifecycleStatus;
import org.tavall.couriers.api.web.contract.PickupRequestStatus;
import org.tavall.couriers.api.web.contract.PickupRequestType;
import org.tavall.couriers.api.web.entities.contract.CourierClientContractEntity;
import org.tavall.couriers.api.web.entities.contract.CourierPickupRequestEntity;
import org.tavall.couriers.api.web.repositories.contract.CourierPickupRequestRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class CourierPickupRequestService {

    private final CourierPickupRequestRepository pickupRequestRepository;
    private final CourierClientContractService clientContractService;

    public CourierPickupRequestService(CourierPickupRequestRepository pickupRequestRepository,
                                       CourierClientContractService clientContractService) {
        this.pickupRequestRepository = pickupRequestRepository;
        this.clientContractService = clientContractService;
    }

    public List<CourierPickupRequestEntity> getPickupsForClient(UUID clientUserId) {
        if (clientUserId == null) {
            return List.of();
        }
        return pickupRequestRepository.findAllByClientUserIdOrderByCreatedAtDesc(clientUserId);
    }

    public List<CourierPickupRequestEntity> getAllPickupRequests() {
        return pickupRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    public CourierPickupRequestEntity createPickupRequest(UUID clientUserId,
                                                          UUID contractId,
                                                          PickupRequestType requestType,
                                                          String pickupAddress,
                                                          String pickupZone,
                                                          Instant scheduledFor,
                                                          String notes,
                                                          String recurringRule) {
        CourierClientContractEntity contract = clientContractService.findContractForClient(clientUserId, contractId);
        if (contract == null) {
            throw new IllegalArgumentException("Contract not found.");
        }
        if (!ContractLifecycleStatus.ACTIVE.name().equals(contract.getStatus())) {
            throw new IllegalStateException("Pickups unlock after Tavall activates the contract.");
        }
        if (requestType == PickupRequestType.RECURRING && !clientContractService.allowsRecurringPickups(contract)) {
            throw new IllegalStateException("Recurring pickup requests are not enabled on this contract.");
        }
        String contractPickupZone = contract.getPickupZone();
        String normalizedPickupZone = blankToNull(pickupZone);
        if (contractPickupZone != null && normalizedPickupZone != null
                && !contractPickupZone.equalsIgnoreCase(normalizedPickupZone)) {
            throw new IllegalStateException("Pickup zone does not match the active contract.");
        }
        String normalizedAddress = blankToNull(pickupAddress);
        if (normalizedAddress == null) {
            throw new IllegalArgumentException("Pickup address is required.");
        }
        if (scheduledFor == null) {
            throw new IllegalArgumentException("Pickup date and time is required.");
        }
        Instant now = Instant.now();
        CourierPickupRequestEntity entity = new CourierPickupRequestEntity(
                UUID.randomUUID(),
                contract.getId(),
                clientUserId,
                requestType.name(),
                PickupRequestStatus.REQUESTED.name(),
                normalizedAddress,
                normalizedPickupZone != null ? normalizedPickupZone : contractPickupZone,
                blankToNull(notes),
                scheduledFor,
                requestType == PickupRequestType.RECURRING ? blankToNull(recurringRule) : null,
                now,
                now
        );
        return pickupRequestRepository.save(entity);
    }

    private String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }
}
