package com.stockexchange.portfolioservice.portfolio.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.util.*;

@Table(name = "portfolio")
public class Portfolio {
    @Id
    private UUID portfolioId;
    private final UUID userId;
    private final BigDecimal cashBalance;

    public Portfolio(UUID portfolioId, UUID userId, BigDecimal cashBalance) {
        this.portfolioId = portfolioId;
        this.userId = userId;
        this.cashBalance = cashBalance;
    }

    public static Portfolio create(UUID userId) {
        return new Portfolio(null, userId, new BigDecimal("100000.00"));
    }

    public UUID getPortfolioId() { return portfolioId; }
    public UUID getUserId() { return userId; }
    public BigDecimal getCashBalance() { return cashBalance; }
    public Portfolio withUpdatedBalance(BigDecimal amount) {
        return new Portfolio(this.portfolioId, this.userId, this.cashBalance.add(amount));
    }
}

