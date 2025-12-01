package com.stockexchange.portfolioservice.trade.handler;

import com.stockexchange.portfolioservice.trade.TradeReceptionService;
import com.stockexchange.portfolioservice.trade.event.TradeExecutedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.function.Function;

@Configuration
public class TradeHandler {
    private final TradeReceptionService tradeReceptionService;

    public TradeHandler(TradeReceptionService tradeReceptionService) {
        this.tradeReceptionService = tradeReceptionService;
    }

    @Bean
    public Function<Flux<TradeExecutedEvent>, Mono<Void>> handleTrade(){
        return flux ->
                flux.flatMap(tradeReceptionService::createPendingTransactionsForTrade).then();
    }
}
