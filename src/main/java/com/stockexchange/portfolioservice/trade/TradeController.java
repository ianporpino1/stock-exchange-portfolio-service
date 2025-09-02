package com.stockexchange.portfolioservice.trade;

import com.stockexchange.portfolioservice.portfolio.PortfolioService;
import com.stockexchange.portfolioservice.trade.dto.TradeExecutedRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trades")
public class TradeController {
    private final PortfolioService portfolioService;

    public TradeController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping("/trades")
    public ResponseEntity<Void> processTrade(@RequestBody TradeExecutedRequest tradeExecutedRequest) {
        portfolioService.updatePortfolioFromTrade(tradeExecutedRequest);
        return ResponseEntity.ok().build();
    }
}
