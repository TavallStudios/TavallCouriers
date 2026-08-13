/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package org.tavall.couriers.api.cache.interfaces;


import org.checkerframework.checker.units.qual.K;
import org.tavall.couriers.api.cache.enums.CacheType;

/**
 * Wrapper contract for values stored in the courier cache.
 *
 * <p>The cache stores wrappers rather than raw payloads so implementations can
 * attach cache-specific metadata without changing the payload type exposed to
 * callers.</p>
 *
 * @param <V> payload type exposed by this cache value
 */
public interface ICacheValue<V> {

    /**
     * Returns the wrapped payload.
     *
     * <p>The default implementation returns {@code null}; concrete wrappers that
     * carry a payload must override this method.</p>
     *
     * @return the cached payload, or {@code null} when no payload is exposed
     */
    default V getValue() {
        return null;
    }

}