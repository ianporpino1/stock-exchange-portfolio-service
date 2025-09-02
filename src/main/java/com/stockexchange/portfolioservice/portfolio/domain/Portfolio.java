package com.stockexchange.portfolioservice.portfolio.domain;

import com.stockexchange.portfolioservice.exception.ErrorException;
import com.stockexchange.portfolioservice.trade.Transaction;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "portfolio")
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID portfolioId;
    private UUID userId;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Position> positions;
    private BigDecimal cashBalance;
    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    public Portfolio(UUID userId) {
        this.userId = userId;
        this.positions = new ArrayList<>();
        this.cashBalance = BigDecimal.valueOf(100000);
        this.transactions = new ArrayList<>();
    }

    public Portfolio() {

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

    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
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
        position.processSell(quantity);

        if (position.getQuantity() == 0) {
            this.positions.remove(position);
        }
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }
}

