package com.stockexchange.portfolioservice.trade;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import java.util.UUID;

public interface TransactionRepository extends ReactiveCrudRepository<Transaction, UUID> {

    @Query("SELECT * FROM transaction WHERE status = 'PENDING' ORDER BY created_at ASC LIMIT 100")
    Flux<Transaction> findTop100PendingForUpdate();
}
