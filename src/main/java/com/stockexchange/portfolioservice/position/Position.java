package com.stockexchange.portfolioservice.position;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Table(name = "position")
public class Position {
    @Id
    private UUID positionId;
    private String symbol;
    private int quantity;
    private BigDecimal averagePrice;
    @Column("portfolio_id")
    private UUID portfolioId;

    public Position(UUID positionId, String symbol, int quantity, BigDecimal averagePrice, UUID portfolioId) {
        this.positionId = positionId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
        this.portfolioId = portfolioId;
    }
    public static Position create(String symbol, UUID portfolioId) {
        return new Position(null, symbol, 0, BigDecimal.ZERO, portfolioId);
    }

    public UUID getPositionId() { return positionId; }
    public String getSymbol() { return symbol; }
    public int getQuantity() { return quantity; }
    public BigDecimal getAveragePrice() { return averagePrice; }
    public UUID getPortfolioId() { return portfolioId; }

    public Position withBuy(int buyQuantity, BigDecimal buyPrice) {
        BigDecimal currentQuantity = BigDecimal.valueOf(this.quantity);
        BigDecimal buyQty = BigDecimal.valueOf(buyQuantity);

        BigDecimal totalCost = this.averagePrice.multiply(currentQuantity)
                .add(buyPrice.multiply(buyQty));

        BigDecimal newQuantity = currentQuantity.add(buyQty);

        BigDecimal newAveragePrice = BigDecimal.ZERO;
        if (newQuantity.compareTo(BigDecimal.ZERO) > 0) {
            newAveragePrice = totalCost.divide(newQuantity, 2, RoundingMode.HALF_UP);
        }

        return new Position(
                this.positionId,
                this.symbol,
                newQuantity.intValue(),
                newAveragePrice,
                this.portfolioId
        );
    }

    public Position withSell(int sellQuantity) {
        int updatedQuantity = this.quantity - sellQuantity;
        return new Position(this.positionId, this.symbol, updatedQuantity, this.averagePrice, this.portfolioId);
    }
}
