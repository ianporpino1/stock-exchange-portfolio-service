package com.stockexchange.portfolioservice.position;

import java.math.BigDecimal;

public record PositionResponse(
        String symbol,
        int quantity,
        BigDecimal averagePrice
) {
    public PositionResponse(Position position) {
        this(position.getSymbol(), position.getQuantity(), position.getAveragePrice());
    }
}
