package com.stockexchange.portfolioservice.portfolio;

import com.stockexchange.portfolioservice.portfolio.domain.Portfolio;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface PortfolioRepository extends ReactiveCrudRepository<Portfolio,UUID> {

    Mono<Portfolio> findByUserId(UUID userId);

}
