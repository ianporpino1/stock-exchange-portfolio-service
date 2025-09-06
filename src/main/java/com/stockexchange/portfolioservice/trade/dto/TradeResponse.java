package com.stockexchange.portfolioservice.trade.dto;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TradeResponse(UUID tradeId,
                            UUID buyOrderId,
                            UUID sellOrderId,
                            UUID buyerUserId,
                            UUID sellerUserId,
                            String symbol,
                            int quantity,
                            BigDecimal price,
                            Instant executedAt
) {
}
