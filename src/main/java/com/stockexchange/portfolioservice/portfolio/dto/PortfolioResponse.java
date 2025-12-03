package com.stockexchange.portfolioservice.portfolio.dto;

import com.stockexchange.portfolioservice.portfolio.domain.Portfolio;
import com.stockexchange.portfolioservice.position.PositionResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PortfolioResponse(
        UUID portfolioId,
        UUID userId,
        BigDecimal cashBalance,
        List<PositionResponse> positions
) {
    public PortfolioResponse(Portfolio portfolio, List<PositionResponse> positions) {
        this(
                portfolio.getPortfolioId(),
                portfolio.getUserId(),
                portfolio.getCashBalance(),
                positions
        );
    }
}
