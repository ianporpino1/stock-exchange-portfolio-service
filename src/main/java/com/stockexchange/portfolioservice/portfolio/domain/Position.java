package com.stockexchange.portfolioservice.portfolio.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Table(name = "position")
@Entity
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID positionId;
    private String symbol;
    private int quantity;
    private BigDecimal averagePrice;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    public Position(String symbol, Portfolio portfolio) {
        this.symbol = symbol;
        this.portfolio = portfolio;
        this.quantity = 0;
        this.averagePrice = BigDecimal.ZERO;
    }

    public Position() {

    }

    public UUID getPositionId() {
        return positionId;
    }

    public void setPositionId(UUID positionId) {
        this.positionId = positionId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }
    public void processBuy(int newQuantity, BigDecimal newPrice) {
        BigDecimal oldTotalValue = this.averagePrice.multiply(BigDecimal.valueOf(this.quantity));
        BigDecimal newTotalValue = oldTotalValue.add(newPrice.multiply(BigDecimal.valueOf(newQuantity)));

        this.quantity += newQuantity;

        if (this.quantity > 0) {
            this.averagePrice = newTotalValue.divide(BigDecimal.valueOf(this.quantity), 4, RoundingMode.HALF_UP);
        }
    }
    public void processSell(int sellQuantity) {
        this.quantity -= sellQuantity;
        if (this.quantity == 0) {
            this.averagePrice = BigDecimal.ZERO;
        }
    }
}
