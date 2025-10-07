package com.stockexchange.portfolioservice.portfolio.dto;

import com.stockexchange.portfolioservice.portfolio.domain.Portfolio;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PortfolioResponse(
        UUID portfolioId,
        UUID userId,
        BigDecimal cashBalance,
        List<PositionResponse> positions
) {
    public PortfolioResponse(Portfolio portfolio) {
        this(
                portfolio.getPortfolioId(),
                portfolio.getUserId(),
                portfolio.getCashBalance(),
                portfolio.getPositions().stream()
                        .map(position -> new PositionResponse(
                                position.getSymbol(),
                                position.getQuantity(),
                                position.getAveragePrice()
                        )).toList()
        );
    }
}
