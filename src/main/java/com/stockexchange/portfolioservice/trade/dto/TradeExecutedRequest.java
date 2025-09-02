package com.stockexchange.portfolioservice.trade.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TradeExecutedRequest(UUID tradeId,
                                   UUID buyerUserId,
                                   UUID sellerUserId,
                                   String symbol,
                                   int quantity,
                                   BigDecimal price,
                                   Instant executedAt) {
}
