package com.stockexchange.portfolioservice.portfolio.domain;

import com.stockexchange.portfolioservice.exception.ErrorException;
import com.stockexchange.portfolioservice.trade.Transaction;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "portfolio")
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID portfolioId;
    @Column(nullable = false, unique = true)
    private UUID userId;
    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Position> positions = new HashSet<>();
    private BigDecimal cashBalance;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "portfolio_id")
    private Set<Transaction> transactions;

    public Portfolio(UUID userId) {
        this.userId = userId;
        this.positions = new HashSet<>();
        this.cashBalance = BigDecimal.valueOf(100000);
        this.transactions = new HashSet<>();
    }

    public Portfolio() {

    }
    public void addPosition(Position position) {
        positions.add(position);
        position.setPortfolio(this);
    }
    public UUID getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(UUID portfolioId) {
        this.portfolioId = portfolioId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    public Set<Position> getPositions() {
        return positions;
    }
    public void setPositions(Set<Position> positions) {
        this.positions = positions;
    }

    public BigDecimal getCashBalance() {
        return cashBalance;
    }
    public void setCashBalance(BigDecimal cashBalance) {
        this.cashBalance = cashBalance;
    }

    public void applyTransaction(String symbol, int quantity, BigDecimal price, OrderType orderType) {
        Position position = this.positions.stream()
                .filter(p -> p.getSymbol().equals(symbol))
                .findFirst()
                .orElseGet(() -> {
                    Position newPosition = new Position(symbol, this);
                    this.positions.add(newPosition);
                    return newPosition;
                });

        switch (orderType) {
            case BUY -> applyBuyLogic(quantity, price, position);
            case SELL -> applySellLogic(quantity, price, position);
        }
    }

    private void applyBuyLogic(int quantity, BigDecimal price, Position position) {
        BigDecimal totalCost = price.multiply(BigDecimal.valueOf(quantity));

        if (this.cashBalance.compareTo(totalCost) < 0) {
            throw new ErrorException("Saldo insuficiente.");
        }

        this.cashBalance = this.cashBalance.subtract(totalCost);
        position.processBuy(quantity, price);
    }
    private void applySellLogic(int quantity, BigDecimal price, Position position) {
        BigDecimal totalCredit = price.multiply(BigDecimal.valueOf(quantity));

        this.cashBalance = this.cashBalance.add(totalCredit);
        position.processSell(quantity, price);

        if (position.getQuantity() == 0) {
            this.positions.remove(position);
        }
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }
}

