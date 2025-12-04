package com.stockexchange.portfolioservice.portfolio.event;

import com.stockexchange.portfolioservice.portfolio.domain.OrderType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ReserveBalanceResponse(UUID orderId,
                                     UUID userId,
                                     String symbol,
                                     BigDecimal price,
                                     int quantity,
                                     OrderType orderType,
                                     Instant createdAt,
                                     BalanceStatus balanceStatus) {
    public ReserveBalanceResponse(ReserveBalanceCommand command, BalanceStatus balanceStatus) {
        this(command.orderId(), command.userId(), command.symbol(), command.price(), command.quantity(), command.orderType(), command.createdAt(), balanceStatus);
    }
}