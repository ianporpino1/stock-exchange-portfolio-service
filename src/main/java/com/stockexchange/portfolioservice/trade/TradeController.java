package com.stockexchange.portfolioservice.trade;

import com.stockexchange.portfolioservice.trade.dto.TradeListResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/trades")
public class TradeController {
    private final TradeReceptionService tradeReceptionService;

    public TradeController(TradeReceptionService tradeReceptionService) {
        this.tradeReceptionService = tradeReceptionService;
    }

    @PostMapping
    public Mono<Void> processTrades(@RequestBody TradeListResponse trades) {
        return tradeReceptionService.createPendingTransactionsForTrades(trades);
    }
}
