package com.stockexchange.portfolioservice.portfolio.handler;

import com.stockexchange.portfolioservice.portfolio.PortfolioService;
import com.stockexchange.portfolioservice.portfolio.domain.OrderType;
import com.stockexchange.portfolioservice.portfolio.event.BalanceEvent;
import com.stockexchange.portfolioservice.portfolio.event.OrderEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Instant;
import java.util.function.Function;

@Configuration
public class OrderHandler {
    private final PortfolioService portfolioService;

    public OrderHandler(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @Bean
    public Function<Flux<Message<OrderEvent.OrderCreated>>, Flux<Message<BalanceEvent>>> handleOrderCreated() {
        return flux -> flux
                .filter(msg -> "order.created".equals(msg.getHeaders().get("eventType")))
                .flatMap(message -> {
            OrderEvent.OrderCreated order = message.getPayload();
            return portfolioService.hasBalance(order)
                    .map(hasBalance -> {
                        BalanceEvent event;
                        String routingKey;

                        if (hasBalance) {
                            event = new BalanceEvent.BalanceReserved(
                                    order.orderId(), order.userId(), order.symbol(),
                                    order.price(), order.quantity(), order.orderType(), Instant.now()
                            );
                            routingKey = "balance.reserved";
                        } else {
                            event = new BalanceEvent.BalanceReservationFailed(
                                    order.orderId(), order.userId(), order.symbol(),
                                    order.price(), order.quantity(), order.orderType(), Instant.now()
                            );
                            routingKey = "balance.failed";
                        }

                        return MessageBuilder.withPayload(event)
                                .setHeader("eventType", routingKey)
                                .build();
                    });
        });
    }

    @Bean
    public Function<Flux<Message<OrderEvent.OrderRejected>>, Mono<Void>> handleOrderRejected() {
        return flux -> flux
                .filter(msg -> "order.rejected".equals(msg.getHeaders().get("eventType")))
                .concatMap(message -> {
            OrderEvent.OrderRejected event = message.getPayload();
            return portfolioService.refund(event);
        }).then();
    }
}
