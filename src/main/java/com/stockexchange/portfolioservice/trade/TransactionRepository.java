package com.stockexchange.portfolioservice.trade;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    boolean existsByTradeId(UUID tradeId);
}
