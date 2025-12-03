package com.stockexchange.portfolioservice.portfolio.event;

import com.stockexchange.portfolioservice.portfolio.domain.OrderType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public interface BalanceEvent extends BaseEvent{
    record BalanceReserved(UUID orderId,
                           UUID userId,
                           String symbol,
                           BigDecimal price,
                           int quantity,
                           OrderType orderType,
                           Instant createdAt)implements BalanceEvent{
        @Override
        public UUID id() {
            return orderId;
        }
    }

    record BalanceReservationFailed(UUID orderId,
                               UUID userId,
                               String symbol,
                               BigDecimal price,
                               int quantity,
                               OrderType orderType,
                               Instant createdAt)implements BalanceEvent{
        @Override
        public UUID id() {
            return orderId;
        }
    }
}
