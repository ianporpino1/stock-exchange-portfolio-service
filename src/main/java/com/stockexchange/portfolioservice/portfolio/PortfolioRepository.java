package com.stockexchange.portfolioservice.portfolio;

import com.stockexchange.portfolioservice.portfolio.domain.Portfolio;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

public interface PortfolioRepository extends ReactiveCrudRepository<Portfolio,UUID> {

    Mono<Portfolio> findByUserId(UUID userId);
    @Modifying
    @Query("""
                UPDATE portfolio 
                SET cash_balance = cash_balance - :amount, 
                    blocked_balance = blocked_balance + :amount 
                WHERE user_id = :userId 
                  AND cash_balance >= :amount
            """)
    Mono<Integer> reserveCash(UUID userId, BigDecimal amount);

    @Modifying
    @Query("""
        UPDATE portfolio 
        SET cash_balance = cash_balance + :amount, 
            blocked_balance = blocked_balance - :amount 
        WHERE user_id = :userId 
          AND blocked_balance >= :amount
    """)
    Mono<Integer> refundCash(UUID userId, BigDecimal amount);
}
