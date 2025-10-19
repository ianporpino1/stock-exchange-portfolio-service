package com.stockexchange.portfolioservice.trade;

import com.stockexchange.portfolioservice.trade.dto.TradeListInput;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
public class TradeController {
    private final TradeReceptionService tradeReceptionService;

    public TradeController(TradeReceptionService tradeReceptionService) {
        this.tradeReceptionService = tradeReceptionService;
    }

    @MutationMapping
    public Mono<Boolean> processTrades(@Argument("trades") TradeListInput trades) {
        return tradeReceptionService.createPendingTransactionsForTrades(trades)
                .thenReturn(true)
                .onErrorResume(_ -> Mono.just(false));
    }
}