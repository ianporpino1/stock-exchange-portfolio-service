package com.stockexchange.portfolioservice.trade.dto;

import java.util.List;

public record TradeListResponse(List<TradeResponse> trades) {}
