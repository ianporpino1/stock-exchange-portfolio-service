package com.stockexchange.portfolioservice.trade;

import com.stockexchange.portfolioservice.portfolio.domain.OrderType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Table("transaction")
public class Transaction {

    @Id
    @Column("transaction_id")
    private UUID transactionId;

    private UUID tradeId;
    private UUID userId;
    private String symbol;
    private int quantity;
    private BigDecimal price;
    private OrderType orderType;
    @Column("created_at")
    private Instant createdAt;
    private TransactionStatus status;

    public Transaction(UUID transactionId, UUID tradeId, UUID userId, String symbol, int quantity, BigDecimal price, OrderType orderType, Instant createdAt, TransactionStatus status) {
        this.transactionId = transactionId;
        this.tradeId = tradeId;
        this.userId = userId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.orderType = orderType;
        this.createdAt = createdAt;
        this.status = status;
    }

    public static Transaction create(UUID tradeId, String symbol, int quantity, BigDecimal price, OrderType orderType, Instant createdAt, UUID userId) {
        return new Transaction(null, tradeId, userId, symbol, quantity, price, orderType, createdAt, TransactionStatus.PENDING);
    }

    public Transaction withStatus(TransactionStatus newStatus) {
        return new Transaction(
                this.transactionId,
                this.tradeId,
                this.userId,
                this.symbol,
                this.quantity,
                this.price,
                this.orderType,
                this.createdAt,
                newStatus
        );
    }

    public TransactionStatus getStatus() {
        return status;
    }
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
    public UUID getUserId() {
        return userId;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getTradeId() {
        return tradeId;
    }

    public void setTradeId(UUID tradeId) {
        this.tradeId = tradeId;
    }
}
