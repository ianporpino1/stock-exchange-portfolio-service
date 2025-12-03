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
    private final BigDecimal blockedBalance;

    public Portfolio(UUID portfolioId, UUID userId, BigDecimal cashBalance,BigDecimal blockedBalance) {
        this.portfolioId = portfolioId;
        this.userId = userId;
        this.cashBalance = cashBalance;
        this.blockedBalance = blockedBalance != null ? blockedBalance : BigDecimal.ZERO;
    }

    public static Portfolio create(UUID userId) {
        return new Portfolio(null, userId, new BigDecimal("100000.00"),BigDecimal.ZERO);
    }

    public UUID getPortfolioId() { return portfolioId; }
    public UUID getUserId() { return userId; }
    public BigDecimal getCashBalance() { return cashBalance; }
    public BigDecimal getBlockedBalance() { return blockedBalance; }
    public Portfolio withUpdatedBalance(BigDecimal amountToAdd) {
        return new Portfolio(
                this.portfolioId,
                this.userId,
                this.cashBalance.add(amountToAdd),
                this.blockedBalance
        );
    }

    public Portfolio withBlockedBalance(BigDecimal newBlockedBalance) {
        return new Portfolio(
                this.portfolioId,
                this.userId,
                this.cashBalance,
                newBlockedBalance
        );
    }

    public Portfolio withBuySettled(BigDecimal amountSpent) {
        return this.withBlockedBalance(
                this.blockedBalance.subtract(amountSpent)
        );
    }

    public Portfolio withSellSettled(BigDecimal amountReceived) {
        return this.withUpdatedBalance(amountReceived);
    }

}

