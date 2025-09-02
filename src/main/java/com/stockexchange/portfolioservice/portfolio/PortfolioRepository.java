package com.stockexchange.portfolioservice.portfolio;

import com.stockexchange.portfolioservice.portfolio.domain.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PortfolioRepository extends JpaRepository<Portfolio,UUID> {
    Optional<Portfolio> findByUserId(UUID userId);
}
