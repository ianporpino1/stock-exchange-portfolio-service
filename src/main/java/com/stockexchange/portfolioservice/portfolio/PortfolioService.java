package com.stockexchange.portfolioservice.portfolio;

import com.stockexchange.portfolioservice.exception.ErrorException;
import com.stockexchange.portfolioservice.portfolio.domain.OrderType;
import com.stockexchange.portfolioservice.portfolio.domain.Portfolio;
import com.stockexchange.portfolioservice.trade.Transaction;
import com.stockexchange.portfolioservice.portfolio.dto.PortfolioResponse;
import com.stockexchange.portfolioservice.trade.dto.TradeExecutedRequest;

import com.stockexchange.portfolioservice.trade.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final TransactionRepository transactionRepository;

    public PortfolioService(PortfolioRepository portfolioRepository, TransactionRepository transactionRepository) {
        this.portfolioRepository = portfolioRepository;
        this.transactionRepository = transactionRepository;
    }

    public PortfolioResponse getPortfolioByUserId(UUID userId) {
        Portfolio portfolio = portfolioRepository.findByUserId(userId)
                .orElseThrow(() -> new ErrorException("Portfolio não encontrado para o usuário com ID: " + userId));

        return new PortfolioResponse(portfolio);
    }

    public void updatePortfolioFromTrade(TradeExecutedRequest trade) {
        if (transactionRepository.existsByTradeId(trade.tradeId())) {
            System.out.println("Trade " + trade.tradeId() + " já foi processado.");
            return;
        }
        Portfolio buyerPortfolio = portfolioRepository.findByUserId(trade.buyerUserId())
                .orElseGet(() -> createPortfolioForUser(trade.buyerUserId()));

        Portfolio sellerPortfolio = portfolioRepository.findByUserId(trade.sellerUserId())
                .orElseGet(() -> createPortfolioForUser(trade.sellerUserId()));


        Transaction buyTransaction = new Transaction(buyerPortfolio, trade.tradeId(), trade.symbol(), trade.quantity(), trade.price(), OrderType.BUY,trade.executedAt());
        Transaction sellTransaction = new Transaction(sellerPortfolio, trade.tradeId(), trade.symbol(), trade.quantity(), trade.price(), OrderType.SELL,trade.executedAt());

        buyerPortfolio.addTransaction(buyTransaction);
        sellerPortfolio.addTransaction(sellTransaction);

        buyerPortfolio.applyTransaction(trade.symbol(), trade.quantity(), trade.price(), OrderType.BUY);
        sellerPortfolio.applyTransaction(trade.symbol(), trade.quantity(), trade.price(), OrderType.SELL);

        portfolioRepository.save(buyerPortfolio);
        portfolioRepository.save(sellerPortfolio);
    }

    private Portfolio createPortfolioForUser(UUID userId) {
        Portfolio newPortfolio = new Portfolio(userId);
        return portfolioRepository.save(newPortfolio);
    }

}
