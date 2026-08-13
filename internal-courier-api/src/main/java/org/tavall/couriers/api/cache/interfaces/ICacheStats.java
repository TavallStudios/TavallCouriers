/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 *
 * No public use, distribution, or modification is permitted without express,
 * written, and verifiable consent from the project founder.
 * SEE LICENSE.TXT
 */

package org.tavall.couriers.api.cache.interfaces;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Read-only snapshot of cache population health.
 *
 * <p>The counts distinguish entries that are still eligible for use from those
 * that remain present but have expired. Implementations should report all three
 * values from the same logical observation so callers can reason about cache
 * occupancy without racing independent counters.</p>
 */
public interface ICacheStats {
    /** @return total entries currently represented by the cache */
    int getTotalEntries();

    /** @return entries that are currently valid for lookup */
    int getValidEntries();

    /** @return entries retained by the cache but no longer valid for lookup */
    int getExpiredEntries();

}