package com.stockexchange.portfolioservice.portfolio.event;


import com.stockexchange.portfolioservice.portfolio.domain.OrderType;
import java.math.BigDecimal;
import java.util.UUID;

public record RefundCommand(UUID orderId,
                            UUID userId,
                            String symbol,
                            BigDecimal amount,
                            OrderType orderType) {
}
