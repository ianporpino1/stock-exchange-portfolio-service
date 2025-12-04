package com.stockexchange.portfolioservice.portfolio.handler;

import com.stockexchange.portfolioservice.portfolio.PortfolioService;
import com.stockexchange.portfolioservice.portfolio.domain.OrderType;
import com.stockexchange.portfolioservice.portfolio.event.BalanceStatus;
import com.stockexchange.portfolioservice.portfolio.event.RefundCommand;
import com.stockexchange.portfolioservice.portfolio.event.ReserveBalanceCommand;
import com.stockexchange.portfolioservice.portfolio.event.ReserveBalanceResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.function.Function;

@Configuration
public class PortfolioCommandHandler {

    private final PortfolioService portfolioService;

    public PortfolioCommandHandler(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @Bean
    public Function<Flux<Message<ReserveBalanceCommand>>, Flux<Message<ReserveBalanceResponse>>> handleReserveBalance() {
        return flux ->
                flux.filter(msg -> "portfolio.balance".equals(msg.getHeaders().get("eventType")))
                        .flatMap(message -> {
                            ReserveBalanceCommand command = message.getPayload();
                            return portfolioService.hasBalance(command.userId(), command.orderType(),
                                            command.price().multiply(BigDecimal.valueOf(command.quantity())))
                                    .map(success -> {
                                        ReserveBalanceResponse response;
                                        if (success) {
                                            response = new ReserveBalanceResponse(
                                                    command, BalanceStatus.SUCCESS
                                            );
                                        } else {
                                            response = new ReserveBalanceResponse(
                                                    command, BalanceStatus.FAILED
                                            );
                                        }
                                        return MessageBuilder.withPayload(response).build();
                                    });
                        });
    }


    @Bean
    public Function<Flux<Message<RefundCommand>>, Mono<Void>> handleRefund() {
        return flux -> flux
                .filter(msg -> "portfolio.refund".equals(msg.getHeaders().get("eventType"))).concatMap(message -> {
            RefundCommand cmd = message.getPayload();
            if (cmd.orderType() == OrderType.BUY) {
                return portfolioService.refund(cmd).then();
            } else {
                return Mono.empty();
            }
        }).then();
    }

}