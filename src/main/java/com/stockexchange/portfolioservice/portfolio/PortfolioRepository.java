package com.stockexchange.portfolioservice.portfolio;

import com.stockexchange.portfolioservice.portfolio.domain.Portfolio;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PortfolioRepository extends JpaRepository<Portfolio,UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Portfolio> findByUserId(UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Portfolio p WHERE p.portfolioId = :id")
    Optional<Portfolio> findByIdWithLock(@Param("id") UUID id);

    @Query(value = "SELECT pg_advisory_xact_lock(:key)", nativeQuery = true)
    void acquireAdvisoryLock(@Param("key") long key);
}
