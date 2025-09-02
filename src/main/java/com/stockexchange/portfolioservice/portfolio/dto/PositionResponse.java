package com.stockexchange.portfolioservice.portfolio.dto;

import java.math.BigDecimal;

public record PositionResponse(
        String symbol,
        int quantity,
        BigDecimal averagePrice
) {
}
