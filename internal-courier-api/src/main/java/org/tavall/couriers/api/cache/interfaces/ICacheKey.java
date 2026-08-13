/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package org.tavall.couriers.api.cache.interfaces;


import org.tavall.couriers.api.cache.enums.CacheDomain;
import org.tavall.couriers.api.cache.enums.CacheSource;
import org.tavall.couriers.api.cache.enums.CacheType;
import org.tavall.couriers.api.cache.enums.CacheVersion;

/**
 * Describes the identity of an entry in the courier cache.
 *
 * <p>A cache key is more than its caller-supplied raw value. Implementations may
 * also partition identity by cache type, domain, source, and version. Callers
 * should therefore treat the complete key as the cache identity rather than
 * comparing only {@link #getRawCacheKey()}.</p>
 *
 * @param <K> type of the caller-supplied key value
 */
public interface ICacheKey<K>{

    /**
     * Returns the caller-supplied portion of the cache key.
     *
     * <p>The default implementation returns {@code null}; concrete key types
     * that carry a raw key must override this method.</p>
     *
     * @return the raw key value, or {@code null} when this key does not expose one
     */
    default K getRawCacheKey(){
        return null;
    }

    /** @return the cache type used to partition this key, or {@code null} when unspecified */
    CacheType getCacheType();

    /** @return the logical cache domain for this key, or {@code null} when unspecified */
    CacheDomain getCacheDomain();

    /** @return the source that produced the cached value, or {@code null} when unspecified */
    CacheSource getSource();

    /** @return the cache schema/data version for this key, or {@code null} when unspecified */
    CacheVersion getVersion();

    /**
     * Compares complete cache-key identity.
     *
     * @param o object to compare with this key
     * @return {@code true} when both objects represent the same cache identity
     */
    boolean equals(Object o);

    /** @return a hash code consistent with complete cache-key identity */
    int hashCode();

    /** @return a diagnostic representation of this cache key */
    String toString();

}