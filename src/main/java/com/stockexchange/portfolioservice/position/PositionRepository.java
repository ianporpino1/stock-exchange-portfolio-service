package com.stockexchange.portfolioservice.position;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface PositionRepository extends ReactiveCrudRepository<Position, UUID> {

    Flux<Position> findByPortfolioId(UUID portfolioId);

    Mono<Position> findByPortfolioIdAndSymbol(UUID portfolioId, String symbol);
}
